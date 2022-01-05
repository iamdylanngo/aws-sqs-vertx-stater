package com.dylanngo.starter;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class Sender {

  public void send() {

    final String USAGE = "\n" +
      "Usage: " +
      "   <queueName> <message>\n\n" +
      "Where:\n" +
      "   queueName - the name of the queue.\n\n" +
      "   message - the message to send.\n\n";

    String queueName = "dylan-sqs";
    String message = "hello";
    SqsClient sqsClient = SqsClient.builder()
      .region(Region.AP_SOUTHEAST_1)
      .build();
    sendMessage(sqsClient, queueName, message);
    sqsClient.close();
  }

  public void sendMessage(SqsClient sqsClient, String queueName, String message) {
    try {
      CreateQueueRequest request = CreateQueueRequest.builder()
        .queueName(queueName)
        .build();
      CreateQueueResponse createResult = sqsClient.createQueue(request);

      GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
        .queueName(queueName)
        .build();

      String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
      SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody(message)
        .delaySeconds(5)
        .build();

      sqsClient.sendMessage(sendMsgRequest);

    } catch (SqsException e) {
      System.err.println(e.awsErrorDetails().errorMessage());
      System.exit(1);
    }
  }
}
