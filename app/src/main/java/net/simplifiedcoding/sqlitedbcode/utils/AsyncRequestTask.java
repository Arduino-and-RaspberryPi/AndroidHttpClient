package net.simplifiedcoding.sqlitedbcode.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class AsyncRequestTask {
    private static OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(600, TimeUnit.MILLISECONDS)
            .build();

    public static String makeRequest(final String requestType, final String ip, final int port, final String path) {
        String response = "";
        try {
            response = new AsyncTask<String, Integer, String>() {

                @Override
                protected String doInBackground(String... params) {
                    String httpResponse = "";
                    try {
                        HttpUrl httpUrl;
                        if(port != 0)
                            httpUrl = RequestBuilder.buildURL(requestType, ip, port, path);
                        else
                            httpUrl = RequestBuilder.buildURL(requestType, ip, path);
                        httpResponse = ApiCall.GET(client, httpUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return httpResponse;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
