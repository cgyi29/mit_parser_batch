package com.tmap.mit.parser.job;

import com.tmap.mit.parser.constant.JobName;
import com.tmap.mit.parser.constant.StepName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * extension '.shp' file parsing job
 */
@Slf4j
@Configuration
public class ShpFileParserJobConfig {
    @Bean
    public Job fileParserJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder(JobName.FILE_PARSER, jobRepository)
                .start(shpFileParserStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step shpFileParserStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(StepName.SHAPE_FILE_PARSER, jobRepository)
                .tasklet(testTasklet(), transactionManager)
                .build();
    }
    @Bean
    public Tasklet testTasklet(){
        return ((contribution, chunkContext) -> {
            log.info(">>>>> This is step test");
            log.info(">>>>> This is step test");
            log.info(">>>>> This is step test");
            log.info(">>>>> 테스클릿에는 들어온건가???");
            return RepeatStatus.FINISHED;
        });
    }

}
