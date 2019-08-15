package com.cloud.example.search;

import com.alibaba.fastjson.JSONObject;
import com.cloud.example.search.utils.ElasticsearchUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * ElasticsearchTest
 *
 * @author Benji
 * @date 2019-08-14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ElasticsearchTest {

    private final String INDEX_NAME = "test";

    // ElasticsearchStatusException[Elasticsearch exception [type=invalid_index_name_exception,
    // reason=Invalid index name [indexTest], must be lowercase]
    @Test
    public void createIndexTest() throws Exception {
        boolean createIndex = ElasticsearchUtils.createIndex(INDEX_NAME);

        System.out.println(createIndex);
    }

    @Test
    public void createMappingTest() throws Exception {
        Map<String, Map<String, String>> proNames = Maps.newHashMap();

        Map<String, String> messageFields = Maps.newHashMap();
        messageFields.put("type", "keyword");
        //messageFields.put("analyzer", "ik_max_word");
        messageFields.put("index", "true");

        proNames.put("name", messageFields);

        boolean createMapping = ElasticsearchUtils.createMapping(INDEX_NAME, proNames);

        System.out.println(createMapping);
    }

    @Test
    public void deleteIndexTest() throws Exception {
        boolean deleteIndex = ElasticsearchUtils.deleteIndex(INDEX_NAME);

        System.out.println(deleteIndex);
    }

    @Test
    public void existsIndexTest() throws Exception {

        boolean existsIndex = ElasticsearchUtils.existsIndex(INDEX_NAME);

        System.out.println(existsIndex);

    }

    @Test
    public void createDocumentTest() throws Exception {

        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String id = ElasticsearchUtils.createDocument(jsonObject, INDEX_NAME, "1");

        System.out.println(id);

    }

    @Test
    public void createDocumentAutoTest() throws Exception {

        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String id = ElasticsearchUtils.createDocument(jsonObject, INDEX_NAME);

        System.out.println(id);

    }

    @Test
    public void updateDocumentByIdTest() throws Exception {

        String jsonString = "{" +
                "\"updated\":\"2017-01-01\"," +
                "\"reason\":\"daily update\"" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        ElasticsearchUtils.updateDocumentById(jsonObject, INDEX_NAME, "1");

    }

    @Test
    public void batchCreateDocumentTest() throws Exception {

        List<JSONObject> list = Lists.newArrayList();

        String jsonString1 = "{" +
                "\"user\":\"kimchy1\"," +
                "\"postDate\":\"2019-08-27\"," +
                "\"message\":\"trying out Elasticsearch 1\"" +
                "}";
        JSONObject jsonObject1 = JSONObject.parseObject(jsonString1);

        String jsonString2 = "{" +
                "\"user\":\"kimchy2\"," +
                "\"postDate\":\"2019-08-28\"," +
                "\"message\":\"trying out Elasticsearch 2\"" +
                "}";
        JSONObject jsonObject2 = JSONObject.parseObject(jsonString2);

        String jsonString3 = "{" +
                "\"user\":\"kimchy3\"," +
                "\"postDate\":\"2019-08-29\"," +
                "\"message\":\"trying out Elasticsearch 3\"" +
                "}";
        JSONObject jsonObject3 = JSONObject.parseObject(jsonString3);

        String jsonString4 = "{" +
                "\"user\":\"kimchy4\"," +
                "\"postDate\":\"2019-08-30\"," +
                "\"message\":\"trying out Elasticsearch 4\"" +
                "}";
        JSONObject jsonObject4 = JSONObject.parseObject(jsonString4);

        list.add(jsonObject1);
        list.add(jsonObject2);
        list.add(jsonObject3);
        list.add(jsonObject4);

        ElasticsearchUtils.batchCreateDocument(list, INDEX_NAME, "2", "3", "4", "5");

    }


}
