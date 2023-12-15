package ee.fakeplastictrees.controller;

import ee.fakeplastictrees.model.SongDto;
import ee.fakeplastictrees.model.SongQuality;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static io.restassured.RestAssured.given;

@RestController
public class PlayerController {
    @Value("${externalServiceUrl}")
    private String externalServiceUrl;

    @PostMapping(value = "/playPositive")
    public ResponseEntity<Void> playPositive(@RequestBody SongDto songDto) {
        request(songDto)
                .post("/play")
                .then()
                .statusCode(200);

        return ResponseEntity.status(200).build();
    }

    @PostMapping("/playNegativeNoCall")
    public ResponseEntity<Void> playNegativeNoCall(@RequestBody SongDto songDto) {
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/playNegativeWrongData")
    public ResponseEntity<Void> playNegativeWrongData(@RequestBody SongDto songDto) {
        SongDto modifiedSongDto = new SongDto("Placebo", songDto.title(), songDto.duration(), SongQuality.MP3);

        request(modifiedSongDto)
                .post("/play")
                .then()
                .statusCode(200);

        return ResponseEntity.status(200).build();
    }

    private RequestSpecification request(SongDto body) {
        return given()
                .baseUri(externalServiceUrl)
                .contentType(ContentType.JSON)
                .body(body);
    }
}
