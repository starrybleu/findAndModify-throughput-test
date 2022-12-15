package com.example.mongothroughputtest;

import com.mongodb.MongoClientSettings;
import com.mongodb.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Bean
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder()
                .applyToConnectionPoolSettings(b -> {
                    b.addConnectionPoolListener(new MyConnectionPoolListener());
                })
                .build();
    }

    @Slf4j
    static class MyConnectionPoolListener implements ConnectionPoolListener {
        public void connectionPoolCreated(ConnectionPoolCreatedEvent event) {
            log.info("{}", event);
        }

        public void connectionPoolCleared(ConnectionPoolClearedEvent event) {
            log.info("{}", event);
        }

        public void connectionPoolReady(ConnectionPoolReadyEvent event) {
            log.info("{}", event);
        }

        public void connectionPoolClosed(ConnectionPoolClosedEvent event) {
            log.info("{}", event);
        }

        public void connectionCheckOutStarted(ConnectionCheckOutStartedEvent event) {
//            log.info("{}", event);
        }

        public void connectionCheckedOut(ConnectionCheckedOutEvent event) {
//            log.info("{}", event);
        }

        public void connectionCheckOutFailed(ConnectionCheckOutFailedEvent event) {
            log.info("{}", event);
        }

        public void connectionCheckedIn(ConnectionCheckedInEvent event) {
//            log.info("{}", event);
        }

        public void connectionCreated(ConnectionCreatedEvent event) {
//            log.info("{}", event);
        }

        public void connectionReady(ConnectionReadyEvent event) {
            log.info("{}", event);
        }
    }
}
