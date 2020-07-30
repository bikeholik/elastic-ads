package com.github.bikeholik.ads.indexer;

import com.github.bikeholik.ads.config.InfrastructureComponentsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Component(InfrastructureComponentsConfig.BEAN_NAME_INDEX_MANAGER)
@RequiredArgsConstructor
@Slf4j
class ElasticsearchIndexManager implements InitializingBean {
    private static final String TEMPLATE_NAME = "ad-stats";
    private final RestHighLevelClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (shouldCreateTemplate()) {
            createTemplate();
        }
    }

    private void createTemplate() throws IOException {
        client.indices().putTemplate(
                new PutIndexTemplateRequest(TEMPLATE_NAME)
                        .source(getTemplateDefinition(), XContentType.JSON),
                RequestOptions.DEFAULT);
        log.info("Template was created");
    }

    private String getTemplateDefinition() throws IOException {
        try (Reader reader = new InputStreamReader(new ClassPathResource("index_template.json").getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    private boolean shouldCreateTemplate() throws IOException {
        return !client.indices().existsTemplate(new IndexTemplatesExistRequest(TEMPLATE_NAME), RequestOptions.DEFAULT);
    }
}
