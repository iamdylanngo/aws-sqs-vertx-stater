package com.dylanngo.starter;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;

public class LongPolling {

  private String QUEUE_NAME = "dylan-sqs";

  public void polling() {

    // Create a SqsClient object
    SqsClient sqsClient = SqsClient.builder()
      .region(Region.AP_SOUTHEAST_1)
      .build();
    setLongPoll(sqsClient);
    sqsClient.close();

  }

  public void setLongPoll(SqsClient sqsClient) {

    // Enable long polling when creating a queue.
    HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
    attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "5");

    CreateQueueRequest createRequest = CreateQueueRequest.builder()
      .queueName(QUEUE_NAME)
      .attributes(attributes)
      .build();

    try {
      GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
        .queueName(QUEUE_NAME)
        .build();
      String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

      // Enable long polling on an existing queue.
      SetQueueAttributesRequest setAttrsRequest = SetQueueAttributesRequest.builder()
        .queueUrl(queueUrl)
        .attributes(attributes)
        .build();

      sqsClient.setQueueAttributes(setAttrsRequest);

      // Enable long polling on a message receipt.
      ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
        .queueUrl(queueUrl)
        .waitTimeSeconds(10)
        .build();

      sqsClient.receiveMessage(receiveRequest);

      List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

      // Print out the messages
      for (Message m : messages) {
        System.out.println("\n" +m.body());
      }
    } catch (SqsException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }
}
