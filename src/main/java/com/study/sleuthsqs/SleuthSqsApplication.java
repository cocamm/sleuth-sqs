package com.study.sleuthsqs;

import brave.sampler.Sampler;
import zipkin2.Span;
import brave.Tracing;
import brave.context.log4j2.ThreadContextScopeDecorator;
import brave.instrumentation.aws.sqs.SqsMessageTracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class SleuthSqsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SleuthSqsApplication.class, args);
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync(Tracing tracing) {
        SqsMessageTracing sqsMessageTracing = SqsMessageTracing.create(tracing);

        return AmazonSQSAsyncClientBuilder.standard()
                .withRequestHandlers(sqsMessageTracing.requestHandler())
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4576/", Regions.US_EAST_1.getName()))
                .build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }
}

@Slf4j
@Component
@RequiredArgsConstructor
class Teste1Consumer {

    private final QueueMessagingTemplate messageTemplate;

    @SqsListener("test1")
    public void listen(@Payload String message, @Headers MessageHeaders headers) {
        log.info("message={}", message);
        log.info("headers={}", headers);

        messageTemplate.convertAndSend("http://localhost:4576/queue/test2", "Teste2");
    }
}

@Slf4j
@Component
@RequiredArgsConstructor
class Teste2Consumer {

    @SqsListener("test2")
    public void listen(@Payload String message, @Headers MessageHeaders headers) {
        log.info("message={}", message);
        log.info("headers={}", headers);
    }
}
