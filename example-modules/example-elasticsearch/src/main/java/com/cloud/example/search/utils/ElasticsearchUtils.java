package com.cloud.example.search.utils;

import com.alibaba.fastjson.JSONObject;
import com.cloud.example.core.exception.CommonException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type elasticsearch utils
 *
 * @author Benji
 * @date 2019-08-13
 */
@Component
public class ElasticsearchUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchUtils.class);

    private static final Integer INDEX_NUMBER_OF_SHARDS = 3;

    private static final Integer INDEX_NUMBER_OF_REPLICAS = 2;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static RestHighLevelClient client;

    @PostConstruct
    public void init() {
        client = this.restHighLevelClient;
    }

    /**
     * 创建索引（建议手工建立索引和映射）
     *
     * @param index 索引名
     * @return 创建是否成功
     * @throws IOException 异常信息
     */
    public static boolean createIndex(String index) throws IOException {

        if (existsIndex(index)) {
            return false;
        }

        CreateIndexRequest request = new CreateIndexRequest(index);

        request.settings(Settings.builder()
                .put("index.number_of_shards", INDEX_NUMBER_OF_SHARDS)
                .put("index.number_of_replicas", INDEX_NUMBER_OF_REPLICAS)
        );

        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        boolean acknowledged = createIndexResponse.isAcknowledged();

        LOGGER.info("Create index successfully? {}", acknowledged);

        return acknowledged;
    }

    /**
     * 创建索引映射（XContentBuilder object）（建议手工建立索引和映射）
     *
     * @param index    索引名
     * @param proNames 字段参数集
     * @return 创建是否成功
     * @throws Exception
     */
    public static boolean createMapping(String index, Map<String, Map<String, String>> proNames) throws Exception {

        if (!existsIndex(index)) {
            return false;
        }

        PutMappingRequest request = new PutMappingRequest(index);

        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("dynamic", "strict").startObject("properties");

        if (!proNames.isEmpty()) {
            for (Map.Entry<String, Map<String, String>> proName : proNames.entrySet()) {
                if (null != proName.getKey()) {
                    builder.startObject(proName.getKey());

                    if (!proName.getValue().isEmpty()) {

                        for (Map.Entry<String, String> filed : proName.getValue().entrySet()) {
                            String pro = filed.getKey();
                            String value = filed.getValue();

                            if (null != pro && null != value) {
                                builder.field(pro, value);
                            }
                        }
                    }

                    builder.endObject();
                }
            }
        }
        builder.endObject().endObject();
        request.source(builder);

        AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);

        boolean acknowledged = putMappingResponse.isAcknowledged();

        LOGGER.info("Create mapping successfully? {}", acknowledged);

        return acknowledged;
    }

    /**
     * 删除索引
     *
     * @param index 索引名
     * @return 执行是否成功
     * @throws IOException 异常信息
     */
    public static boolean deleteIndex(String index) throws IOException {

        if (!existsIndex(index)) {
            return false;
        }

        DeleteIndexRequest request = new DeleteIndexRequest(index);

        request.indicesOptions(IndicesOptions.lenientExpandOpen());

        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);

        boolean acknowledged = response.isAcknowledged();

        LOGGER.info("Delete index successfully? {}", acknowledged);

        return acknowledged;
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引名
     * @return 索引是否存在
     * @throws IOException 异常信息
     */
    public static boolean existsIndex(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);

        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        if (exists) {
            LOGGER.info("The index [{}] is exist!", index);
        } else {
            LOGGER.info("The index [{}] is not exist!", index);
        }

        return exists;
    }

    /**
     * The type Create document in ES
     *
     * @param jsonObject The JSON Object Data String
     * @param index      The index
     * @param id         The document id
     * @return id
     * @throws IOException IOException
     */
    public static String createDocument(JSONObject jsonObject, String index, String id) throws IOException {

        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(jsonObject, XContentType.JSON);

        // can be create or update (default) change only create
        request.opType(DocWriteRequest.OpType.CREATE);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        LOGGER.info("Create document response status: {}, id: {}", response.status().getStatus(), response.getId());

        return response.getId();
    }

    /**
     * The type Create document in ES and auto create documentId
     *
     * @param jsonObject The JSON Object Data String
     * @param index      The index
     * @return id
     * @throws IOException IOException
     */
    public static String createDocument(JSONObject jsonObject, String index) throws IOException {
        return createDocument(jsonObject, index, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * The type Update document by id
     *
     * @param jsonObject The JSON Object Data String
     * @param index      The index
     * @param id         id
     * @throws IOException IOException
     */
    public static void updateDocumentById(JSONObject jsonObject, String index, String id) throws IOException {
        UpdateRequest request = new UpdateRequest(index, id);

        request.doc(jsonObject, XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);

        LOGGER.info("Update document response status: {}, id: {}", response.status().getStatus(), response.getId());
    }

    /**
     * The type batch create document
     *
     * @param batchJsonObject the batch JsonObject data
     * @param index           the index
     * @param ids             the ids
     * @throws IOException IOException
     */
    public static void batchCreateDocument(List<JSONObject> batchJsonObject, String index, String... ids) throws IOException {
        BulkRequest request = new BulkRequest();

        if (batchJsonObject == null || batchJsonObject.isEmpty()) {
            throw new CommonException("List is empty.");
        }

        //// batchJsonObject.forEach(e -> request.add(new IndexRequest(index).id(ids[0]).source(e, XContentType.JSON)));

        ForEachUtils.forEach(0, batchJsonObject, (_index, _item) -> {
            request.add(new IndexRequest(index).id(ids[_index]).source(_item, XContentType.JSON));
        });

        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);

        LOGGER.info("Batch create document response status: {}, name: {}", responses.status().getStatus(), responses.status().name());

    }


}