package com.app.noobshop.application.common;

import com.app.noobshop.application.config.ConfigHolder;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class EsClientUtil {

    public static RestHighLevelClient getEsClient() {
        return openEsClient().client();
    }

    public static EsClientResource openEsClient() {
        String esHost = ConfigHolder.getEsHost();
        int esPort = ConfigHolder.getEsPort();
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(esHost, esPort, "http"))
        );
        return new EsClientResource(client);
    }

    public record EsClientResource(RestHighLevelClient client) implements AutoCloseable {

        @Override
        public void close() throws Exception {
            client.close();
        }
    }
}
