package net.simplifiedcoding.sqlitedbcode;

import okhttp3.HttpUrl;

public class RequestBuilder {
    public static HttpUrl buildURL(String requestType, String host, String path) {
        return new HttpUrl.Builder()
                .scheme(requestType)
                .host(host)
                .addPathSegment(path)
                .build();
    }

    public static HttpUrl buildURL(String requestType, String host, int port, String path) {
        return new HttpUrl.Builder()
                .scheme(requestType)
                .host(host)
                .port(port)
                .addPathSegment(path)
                .build();
    }

}
