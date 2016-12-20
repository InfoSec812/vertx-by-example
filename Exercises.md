### **Exercise 1:** Simple Vert.x Hello World Web Server

1. Download the FULL Vert.x distribution from [http://vertx.io/](http://vertx.io/) and ensure that the **vertx** command is in your path (vertx/bin/vertx)

2. Create a new file **Exercise1.groovy** with the following contents:

[Exercise1.groovy](Exercise1/Exercise1.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise1 extends GroovyVerticle {

    void start() {
        // Create a new HttpServer
        def server = vertx.createHttpServer()
        
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        // Register a request handler for the HttpServer
        server.requestHandler({ req ->    
                 req.response().end(response) })

        // Listen on port 8080 and interface `127.0.0.1`
        server.listen(8080, '127.0.0.1')
    }
}
```


3. Run the verticle with the command `**vertx run Exercise1.groovy**`

    1. You should see a message like: `Succeeded in deploying verticle`

4. Open a browser and point it at: [http://localhost:8080/](http://localhost:8080/)

Next Steps: (see [HttpServerResponse](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpServerResponse.html))

* Modify the example above to add a `Content-Type` response header

* Modify the example above to add an HTTP response code of 201 to the response

* Modify the example above to add an HTTP reason phrase of ‘IDUNNO’ to the response

### **Exercise 2**: Are you fluent?!?!

Vert.x APIs are written to be [fluent](https://en.wikipedia.org/wiki/Fluent_interface). This means that you can chain method calls together so that they form a sort of domain specific language which CAN be easier to read. We will modify our first example to use the fluent API in Vert.x to perform the same operations.

[Exercise2.groovy](Exercise2/Exercise2.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise2 extends GroovyVerticle {

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler({ req ->    // Register a request handler for the HttpServer
                 req.response().end(response) })
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
```


You’ll see that we chained the createHttpServer() method, which returns an HttpServer object, to the requestHandler() method. We then chained the requestHandler() method to the listen() method. Each of these chained methods returns the original HttpServer object so that we can make subsequent calls in a fluent manner.

### **Exercise 3:** Handlers

A handler in Vert.x is a form of [Callback](https://en.wikipedia.org/wiki/Callback_(computer_programming)). Handlers are passed as arguments to some Vert.x methods so that the callback can be executed once a particular asynchronous operation has been completed. Handlers for Vert.x can be written in Groovy in several ways:

#### **Exercise 3.1:** Handler classes

The basic Handler in Vert.x is any class which implements the [Handler](http://vertx.io/docs/apidocs/io/vertx/core/Handler.html) interface. For example:

[Exercise3_1.groovy](Exercise3/Exercise3_1.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise3_1 extends GroovyVerticle {

    private class RequestHandler implements Handler<HttpServerRequest> {
        void handle(HttpServerRequest req) {
            def response = new JsonObject([ok: true]).encode()
            req.response().end(response)
        }
    }

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(new RequestHandler())
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
```


As you can see, we pass an instance of the RequestHandler class to the requestHandler() method on the HttpServer object and that instance will handle the HTTP requests.

#### **Exercise 3.2:** Method References

Another way to implement handlers removes some of the boiler-plate of having a separate hanlder class for each Callback we want to register. It’s called a [Method Reference](http://docs.groovy-lang.org/latest/html/documentation/#method-pointer-operator). A method reference is a way of assigning a method to behave as a callback without having to implement a Handler interface on a new class.

[Exercise3_2.groovy](Exercise3/Exercise3_2.groovy)
```groovy
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class Exercise3_2 extends GroovyVerticle {

    /**
     * Handle HttpServerRequests
     */
    void handleRequest(HttpServerRequest req) {
        def response = new JsonObject([ok: true]).encode()
        req.response().end(response)
    }

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(this.&handleRequest) // Register a request handler
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
```


#### **Exercise 3.3:** Closures

Finally, in Groovy we can use [Closures](http://docs.groovy-lang.org/latest/html/documentation/#_closures). Closures are a way of writing a bit of code which can be passed as a value . . . in-line…

[Exercise3_3.groovy](Exercise3/Exercise3_3.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.core.json.JsonObject

class HelloWorld extends GroovyVerticle {

    void start() {
        // Create a JSON response
        def response = new JsonObject([ok: true]).encode()

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler({ req ->    // Register a request handler
                 req.response().end(response)
             })
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
```


An alternate way of declaring that closure would be to assign the closure to a variable and then pass the variable into the requestHandler() method as shown below:

[Exercise3_3_1.groovy](Exercise3/Exercise3_3_1.groovy)
```groovy
import io.vertx.core.json.JsonObject
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.lang.groovy.GroovyVerticle

class Exercise3_3_1 extends GroovyVerticle {

    void start() {

        def reqHandler = { HttpServerRequest req ->
            // Create a JSON response
            def response = new JsonObject([ok: true]).encode()
            req.response().end(response)
        }

        vertx.createHttpServer()         // Create a new HttpServer
             .requestHandler(reqHandler) // Register a request handler
             .listen(8080, '127.0.0.1')  // Listen on port 8080 and interface `127.0.0.1`
    }
}
```


Next Steps: (see [HttpServerRequest](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpServerRequest.html))

* Modify the example above to include the requested path as an item in the JSON response body

* Modify the example above to include the request headers as a nested JSON object within the response body

### **Exercise 4:** Using Routers

So far, we have seen that we can add a requestHandler() to an HTTP server, but what if we want to have a number of different paths which do different things in our web application? This is where the Vert.x Web module comes in. It gives us a new features like [Router](http://vertx.io/docs/apidocs/io/vertx/ext/web/Router.html) and [RoutingContext](http://vertx.io/docs/apidocs/io/vertx/ext/web/RoutingContext.html).

[Exercise4.groovy](Exercise4/Exercise4.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

class Exercise4 extends GroovyVerticle {

    void start() {
        def router = Router.router(vertx)

        router.get('/')              .handler(this.&rootHandler)
        router.get('/something/else').handler(this.&otherHandler)

        vertx.createHttpServer()             // Create a new HttpServer
             .requestHandler(router.&accept) // Register a request handler
             .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: true, path: ctx.request().path()]).encode())
    }

    void otherHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: false, message: 'Something Else']).encode())
    }
}
```

1. You see that we added 2 different routes to the Router instance

2. Each route has a separate handler set via a method reference

3. Finally, we pass the Router’s accept method via a method reference as a handler for the HttpServer’s requestHandler() method.

### **Exercise 5:** Routes with Path Parameters

In the previous example, we saw that we could specify different paths with different handlers, but what about if we want to capture information FROM the path in a programmatic manner?

[Exercise5.groovy](Exercise5/Exercise5.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

class Exercise5 extends GroovyVerticle {

    void start() {
        def router = Router.router(vertx)

        router.get('/')            .handler(this.&rootHandler)
        router.get('/customer/:id').handler(this.&custHandler)

        vertx.createHttpServer()             // Create a new HttpServer
            .requestHandler(router.&accept) // Register a request handler
            .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
    }

    void rootHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([ok: true, path: ctx.request().path()]).encode())
    }

    void custHandler(RoutingContext ctx) {
        ctx.response().end(new JsonObject([
           ok: false,
           custID: ctx.request().getParam('id')
        ]).encode())
    }
}
```


Next Steps: (see [Router](http://vertx.io/docs/apidocs/io/vertx/ext/web/Router.html), [Routing With Regular Expressions](http://vertx.io/docs/vertx-web/groovy/#_routing_with_regular_expressions), [Routing Based On MIME Types](http://vertx.io/docs/vertx-web/groovy/#_routing_based_on_mime_type_of_request), [Request Body Handling](http://vertx.io/docs/vertx-web/groovy/#_request_body_handling))

* Modify the example above to have a new route which had multiple path parameters

* Modify the example above to use a route with regular expressions

* Modify the example to add a new HTTP POST endpoint which consumes JSON and produces the POSTed JSON

### **Exercise 6:** Programmatically Deploy Verticles

So far, our exercised have done all of their work in a single Verticle (HelloWorld). This is fine for simple applications, but it does not scale well for larger and more complex applications. Each Verticle is single-threaded; so in order to utilize our CPU cores effectively, we need to distribute workloads across multiple Verticles.

[EventVerticle.groovy](Exercise6/EventVerticle.groovy)
```groovy
import io.vertx.lang.groovy.GroovyVerticle
import io.vertx.groovy.core.eventbus.Message
import io.vertx.core.json.JsonObject

class EventVerticle extends GroovyVerticle {

    @Override
    void start() {
        vertx.eventBus().consumer('event.verticle', this.&doSomething)
    }

    void doSomething(Message<JsonObject> msg) {
        if ((Math.round(Math.random()*1))==1) { // Randomly choose to return or fail
            msg.reply(msg.body())
        } else {
            msg.fail(1, 'Random Failure')
        }
    }
}
```

[Exercise6.groovy](Exercise6/Exercise6.groovy)
```groovy
// .. SNIP ..

class Exercise6 extends GroovyVerticle {

    void start() {
        def router = Router.router(vertx)

        router.get().handler(this.&rootHandler)

        vertx.createHttpServer()            // Create a new HttpServer
            .requestHandler(router.&accept) // Register a request handler
            .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        vertx.deployVerticle('groovy:EventVerticle.groovy')
    }

    // .. SNIP ..
}
```


(**NOTE:** When using `**vertx run <VerticleName>**` to launch Vert.x applications, the files should be in the current 
working directory or a child directory referenced by it's relative path)

Several new concepts have been introduced in this example:

* The [EventBus](http://vertx.io/docs/vertx-core/groovy/#event_bus) - Used to communicate between Verticles in a 
thread-safe manner

* Deploying Verticles Programmatically

* Handling [AsyncResult](http://vertx.io/docs/apidocs/io/vertx/core/AsyncResult.html)s via Callback

* Using [Message](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/Message.html) objects - Message objects consist of JsonObject or String contents and can be replied to

### **Exercise 7:** Deploy With Futures
Often, the application will need to ensure that certain Verticles are 
already up and running before proceeding to do other actions. To allow for 
this, Vert.x provides a way of deploying Verticles with a callback once 
the deployment is complete.

[Example7.groovy](Example7/Example7.groovy)
```groovy
// .. SNIP ..
class Exercise7 extends GroovyVerticle {

    void start() {
        vertx.deployVerticle('groovy:EventVerticle.groovy', this.&deployHandler)
    }

	// .. SNIP ..

    void deployHandler(AsyncResult<String> res) {
        if (res.succeeded()) {
            LoggerFactory.getLogger(Exercise7).info('Successfully deployed EventVerticle')

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            def router = Router.router(vertx)

            router.get().handler(this.&rootHandler)

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router.&accept) // Register a request handler
                .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        } else {
            // Otherwise, exit the application
            LoggerFactory.getLogger(Exercise7).error('Failed to deploy EventVerticle', res.cause())
            vertx.close()
        }
    }
}
```

[EventVerticle.groovy](Exercise7/EventVerticle.groovy)

Next Steps:
* Modify the example above to attempt to redeploy EventVerticle in case of a failure (Use maximum of 3 retries)
* Modify the example above to deploy more than one Verticle and call the new Verticle `AnotherVerticle.groovy`

### Exercise 8: Asynchronous Coordination
It is useful to coordinate several asynchronous operations in a single handler for certain situations. To 
facilitate this, Vert.x provides a [CompositeFuture](http://vertx.io/docs/apidocs/io/vertx/core/CompositeFuture.html)

[Exercise8.groovy](Exercise8/Exercise8.groovy)
```groovy
// .. SNIP ..

class Exercise8 extends GroovyVerticle {

    void start() {

        Future eventVerticleFuture = Future.future()
        Future anotherVerticleFuture = Future.future()

        CompositeFuture.join(eventVerticleFuture, anotherVerticleFuture).setHandler(this.&deployHandler)

        vertx.deployVerticle('groovy:EventVerticle.groovy', eventVerticleFuture.completer())
        vertx.deployVerticle('groovy:AnotherVerticle.groovy', anotherVerticleFuture.completer())
    }

    // .. SNIP ..

    void deployHandler(CompositeFuture cf) {
        if (cf.succeeded()) {
            LoggerFactory.getLogger(Exercise8).info('Successfully deployed all verticles')

            // If the EventVerticle successfully deployed, configure and start the HTTP server
            def router = Router.router(vertx)

            router.get().handler(this.&rootHandler)

            vertx.createHttpServer()            // Create a new HttpServer
                .requestHandler(router.&accept) // Register a request handler
                .listen(8080, '127.0.0.1')      // Listen on 127.0.0.1:8080
        } else {
            def range = 0..(cf.size() - 1)
            range.each { x ->
                if (cf.failed(x)) {
                    LoggerFactory.getLogger(Exercise8).error('Failed to deploy verticle', cf.cause(x))
                }
            }
            vertx.close()
        }
    }
}

```

Next Steps: (see [CompositeFuture](http://vertx.io/docs/apidocs/io/vertx/core/CompositeFuture.html) and 
[Async Coordination](http://vertx.io/docs/vertx-core/groovy/#_sequential_composition))
* Modify the example above to use a List of futures instead of specifying each future as a parameter.
* Remove the CompositeFuture and use composed [Future](http://vertx.io/docs/apidocs/io/vertx/core/Future.html)s to 
  load one verticle after another
