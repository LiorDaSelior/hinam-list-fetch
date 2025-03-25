package com.hinam_list.hinam_list_fetch.service.scraper;

import com.hinam_list.hinam_list_fetch.config.StoreDataConfigProperties;
import com.hinam_list.hinam_list_fetch.service.scraper.exception.APIResponseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class HaziHinamScraper extends AbstractScraper{

    public HaziHinamScraper(StoreDataConfigProperties storeDataConfigProperties) throws IOException, InterruptedException, APIResponseException {
        super(storeDataConfigProperties, "HaziHinam");
        if (storeDataConfigProperties.getStoreNameDataMap().containsKey(storeName)) {
            String uriString = storeDataConfigProperties.getStoreNameDataMap().get(storeName).storeApiUrl() + "/init";
            HttpRequest request = createHttpGetRequest(uriString);
            getResponse(request);
        }
    }
    
    @Override
    public List<String> getCategoryIdList() throws IOException, APIResponseException, InterruptedException {
        String uriString = storeDataConfigProperties.getStoreNameDataMap().get(storeName).storeApiUrl() +
                "/api/Catalog/get";
        HttpRequest request = createHttpGetRequest(uriString);
        String jsonResponseString = getResponse(request);

        JSONArray categoryJsonArray = extractJsonCategoryArrayFromJsonResponseString(jsonResponseString);

        return extractSubcategoryIdListFromCategoryJsonArray(categoryJsonArray);
    }

    protected JSONArray extractJsonCategoryArrayFromJsonResponseString(String jsonResponse) {
        return new JSONObject(jsonResponse)
                .getJSONObject("Results")
                .getJSONArray("Categories");
    }

    protected List<String> extractSubcategoryIdListFromCategoryJsonArray(JSONArray categoryJsonArray) {
        List<String> resultIdArray = new ArrayList<>();
        for (int i = 0; i < categoryJsonArray.length(); i++) {
            JSONArray currentSubCategoryJsonArray = categoryJsonArray
                    .getJSONObject(i)
                    .getJSONArray("SubCategories");
            for (int j = 0; j < currentSubCategoryJsonArray.length(); j++) {
                resultIdArray.add(
                        String.valueOf(
                                currentSubCategoryJsonArray
                                        .getJSONObject(j)
                                        .getInt("Id")));
            }
        }
        return resultIdArray;
    }

    @Override
    public JSONArray getCategoryProductInfo(String categoryId) throws IOException, APIResponseException, InterruptedException {
        String uriString = storeDataConfigProperties.getStoreNameDataMap().get(storeName).storeApiUrl() +
                "/api/item/getItemsBySubCategory?Id=" + categoryId;
        HttpRequest request = createHttpGetRequest(uriString);
        String jsonResponseString = getResponse(request);
        return extractProductArrayFromJsonResponseString(jsonResponseString);
    }

    protected JSONArray extractProductArrayFromJsonResponseString(String jsonResponse) {
        return new JSONObject(jsonResponse)
                .getJSONObject("Results")
                .getJSONObject("Category")
                .getJSONObject("SubCategory")
                .getJSONArray("Items");
    }
}
