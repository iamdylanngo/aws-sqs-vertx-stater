package com.dylanngo.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    var sender = new Sender();
    var receive = new Receive();

    new Thread(() -> {
      var longPolling = new LongPolling();
      longPolling.polling();
    }).start();

    vertx.createHttpServer().requestHandler(req -> {
      sender.send();
//      receive.receive();

      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");

    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
