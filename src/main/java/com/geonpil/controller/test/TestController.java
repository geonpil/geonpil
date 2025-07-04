package com.geonpil.controller.test;

import com.geonpil.service.test.ElasticsearchTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ElasticsearchTestService elasticsearchTestService;

    @GetMapping("/es-test")
    public String testEs() {
        elasticsearchTestService.ping();
        return "Elasticsearch 테스트 완료!";
    }
}
