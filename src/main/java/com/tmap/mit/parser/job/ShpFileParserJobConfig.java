package com.tmap.mit.parser.job;

import com.tmap.mit.parser.constant.JobName;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
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
 * file을 파싱하기 위한 job
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileParserJobConfig {
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 1000;

    @Bean
    public Job fileParserJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder(, jobRepository)
                .start(shpeFileParserStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step shpeFileParserStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(SHAPE_FILE_PARSER_STEP, jobRepository)
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

    /*@Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 진행중이며, 종료일시(endedAt)이 현재 시점보다 과거일 경우 만료 대상이 됩니다.
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }*/

    /*@Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }
    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }*/

}
