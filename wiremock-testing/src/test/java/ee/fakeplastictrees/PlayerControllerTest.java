package ee.fakeplastictrees;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import ee.fakeplastictrees.model.SongDto;
import ee.fakeplastictrees.model.SongQuality;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerTest extends AbstractTestNGSpringContextTests {
    @Value("${serviceUrl}")
    private String serviceUrl;

    @LocalServerPort
    private int servicePort;

    @Value("${wireMockUrl}")
    private String wireMockUrl;

    @Value("${wireMockPort}")
    private Integer wireMockPort;

    private SongDto songDto;
    private String expectedJson;

    @PostConstruct
    public void prepareObjects() throws Exception {
        WireMock.configureFor(wireMockUrl, wireMockPort);

        songDto = new SongDto("Bryan Adams", "Summer of '69", 216, SongQuality.FLAC);
        expectedJson = new ObjectMapper().writeValueAsString(songDto);
    }

    @BeforeMethod
    public void resetStubs() {
        WireMock.reset();
        stubFor(post("/play").willReturn(ok()));
    }

    @Test
    public void positiveTest() throws JsonProcessingException {
        request(songDto)
                .post("/playPositive")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/play")));
        verify(postRequestedFor(urlEqualTo("/play")).withRequestBody(equalToJson(expectedJson)));
    }


    @Test
    public void negativeTestNoCall() {
        request(songDto)
                .post("/playNegativeNoCall")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/play")));
    }

    @Test
    public void negativeTestWrongData() throws JsonProcessingException {
        request(songDto)
                .post("/playNegativeWrongData")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/play")));
        verify(postRequestedFor(urlEqualTo("/play")).withRequestBody(equalToJson(expectedJson)));
    }

    private RequestSpecification request(SongDto body) {
        return given()
                .baseUri(format("%s:%d", serviceUrl, servicePort))
                .contentType(ContentType.JSON)
                .body(body);
    }
}
