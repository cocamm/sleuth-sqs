# sleuth-sqs

## Configuração 

Adicionar as dependências:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    <dependency>
        <groupId>io.zipkin.aws</groupId>
        <artifactId>brave-instrumentation-aws-java-sdk-sqs</artifactId>
        <version>0.20.0</version>
    </dependency>
</dependencies>
```

Adicionar a configuração ao bean do client sqs:

```java
@Bean
@Primary
public AmazonSQSAsync amazonSQSAsync(Tracing tracing) {
    SqsMessageTracing sqsMessageTracing = SqsMessageTracing.create(tracing);

    return AmazonSQSAsyncClientBuilder.standard()
            .withRequestHandlers(sqsMessageTracing.requestHandler())
            .build();
}
```

Resultado no log:
```console
2020-04-11 13:35:04.964  INFO [sleuth-sqs,bb60c77b0995e7d8,8b7934b554420fdb,false] 72678 --- [enerContainer-4] com.study.sleuthsqs.Teste1Consumer       : message=Teste
2020-04-11 13:35:04.964  INFO [sleuth-sqs,bb60c77b0995e7d8,8b7934b554420fdb,false] 72678 --- [enerContainer-4] com.study.sleuthsqs.Teste1Consumer       : headers={SentTimestamp=1586622904970, ReceiptHandle=aa77bf5f-10a2-4d89-94dc-1fede10a6836#839d12ce-e468-4efd-becd-79f6366d7760, MessageGroupId=, SenderId=127.0.0.1, LogicalResourceId=test1, ApproximateReceiveCount=1, Visibility=org.springframework.cloud.aws.messaging.listener.QueueMessageVisibility@6e003d4c, MessageDeduplicationId=, lookupDestination=test1, ApproximateFirstReceiveTimestamp=1586622904971, MessageId=aa77bf5f-10a2-4d89-94dc-1fede10a6836}
2020-04-11 13:35:05.035  INFO [sleuth-sqs,bb60c77b0995e7d8,db8a1969a05bdacf,false] 72678 --- [enerContainer-4] com.study.sleuthsqs.Teste2Consumer       : message=Teste2
2020-04-11 13:35:05.035  INFO [sleuth-sqs,bb60c77b0995e7d8,db8a1969a05bdacf,false] 72678 --- [enerContainer-4] com.study.sleuthsqs.Teste2Consumer       : headers={SentTimestamp=1586622905030, ReceiptHandle=8f2ba2a6-a6c7-40d6-8c7a-e306e5a2ce15#fc1a3c0f-f0ee-4466-9e1f-6b20e4d54113, X-B3-ParentSpanId=8b7934b554420fdb, MessageGroupId=, SenderId=127.0.0.1, LogicalResourceId=test2, ApproximateReceiveCount=1, X-B3-SpanId=a33dc46508f56d1b, X-B3-Sampled=0, Visibility=org.springframework.cloud.aws.messaging.listener.QueueMessageVisibility@4115b61, X-B3-TraceId=bb60c77b0995e7d8, MessageDeduplicationId=, contentType=text/plain;charset=UTF-8, lookupDestination=test2, ApproximateFirstReceiveTimestamp=1586622905031, MessageId=8f2ba2a6-a6c7-40d6-8c7a-e306e5a2ce15}

````