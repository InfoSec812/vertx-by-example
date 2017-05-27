package com.redhat.labs.vertx.exercise15;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class Main extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer("test.address", msg -> {
            msg.reply(new JsonObject().put("ok", true).put("message", "SUCCESS"));
        });

        startFuture.complete();
    }
}
