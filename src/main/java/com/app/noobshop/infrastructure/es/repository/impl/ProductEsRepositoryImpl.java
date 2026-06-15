package com.app.noobshop.infrastructure.es.repository.impl;

import com.app.noobshop.aop.annotation.common.ParamCheckAnnotation;
import com.app.noobshop.infrastructure.es.document.ProductDocument;
import com.app.noobshop.infrastructure.es.index.EsIndexEnum;
import com.app.noobshop.infrastructure.es.repository.ProductEsRepository;
import com.app.noobshop.pojo.emums.CommonSortTypeEnum;
import com.app.noobshop.pojo.emums.CommonStatus;
import com.app.noobshop.pojo.emums.ProductSortTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ProductEsRepositoryImpl implements ProductEsRepository {

    private static final String PRODUCT_INDEX = EsIndexEnum.PRODUCT.getIndexName();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final RestHighLevelClient esClient;

    @Override
    public ProductDocument getById(Long id) {
        try {
            GetResponse response = esClient.get(new GetRequest(PRODUCT_INDEX, id.toString()), RequestOptions.DEFAULT);
            if (!response.isExists()) {
                return null;
            }
            return toProductDocument(response.getSourceAsString());
        } catch (IOException e) {
            throw new RuntimeException("Query product document by id failed: " + id, e);
        }
    }

    @Override
    public List<ProductDocument> getById(Long id, Long... ids) {
        List<Long> idList = new ArrayList<>(Arrays.asList(ids));
        idList.add(id);
        return getByIdList(idList);
    }

    @Override
    public List<ProductDocument> getByIdList(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return List.of();
        }

        String[] ids = idList.stream().map(String::valueOf).toArray(String[]::new);
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.idsQuery().addIds(ids))
                .size(idList.size());
        return search(source);
    }

    @Override
    public Long getMaxId() {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .sort(ProductDocument.Fields.id, SortOrder.DESC)
                .size(1)
                .fetchSource(new String[]{ProductDocument.Fields.id}, null);

        List<ProductDocument> documents = search(source);
        if (documents.isEmpty()) {
            return 0L;
        }
        ProductDocument document = documents.get(0);
        if (document == null || document.getId() == null) {
            throw new RuntimeException("ES product_index max id document is invalid");
        }
        return document.getId();
    }

    @Override
    public void save(ProductDocument document) {
        if (document == null || document.getId() == null) {
            throw new IllegalArgumentException("Product document id can not be empty");
        }
        try {
            esClient.index(toIndexRequest(document), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Save product document to ES failed, id: " + document.getId(), e);
        }
    }

    @Override
    public void batchSave(List<ProductDocument> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }

        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (ProductDocument document : documents) {
                if (document == null || document.getId() == null) {
                    throw new IllegalArgumentException("Product document id can not be empty");
                }
                bulkRequest.add(toIndexRequest(document));
            }
            esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Batch save product documents to ES failed", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product document id can not be empty");
        }
        try {
            esClient.delete(new DeleteRequest(PRODUCT_INDEX, id.toString()), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Delete product document from ES failed, id: " + id, e);
        }
    }

    @Override
    public List<ProductDocument> searchByName(String name) {
        if (StringUtils.isBlank(name)) {
            return List.of();
        }
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(activeProductQuery(QueryBuilders.matchQuery(ProductDocument.Fields.name, name)));
        return search(source);
    }

    @Override
    public List<ProductDocument> searchByName(String name, Integer limit) {
        if (StringUtils.isBlank(name)) {
            return List.of();
        }
        if (limit == null || limit <= 0) {
            throw new RuntimeException("Invalid ES query limit: " + limit);
        }
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(activeProductQuery(QueryBuilders.matchQuery(ProductDocument.Fields.name, name)))
                .size(limit);
        return search(source);
    }

    @Override
    public List<ProductDocument> searchLimitAfterId(Integer limit, Long productId) {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(activeProductQuery(QueryBuilders.rangeQuery(ProductDocument.Fields.id).gt(productId)))
                .sort(ProductDocument.Fields.id, SortOrder.ASC)
                .size(limit);
        return search(source);
    }

    @Override
    @ParamCheckAnnotation
    public List<ProductDocument> searchLimitByProductSortTypeAndCategoryId(
            ProductSortTypeEnum productSortTypeEnum,
            Long categoryId,
            Integer limit
    ) {
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.termQuery(ProductDocument.Fields.categoryId, categoryId)));
        return search(source);
    }

    @Override
    public List<ProductDocument> searchCursorByProductSortTypeAndCategoryId(
            ProductSortTypeEnum productSortTypeEnum,
            Long categoryId,
            Integer limit,
            String sortValue,
            Long productId
    ) {
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.termQuery(ProductDocument.Fields.categoryId, categoryId)))
                .searchAfter(searchAfterValues(productSortTypeEnum, sortValue, productId));
        return search(source);
    }

    @Override
    @ParamCheckAnnotation
    public List<ProductDocument> searchLimitByProductSortTypeAndCategoryIdList(
            ProductSortTypeEnum productSortTypeEnum,
            List<Long> categoryIdList,
            Integer limit
    ) {
        if (CollectionUtils.isEmpty(categoryIdList)) {
            return List.of();
        }
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.termsQuery(ProductDocument.Fields.categoryId, categoryIdList)));
        return search(source);
    }

    @Override
    @ParamCheckAnnotation
    public List<ProductDocument> searchCursorByProductSortTypeAndCategoryIdList(
            ProductSortTypeEnum productSortTypeEnum,
            List<Long> categoryIdList,
            Integer limit,
            String sortValue,
            Long productId
    ) {
        if (CollectionUtils.isEmpty(categoryIdList)) {
            return List.of();
        }
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.termsQuery(ProductDocument.Fields.categoryId, categoryIdList)))
                .searchAfter(searchAfterValues(productSortTypeEnum, sortValue, productId));
        return search(source);
    }

    @Override
    public List<ProductDocument> searchLimitByProductSortTypeAndProductName(
            ProductSortTypeEnum productSortTypeEnum,
            String keyword,
            Integer limit
    ) {
        if (StringUtils.isBlank(keyword)) {
            return Collections.emptyList();
        }
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.matchQuery(ProductDocument.Fields.name, keyword).fuzziness("AUTO")));
        return search(source);
    }

    @Override
    public List<ProductDocument> searchCursorByProductSortTypeAndProductName(
            ProductSortTypeEnum productSortTypeEnum,
            String keyword,
            Integer limit,
            String sortValue,
            Long productId
    ) {
        if (StringUtils.isBlank(keyword)) {
            return Collections.emptyList();
        }
        SearchSourceBuilder source = baseSortedSource(productSortTypeEnum, limit)
                .query(activeProductQuery(QueryBuilders.matchQuery(ProductDocument.Fields.name, keyword).fuzziness("AUTO")))
                .searchAfter(searchAfterValues(productSortTypeEnum, sortValue, productId));
        return search(source);
    }

    @Override
    public List<ProductDocument> searchLimitOrderByField(
            Integer limit,
            String fieldName,
            CommonSortTypeEnum commonSortTypeEnum
    ) {
        if (StringUtils.isBlank(fieldName) || Objects.isNull(limit) || Objects.isNull(commonSortTypeEnum)) {
            return Collections.emptyList();
        }
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(activeProductQuery(null))
                .sort(fieldName, toSortOrder(commonSortTypeEnum))
                .size(limit);
        return search(source);
    }

    private SearchSourceBuilder baseSortedSource(ProductSortTypeEnum productSortTypeEnum, Integer limit) {
        return new SearchSourceBuilder()
                .sort(productSortTypeEnum.getSortField(), toSortOrder(productSortTypeEnum.getCommonSortTypeEnum()))
                .sort(ProductDocument.Fields.id, SortOrder.ASC)
                .size(limit);
    }

    private BoolQueryBuilder activeProductQuery(QueryBuilder queryBuilder) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(ProductDocument.Fields.status, CommonStatus.ACTIVE.getNumber()));
        if (queryBuilder != null) {
            boolQuery.must(queryBuilder);
        }
        return boolQuery;
    }

    private List<ProductDocument> search(SearchSourceBuilder source) {
        try {
            SearchRequest request = new SearchRequest(PRODUCT_INDEX);
            request.source(source);
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            List<ProductDocument> documents = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                documents.add(toProductDocument(hit.getSourceAsString()));
            }
            return documents;
        } catch (IOException e) {
            throw new RuntimeException("Search product documents from ES failed", e);
        }
    }

    private IndexRequest toIndexRequest(ProductDocument document) throws JsonProcessingException {
        return new IndexRequest(PRODUCT_INDEX)
                .id(document.getId().toString())
                .source(OBJECT_MAPPER.writeValueAsString(document), XContentType.JSON);
    }

    private ProductDocument toProductDocument(String source) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(source, ProductDocument.class);
    }

    private SortOrder toSortOrder(CommonSortTypeEnum commonSortTypeEnum) {
        return commonSortTypeEnum.isAsc() ? SortOrder.ASC : SortOrder.DESC;
    }

    private Object[] searchAfterValues(ProductSortTypeEnum productSortTypeEnum, String sortValue, Long productId) {
        return new Object[]{
                convertSortValue(productSortTypeEnum, sortValue),
                productId
        };
    }

    private Object convertSortValue(ProductSortTypeEnum productSortTypeEnum, String sortValue) {
        if (StringUtils.isBlank(sortValue)) {
            return sortValue;
        }
        String sortField = productSortTypeEnum.getSortField();
        if (ProductDocument.Fields.price.equals(sortField)) {
            return new BigDecimal(sortValue);
        }
        if (ProductDocument.Fields.salesCount.equals(sortField)
                || ProductDocument.Fields.viewCount.equals(sortField)
                || ProductDocument.Fields.id.equals(sortField)) {
            return Long.valueOf(sortValue);
        }
        return sortValue;
    }
}
