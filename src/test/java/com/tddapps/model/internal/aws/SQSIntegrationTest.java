package com.tddapps.model.internal.aws;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.tddapps.model.internal.aws.test.TestEnvironment;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(services = { "sqs" }, environmentVariableProvider = TestEnvironment.class)
public class SQSIntegrationTest {
    @Test
    void SampleTest1(){
        val sqs = AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4576", "test_sandbox"))
                .build();
        sqs.createQueue(new CreateQueueRequest("sample_queue_1"));

        val queueUrl = sqs.listQueues().getQueueUrls().get(0);

        sqs.sendMessage(queueUrl, "Sample Message1");

        val messages = sqs.receiveMessage(queueUrl).getMessages();

        val receivedBody = messages.get(0).getBody();

        messages.forEach(m -> sqs.deleteMessage(queueUrl, m.getReceiptHandle()));

        sqs.deleteQueue(queueUrl);

        assertEquals("Sample Message1", receivedBody);
    }
}
