package com.hinam_list.hinam_list_fetch.service.scraper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hinam_list.hinam_list_fetch.config.StoreDataConfigProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HaziHinamScraperTest {
    @Mock
    StoreDataConfigProperties properties;

    @InjectMocks
    HaziHinamScraper scraper;
    protected final String jsonResponseString;


    public HaziHinamScraperTest() {
        Resource storeApiResponseFile = new DefaultResourceLoader().getResource("StoreAPIExample-HaziHinam.json");
        try {
            jsonResponseString =  new String(
                    storeApiResponseFile.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Category Extraction")
    void extractJsonCategoryArrayFromJsonResponseStringTest() throws JsonProcessingException {
        JSONObject expectedResponseObject = new JSONObject(jsonResponseString);

        ObjectMapper mapper = new ObjectMapper();

        assertEquals(mapper.readTree(expectedResponseObject.getJSONObject("Results").getJSONArray("Categories").toString()),
                mapper.readTree(scraper.extractJsonCategoryArrayFromJsonResponseString(jsonResponseString).toString()));
    }

    @Test
    @Order(2)
    @DisplayName("Subcategory Extraction")
    void extractSubcategoryIdListFromCategoryJsonArrayTest() {
        JSONObject expectedResponseObject = new JSONObject(jsonResponseString);
        JSONArray expectedCategoryJsonArray = expectedResponseObject.getJSONObject("Results").getJSONArray("Categories");
        assertEquals(Arrays.asList("0","1","2","3"),
                scraper.extractSubcategoryIdListFromCategoryJsonArray(expectedCategoryJsonArray));
    }
}
