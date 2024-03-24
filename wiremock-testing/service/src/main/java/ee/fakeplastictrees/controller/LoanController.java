package ee.fakeplastictrees.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ee.fakeplastictrees.model.loan.LoanCalculatorRequest;
import ee.fakeplastictrees.model.loan.LoanCalculatorResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RestController
@RequestMapping("/loans")
public class LoanController {
    @Value("${loanServiceUrl}")
    private String loanServiceUrl;

    @PostMapping("/calculate")
    public ResponseEntity<LoanCalculatorResponse> calculate(@RequestBody LoanCalculatorRequest request) {
        var response = RestAssured.given()
                .baseUri(loanServiceUrl)
                .contentType(ContentType.JSON)
                .body(request)
                .post("/calculate")
                .prettyPeek()
                .as(LoanCalculatorResponse.class);

        return ResponseEntity.ok(response);
    }
}
