package com.dylanngo.starter;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

public class Receive {

  private String QUEUE_NAME = "dylan-sqs";

  public void receive() {
    SqsClient sqsClient = SqsClient.builder()
      .region(Region.AP_SOUTHEAST_1)
      .build();

    try {
      CreateQueueRequest request = CreateQueueRequest.builder()
        .queueName(QUEUE_NAME)
        .build();
      CreateQueueResponse createResult = sqsClient.createQueue(request);

      GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
        .queueName(QUEUE_NAME)
        .build();

      String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

      SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody("hello world")
        .delaySeconds(5)
        .build();
      sqsClient.sendMessage(sendMsgRequest);

      // Send multiple messages to the queue
      SendMessageBatchRequest sendBatchRequest = SendMessageBatchRequest.builder()
        .queueUrl(queueUrl)
        .entries(
          SendMessageBatchRequestEntry.builder()
            .messageBody("Hello from message 1")
            .id("msg_1")
            .build()
          ,
          SendMessageBatchRequestEntry.builder()
            .messageBody("Hello from message 2")
            .delaySeconds(10)
            .id("msg_2")
            .build())
        .build();
      sqsClient.sendMessageBatch(sendBatchRequest);

      // Receive messages from the queue
      ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
        .queueUrl(queueUrl)
        .build();
      List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

      // Print out the messages
      for (Message m : messages) {
        System.out.println("\n" +m.body());
      }
    } catch (QueueNameExistsException e) {
      throw e;
    }
  }
}
