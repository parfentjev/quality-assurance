package ee.fakeplastictrees.controller;

import ee.fakeplastictrees.model.atc.AntiTerrorismCheckRequest;
import ee.fakeplastictrees.model.atc.AntiTerrorismCheckResponse;
import ee.fakeplastictrees.model.atc.AntiTerrorismCheckResult;
import ee.fakeplastictrees.model.bank.TransferRequest;
import ee.fakeplastictrees.model.bank.TransferResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ee.fakeplastictrees.model.atc.AntiTerrorismCheckType.IBAN;
import static java.lang.String.format;

@RestController
@RequestMapping("/operations")
public class OperationsController {
    @Value("${antiTerrorismCheckServiceUrl}")
    private String antiTerrorismCheckServiceUrl;

    @PostMapping(value = "/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest) {
        var atcRequest = new AntiTerrorismCheckRequest(IBAN, transferRequest.recipientAccount());

        var atcResponse = RestAssured.given()
                        .baseUri(antiTerrorismCheckServiceUrl)
                        .contentType(ContentType.JSON)
                        .body(atcRequest)
                        .post("/atc")
                        .as(AntiTerrorismCheckResponse.class);

        if (atcResponse.result() == AntiTerrorismCheckResult.POSITIVE) {
            return ResponseEntity.ok(new TransferResponse(true, null));
        } else {
            var message = format("Anti-terrorism check has failed with score=%.2f", atcResponse.score());

            return ResponseEntity.ok(new TransferResponse(false, message));
        }
    }
}
