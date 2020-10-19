package com.expedia.http.async;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

public class AsyncHttpClient {
    private static final int MAX_TOTAL_CONNECTION = 100;
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 50000;
    private static final int SOCKET_TIMEOUT_MILLISECONDS = 100000;

    public AsyncHttpClient() {
    }

    public CloseableHttpAsyncClient build() throws IOReactorException {
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS)
                .setSocketTimeout(SOCKET_TIMEOUT_MILLISECONDS)
                .build();
        final DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        final PoolingNHttpClientConnectionManager connectionManager =
                new PoolingNHttpClientConnectionManager(ioReactor);

        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTION);

        return HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(config)
                .build();

    }


}
