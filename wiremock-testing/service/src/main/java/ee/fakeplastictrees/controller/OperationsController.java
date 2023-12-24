package ee.fakeplastictrees.controller;

import static ee.fakeplastictrees.model.atc.AntiTerrorismCheckType.IBAN;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;

import ee.fakeplastictrees.model.atc.AntiTerrorismCheckRequest;
import ee.fakeplastictrees.model.atc.AntiTerrorismCheckResponse;
import ee.fakeplastictrees.model.atc.AntiTerrorismCheckResult;
import ee.fakeplastictrees.model.bank.TransferRequest;
import ee.fakeplastictrees.model.bank.TransferResponse;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// create a rest controller
@RestController
// all requests will start with /operations
@RequestMapping("/operations")
public class OperationsController {
  // read the ATC service url from application.properties
  @Value("${antiTerrorismCheckServiceUrl}")
  private String antiTerrorismCheckServiceUrl;

  // define an endpoint: /operations/transfer
  // it accepts a body described in TransferRequest
  @PostMapping(value = "/transfer")
  public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest) {
    // create a request for the ATC service
    // {
    //  "type": "IBAN",
    //  "identifier": "<from transferRequest>"
    // }
    var atcRequest = new AntiTerrorismCheckRequest(IBAN, transferRequest.recipientAccount());

    // make a request to the ATC service with atcRequest
    var atcResponse =
        given()
            .baseUri(antiTerrorismCheckServiceUrl)
            .contentType(ContentType.JSON)
            .body(atcRequest)
            .post("/ats")
            // deserialize the response to
            .as(AntiTerrorismCheckResponse.class);

    if (atcResponse.result() == AntiTerrorismCheckResult.POSITIVE) {
      // if the ATC check is positive, return a response saying that the transfer is completed
      // there will be no message as it's not needed
      return ResponseEntity.ok(new TransferResponse(true, null));
    } else {
      // if the ATC check is negative, return a response saying that the transfer is not completed
      // with a user-friendly error message
      var message = format("Anti-terrorism check has failed with score=%.2f", atcResponse.score());

      return ResponseEntity.ok(new TransferResponse(false, message));
    }
  }
}
