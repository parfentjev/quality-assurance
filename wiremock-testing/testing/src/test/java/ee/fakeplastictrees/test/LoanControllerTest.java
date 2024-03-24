package ee.fakeplastictrees.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ee.fakeplastictrees.test.model.LoanCalculatorRequest;
import ee.fakeplastictrees.test.model.LoanCalculatorResponse;
import ee.fakeplastictrees.test.util.Config;
import io.restassured.http.ContentType;

public class LoanControllerTest extends AbstractWireMockTest {
    // this method returns test data
    // testng will execute the test below
    // with both LoanCalculatorRequest objects
    //
    // property value is the same for both, but the loan term
    // and hence the expected payemtn are different
    @DataProvider
    private Object[][] testData() {
        return new Object[][] {
                { new LoanCalculatorRequest(BigDecimal.valueOf(125000), 6), BigDecimal.valueOf(20833.33) },
                { new LoanCalculatorRequest(BigDecimal.valueOf(125000), 12), BigDecimal.valueOf(10416.67) }
        };
    }

    @Test(dataProvider = "testData")
    public void calculateMonthlyPayment(LoanCalculatorRequest request, BigDecimal expectedPayment) {
        // define the response that WireMock should return
        var stub = aResponse()
                .withStatus(200)
                .withHeader("content-type", "application/json")
                .withTransformers("loan-calculator-transformer");

        // create a stub with this response
        stubFor(post("/calculate").willReturn(stub));

        // make a request to the bank-api service
        var response = given()
                .baseUri(Config.BANK_API_URL)
                .contentType(ContentType.JSON)
                .body(request)
                .post("/loans/calculate")
                .then()
                .statusCode(200)
                .extract()
                .as(LoanCalculatorResponse.class);

        // validate the actual monthly payment
        assertEquals(response.monthyPayment(), expectedPayment);
    }
}
