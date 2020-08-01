package com.github.bikeholik.ads.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
class AdStatsController {
    private final ElasticsearchQueryProcessor queryProcessor;

    @PostMapping("v1/ad-stats")
    List<Result> queryStats(@RequestBody @Valid Query query) {
        log.info("Processing query {}", query);
        return queryProcessor.execute(query);
    }
}
