package com.ubs.demo.testers;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class jsonFeeder extends Simulation {
    FeederBuilder<String> feeder = csv("createUsingPOST.csv").circular();
//    FeederBuilder<String> feeder2 = jsonUrl("").circular();
private HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8085/"); //Update the end point URL here
    private ScenarioBuilder createUsingPOST = scenario("createUsingPOST")
            .feed(feeder)
            .exec(
                    http("Do operation for createUsingPOST")
                            .post("/api")
                            .header("content-type","application/json")
                            .asJson()
//                            .body("json").asJson()
                            .check(status().is(200),jsonPath("$.name"))).pause(1);
    {
        setUp(createUsingPOST.injectOpen(rampUsers(5).during(5))).protocols(httpProtocol);
    }

}



