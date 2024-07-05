package com.maohua.search;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.maohua.search.config.ElasticConfig;
import lombok.Data;
import net.minidev.json.JSONArray;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);

    }

    @Test
    void saveDataToES() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");//数据id
        //request.source("userName", "jane", "age", "19", "sex", "male");
        User user = new User();
        user.setAge(19);
        user.setUseName("hello");
        String jsonString = JacksonUtils.toJson(user);
        request.source(jsonString, XContentType.JSON);

       // System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + client);
        //执行保存操作
        IndexResponse index = client.index(request, ElasticConfig.COMMON_OPTIONS);
        System.out.println(index);
    }
    @Data
    class User{
        private String useName;
        private String gender;
        private Integer age;
    }

    @Test
    public void searchESData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //指定database
        searchRequest.indices("users");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("useName", "hello"));
        //按照年龄分布聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation((ageAgg));
        //按年龄平均工资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("blanaceAgg").field("balance");
        sourceBuilder.aggregation(balanceAvg);
      //  sourceBuilder.from();

        System.out.println(sourceBuilder.toString());

        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        //分析结果
        System.out.println(response.toString());
        //获取aggregation的信息
        Aggregations aggregations = response.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for(Terms.Bucket bucket: ageAgg1.getBuckets()){
            bucket.getKeyAsString();
        }
        Avg balanceAvg1 = aggregations.get("balanceAgg");
        System.out.println(balanceAvg1);


    }

}
