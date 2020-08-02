package com.github.bikeholik.ads.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
class ImportController {

    private final JobLauncher jobLauncher;
    private final Job importJob;

    @PostMapping("v1/imports")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void importData() throws Exception {
        jobLauncher.run(importJob, new JobParameters());
        log.info("Job executed");
    }
}
