package com.hinam_list.hinam_list_fetch.service.scraper;

import com.hinam_list.hinam_list_fetch.config.StoreDataConfigProperties;
import com.hinam_list.hinam_list_fetch.service.scraper.exception.APIResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public abstract class AbstractScraper {
    protected HttpClient client;
    protected StoreDataConfigProperties storeDataConfigProperties;
    protected final String storeName;

    @Autowired
    public AbstractScraper(StoreDataConfigProperties storeDataConfigProperties, String storeName) {
        this.storeName = storeName;
        this.storeDataConfigProperties = storeDataConfigProperties;

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = HttpClient.newBuilder().cookieHandler(cookieManager).build();
    }

    public abstract List<String> getCategoryIdList() throws IOException, APIResponseException, InterruptedException;
    public abstract JSONArray getCategoryProductInfo(String categoryId) throws IOException, APIResponseException, InterruptedException;


    private HttpRequest.Builder createHttpBuilder(String uriString) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        try {
            requestBuilder.uri(new URI(uriString));
        } catch (URISyntaxException uriExp) { // uri string is incorrect
            throw (new RuntimeException("Target URI is not in the correct format", uriExp));
        }
        return requestBuilder;
    }

    protected HttpRequest createHttpGetRequest(String uriString) {
        var builder = createHttpBuilder(uriString);
        //builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        builder.GET();
        return builder.build();
    }

    protected HttpRequest createHttpPostRequest(String uriString, Map<String,String> propertyMap) {
        var builder = createHttpBuilder(uriString);
        builder.header("Content-Type", "application/json; charset=utf-8");
        //builder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        JSONObject obj = new JSONObject(propertyMap);
        builder.POST(HttpRequest.BodyPublishers.ofString(obj.toString()));
        return builder.build();
    }

    protected String getResponse(HttpRequest request) throws IOException, InterruptedException, APIResponseException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int responseCode = response.statusCode();
        if (responseCode != 200) {
            throw (new APIResponseException(responseCode));
        }
        return response.body();
    }

}
