package ee.fakeplastictrees.controller;

import ee.fakeplastictrees.model.SongDto;
import ee.fakeplastictrees.model.SongQuality;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.restassured.RestAssured.given;

@RestController
@RequestMapping("/play")
public class PlayerController {
    @Value("${dataServiceUrl}")
    private String dataServiceUrl;

    @PostMapping(value = "/")
    public ResponseEntity<Void> play(@RequestBody SongDto songDto) {
        int statusCode = request(songDto)
                .post("/store")
                .statusCode();

        return ResponseEntity.status(statusCode).build();
    }

    @PostMapping("/no-call")
    public ResponseEntity<Void> noCall(@RequestBody SongDto songDto) {
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/wrong-data")
    public ResponseEntity<Void> wrongData(@RequestBody SongDto songDto) {
        SongDto modifiedSongDto = new SongDto("Placebo", songDto.title(), songDto.duration(), SongQuality.MP3);

        request(modifiedSongDto)
                .post("/store")
                .then()
                .statusCode(200);

        return ResponseEntity.status(200).build();
    }

    private RequestSpecification request(SongDto body) {
        return given()
                .baseUri(dataServiceUrl)
                .contentType(ContentType.JSON)
                .body(body);
    }
}
