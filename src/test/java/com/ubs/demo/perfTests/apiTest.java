package com.ubs.demo.perfTests;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class apiTest extends Simulation{

    //protocol
    private HttpProtocolBuilder httpProtocol = http.baseUrl("https://reqres.in/api");

    //sceanrio
    private ScenarioBuilder Createusers = scenario("Create User").exec(
            http("Create a new User")
                    .post("/users").header("content-type","application/json")
                    .asJson()
                    .body(RawFileBody("Data/user.json"))
//                    .body(StringBody("{\n" +
//                            "    \"name\": \"morpheus\",\n" +
//                            "    \"job\": \"leader\"\n" +
//                            "}")).asJson()
                    .check(status().is(201),jsonPath("$.name").is("qaautomationhub"))).pause(1);



    {
        setUp(
                Createusers.injectOpen(rampUsers(5).during(5))).protocols(httpProtocol);
    }
}

