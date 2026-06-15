package com.app.noobshop.infrastructure.es.index.EsIndexInitializerService.impl;

import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.infrastructure.es.index.EsIndexEnum;
import com.app.noobshop.infrastructure.es.index.EsIndexInitializerService.EsIndexInitializerService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

@Slf4j
public class EsIndexInitializerServiceImpl implements EsIndexInitializerService {

    private final RestHighLevelClient elasticsearchClient;

    public EsIndexInitializerServiceImpl(RestHighLevelClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public void initProductIndex() {
        String indexName = EsIndexEnum.PRODUCT.getIndexName();
        try {
            boolean exists = elasticsearchClient.indices()
                    .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);

            if (exists) {
                log.info("ES index [{}] already exists, skipped", indexName);
                return;
            }

            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
            );
            request.mapping(buildProductMapping());

            elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("ES index [{}] initialized", indexName);
        } catch (IOException e) {
            log.error("ES index [{}] initialization failed", indexName, e);
            throw new RuntimeException("Product ES index initialization failed", e);
        }
    }

    private XContentBuilder buildProductMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        builder.startObject("properties");
        builder.startObject(ProductDocument.Fields.id).field("type", "long").endObject();
        builder.startObject(ProductDocument.Fields.categoryId).field("type", "long").endObject();
        builder.startObject(ProductDocument.Fields.name)
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .field("search_analyzer", "ik_smart")
                .startObject("fields")
                .startObject("keyword").field("type", "keyword").field("ignore_above", 256).endObject()
                .endObject()
                .endObject();
        builder.startObject(ProductDocument.Fields.image).field("type", "keyword").endObject();
        builder.startObject(ProductDocument.Fields.sellPoint)
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .field("search_analyzer", "ik_smart")
                .endObject();
        builder.startObject(ProductDocument.Fields.price)
                .field("type", "scaled_float")
                .field("scaling_factor", 100)
                .endObject();
        builder.startObject(ProductDocument.Fields.status).field("type", "integer").endObject();
        builder.startObject(ProductDocument.Fields.viewCount).field("type", "long").endObject();
        builder.startObject(ProductDocument.Fields.salesCount).field("type", "long").endObject();
        builder.startObject(ProductDocument.Fields.createTime).field("type", "date").endObject();
        builder.startObject(ProductDocument.Fields.updateTime).field("type", "date").endObject();
        builder.endObject();
        builder.endObject();
        return builder;
    }
}
