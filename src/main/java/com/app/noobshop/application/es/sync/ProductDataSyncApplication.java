package com.app.noobshop.application.es.sync;

import com.app.noobshop.application.common.DbUtil;
import com.app.noobshop.application.common.EsClientUtil;
import com.app.noobshop.infrastructure.es.common.mapstruct.EsCopyMapper;
import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.infrastructure.es.index.EsIndexEnum;
import com.app.noobshop.mapper.ProductMapper;
import com.app.noobshop.pojo.entity.Product;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Slf4j
public class ProductDataSyncApplication {

    private static final String ES_INDEX = EsIndexEnum.PRODUCT.getIndexName();
    private static final EsCopyMapper ES_COPY_MAPPER = Mappers.getMapper(EsCopyMapper.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory = DbUtil.getSqlSessionFactory();

        try (SqlSession session = sqlSessionFactory.openSession(true);
             EsClientUtil.EsClientResource esClientResource = EsClientUtil.openEsClient()) {

            ProductMapper productMapper = session.getMapper(ProductMapper.class);
            List<Product> productList = productMapper.selectList(
                    Wrappers.lambdaQuery(Product.class).orderByAsc(Product::getId)
            );

            if (productList.isEmpty()) {
                System.out.println("MySQL product table has no data. Sync skipped.");
                return;
            }

            List<ProductDocument> productDocumentList = productList.stream()
                    .map(ES_COPY_MAPPER::ProductToProductDocument)
                    .toList();

            bulkSyncToEs(esClientResource.client(), productDocumentList);
            System.out.println("Sync to ES succeeded. Product count: " + productList.size());
        } catch (Exception e) {
            log.error("Product data sync to ES failed", e);
        }
    }

    private static void bulkSyncToEs(RestHighLevelClient esClient, List<ProductDocument> esList) throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        for (ProductDocument productDocument : esList) {
            bulkRequest.add(new IndexRequest(ES_INDEX)
                    .id(productDocument.getId().toString())
                    .source(OBJECT_MAPPER.writeValueAsString(productDocument), XContentType.JSON));
        }
        esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
