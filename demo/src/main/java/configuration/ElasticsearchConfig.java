package configuration;

import org.apache.catalina.authenticator.BasicAuthenticator;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class ElasticsearchConfig {

    @Value("${elastic.search.auth.header}")
    private String elasticSearchAuthHeader;

    @Bean
    public RestHighLevelClient restHighLevelClient() {

//        CredentialsProvider basicCredentials = new BasicCredentialsProvider();
//
//        basicCredentials.setCredentials(
//                AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "8EWR-_RLefuLW0MgMQk=")
//        );
//
//        RestClientBuilder restClientBuilder = RestClient.builder(
//                new HttpHost("localhost", 9200, "http")
//        ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//            @Override
//            public HttpAsyncClientBuilder customizeHttpClient(
//                    HttpAsyncClientBuilder httpClientBuilder) {
//                return httpClientBuilder
//                        .setDefaultCredentialsProvider(basicCredentials);
//            }
//        });
        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        elasticSearchAuthHeader)};

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"))
                        .setDefaultHeaders(defaultHeaders));

    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {

        return new ElasticsearchRestTemplate(restHighLevelClient());

    }

}
