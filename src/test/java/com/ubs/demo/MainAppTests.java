package com.ubs.demo;

import com.ubs.demo.gatling.Engine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.ubs.demo.gatling.generateGatlingTests;

import java.io.IOException;
//
@SpringBootTest
class MainAppTests {

	@Test
	void generatePerformanceTests() throws IOException {
//		generateGatlingTests.generateGatlingTests("http://localhost:8085/v2/api-docs");
		//http://localhost:8085/swagger-ui.html
//
//		Engine.runTests();

		generateGatlingTests.generateGatlingTests("src/test/resources/identity-admin.json");
	}


}
