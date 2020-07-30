package com.github.bikeholik.ads.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "import")
@Getter
@Setter
class ImportProperties {
    private String url;
    private int chunkSize = 128;
}
