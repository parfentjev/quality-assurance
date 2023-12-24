package ee.fakeplastictrees.test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.testng.Assert.*;

import ee.fakeplastictrees.test.model.TransferRequest;
import ee.fakeplastictrees.test.model.TransferResponse;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.math.BigDecimal;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OperationsControllerTest extends AbstractWireMockTest {
  @BeforeMethod
  public void resetStubs() {
    reset();
  }

  @Test
  public void transferPositive() {
    var atcResponse = "{\"result\":\"POSITIVE\"}";
    stubFor(post("/ats").willReturn(okJson(atcResponse)));

    var requestedBody = requestBody();
    var transferResponse =
        requestSpecification(requestedBody)
            .post("/transfer")
            .then()
            .statusCode(200)
            .extract()
            .as(TransferResponse.class);

    var expectedJson = "{\"type\": \"IBAN\", \"identifier\": \"NL43INGB3831267707\"}";
    verify(exactly(1), postRequestedFor(urlEqualTo("/ats")));
    verify(postRequestedFor(urlEqualTo("/ats")).withRequestBody(equalToJson(expectedJson)));

    assertTrue(transferResponse.completed());
    assertNull(transferResponse.message());
  }

  @Test
  public void transferNegative() {
    var atcResponse = "{\"result\":\"NEGATIVE\",\"score\":\"0.75\"}";
    stubFor(post("/ats").willReturn(okJson(atcResponse)));

    var requestedBody = requestBody();
    var transferResponse =
        requestSpecification(requestedBody)
            .post("/transfer")
            .then()
            .statusCode(200)
            .extract()
            .as(TransferResponse.class);

    var expectedJson = "{\"type\": \"IBAN\", \"identifier\": \"NL43INGB3831267707\"}";
    verify(exactly(1), postRequestedFor(urlEqualTo("/ats")));
    verify(postRequestedFor(urlEqualTo("/ats")).withRequestBody(equalToJson(expectedJson)));

    assertFalse(transferResponse.completed());
    assertEquals(transferResponse.message(), "Anti-terrorism check has failed with score=0.75");
  }

  private TransferRequest requestBody() {
    return new TransferRequest(
        format("%s %s", randomAlphabetic(5), randomAlphabetic(10)),
        "NL43INGB3831267707",
        BigDecimal.valueOf(123.45),
        randomAlphabetic(25));
  }

  private RequestSpecification requestSpecification(TransferRequest body) {
    return given()
        .baseUri("http://localhost:8080/operations")
        .contentType(ContentType.JSON)
        .body(body);
  }
}
