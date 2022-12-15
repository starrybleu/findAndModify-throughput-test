package com.example.mongothroughputtest;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@SpringBootApplication
public class MongoThroughputTestApplication {

    private final MongoOperations mongoOperations;

    public MongoThroughputTestApplication( MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    static final int iterationCount = 100000;

    public static void main(String[] args) {
        SpringApplication.run(MongoThroughputTestApplication.class, args);
    }

    @PostConstruct
    void execute() {
        var atomicInt = new AtomicInteger(0);
        var threadPool = Executors.newFixedThreadPool(16); // NOTE: 스레드 개수에 따라 findAndModify 의 duration 이 크게 영향을 받는다는 것을 실험을 통해 알아냈다.
        var started = LocalDateTime.now();
        var oldTimeMillis = System.currentTimeMillis();
        log.info("Started enqueued");
        for (int i = 0; i < iterationCount; i += 1) {
            threadPool.execute(() -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                var a = mongoOperations.findAndModify(query(where("_id").is("num")),
                        new Update().inc("seq", 1), options().returnNew(true).upsert(true),
                        Map.class);
                stopWatch.stop();
                long duration = stopWatch.getLastTaskTimeMillis();
                if (duration > 200 || atomicInt.get() == iterationCount) {
                    log.info("duration : {} ms, atomicInt: {}, a: {}", duration, atomicInt.get(), a);
                }
                if (atomicInt.get() >= iterationCount - 1) {
                    log.info("Complete totalDuration: {} ms", System.currentTimeMillis() - oldTimeMillis);
                    log.info("$$$$ duration : {} ms, atomicInt: {}, a: {}", duration, atomicInt.get(), a);
                }
                atomicInt.getAndIncrement();
            });
        }
        log.info("Finished enqueued, strated: {}", started);
    }
}
