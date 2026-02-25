package com.geonpil.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * search-service 원격 호출 시 사용할 RestTemplate.
 * search.service.use-remote=true 일 때만 RestBookSearchFacade가 이 빈을 주입받습니다.
 */
@Configuration
public class SearchServiceClientConfig {

    @Bean
    @ConditionalOnProperty(name = "search.service.use-remote", havingValue = "true")
    public RestTemplate searchServiceRestTemplate() {
        RestTemplate rest = new RestTemplate();
        // 필요 시 connect/read timeout 설정
        // SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        // f.setConnectTimeout(3000);
        // f.setReadTimeout(5000);
        // rest.setRequestFactory(f);
        return rest;
    }
}
