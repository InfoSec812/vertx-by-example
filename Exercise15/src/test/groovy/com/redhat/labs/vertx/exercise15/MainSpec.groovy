package com.redhat.labs.vertx.exercise15

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

class MainSpec extends Specification {

    def "Test static message handler"() {
        given: "An instance of Vert.x, a deployed Main verticle, and an AsyncCondition" // <1>
            def async = new AsyncConditions(2)                          // <2>
            def vertx = Vertx.vertx()
            vertx.deployVerticle(new Main(), { res ->
                async.evaluate {                                        // <3>
                    // Verticle was successfully deployed
                    res.succeeded() == true
                }
            })
            def result = null as JsonObject

        when: "A message is sent on the event bus to 'test.address'"    // <4>
            vertx.eventBus().send('test.address', new JsonObject([ok: true]), { reply ->
                async.evaluate {                                        // <5>
                    reply.succeeded() == true
                    result = reply.result().body() as JsonObject
                    result.getBoolean("ok") == true
                    result.getString("message") == 'SUCCESS'
                }
            })

        then: "The reply to the message must match expectations"        // <6>
            async.await(10)                                             // <7>
    }
}
