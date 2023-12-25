package ee.fakeplastictrees.test;

import ee.fakeplastictrees.test.model.TransferRequest;
import ee.fakeplastictrees.test.model.TransferResponse;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.testng.Assert.*;

public class OperationsControllerTest extends AbstractWireMockTest {
    @Test
    public void transferPositive() {
        var response = aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("responsePositive.json");

        stubFor(post("/atc").willReturn(response));

        var transferRequest = transferRequestBody();
        var transferResponse = postOperationsTransfer(transferRequest);

        verify(exactly(1), postRequestedFor(urlEqualTo("/atc")));
        verify(postRequestedFor(urlEqualTo("/atc"))
                .withRequestBody(matchingJsonPath("$.type", equalTo("IBAN")))
                .withRequestBody(matchingJsonPath("$.identifier", equalTo(transferRequest.recipientAccount()))));

        assertTrue(transferResponse.completed());
        assertNull(transferResponse.message());
    }

    @Test
    public void transferNegative() {
        var response = aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("responseNegative.json");

        stubFor(post("/atc").willReturn(response));

        var transferRequest = transferRequestBody();
        var transferResponse = postOperationsTransfer(transferRequest);

        verify(exactly(1), postRequestedFor(urlEqualTo("/atc")));
        verify(postRequestedFor(urlEqualTo("/atc"))
                .withRequestBody(matchingJsonPath("$.type", equalTo("IBAN")))
                .withRequestBody(matchingJsonPath("$.identifier", equalTo(transferRequest.recipientAccount()))));

        assertFalse(transferResponse.completed());
        assertEquals(transferResponse.message(), "Anti-terrorism check has failed with score=0.75");
    }

    private TransferRequest transferRequestBody() {
        return new TransferRequest(
                format("%s %s", randomAlphabetic(5), randomAlphabetic(10)),
                "NL43INGB3831267707",
                BigDecimal.valueOf(123.45),
                randomAlphabetic(25));
    }

    private TransferResponse postOperationsTransfer(TransferRequest body) {
        var baseUri = Optional.ofNullable(System.getenv("BANKING_SERVICE_URL")).orElse("http://localhost:8080");

        return given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(body)
                .post("/operations/transfer")
                .then()
                .statusCode(200)
                .extract()
                .as(TransferResponse.class);
    }
}
