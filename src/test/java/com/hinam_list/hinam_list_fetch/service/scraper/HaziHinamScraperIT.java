package com.hinam_list.hinam_list_fetch.service.scraper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.hinam_list.hinam_list_fetch.service.scraper.exception.APIResponseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@ActiveProfiles("test")
@SpringBootTest
//@TestPropertySource("classpath:json-scraper-test.properties")
public class HaziHinamScraperIT {
    @Autowired
    protected HaziHinamScraper scraper;
    protected final String jsonResponseString;


    public HaziHinamScraperIT() {
        addInitStub();

        Resource storeApiResponseFile = new DefaultResourceLoader().getResource("StoreAPIExample-HaziHinam.json");
        try {
            jsonResponseString =  new String(
                    storeApiResponseFile.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();


    @DynamicPropertySource
    static void configureProperties (DynamicPropertyRegistry registry) {
        registry.add("store.storeNameDataMap.HaziHinam.storeApiUrl", wireMockServer::baseUrl);
    }

    protected void addInitStub() {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/init"))
                .willReturn(
                        ok()));
    }

    @Test
    void getCategoryIdListTest () throws IOException, InterruptedException, APIResponseException, APIResponseException {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/Catalog/get"))
                .willReturn(
                ok(
                        jsonResponseString
                )));

        var result = this.scraper.getCategoryIdList();
        assertEquals(Arrays.asList("0", "1", "2", "3"), result);
    }
}
