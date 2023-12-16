package ee.fakeplastictrees;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import ee.fakeplastictrees.model.SongDto;
import ee.fakeplastictrees.model.SongQuality;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
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
    public void setUp() throws Exception {
        WireMock.configureFor(wireMockUrl, wireMockPort);

        songDto = new SongDto("Bryan Adams", "Summer of '69", 216, SongQuality.FLAC);
        expectedJson = new ObjectMapper().writeValueAsString(songDto);
    }

    @BeforeMethod
    public void resetStubs() {
        WireMock.reset();
    }

    @AfterClass
    public void tearDown() {
        WireMock.removeAllMappings();
    }

    @Test
    public void positiveTest() {
        stubFor(post("/store").willReturn(ok()));

        request(songDto)
                .post("/play/")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/store")));
        verify(postRequestedFor(urlEqualTo("/store")).withRequestBody(equalToJson(expectedJson)));
    }


    @Test
    public void negativeTestNoCall() {
        stubFor(post("/store").willReturn(ok()));

        request(songDto)
                .post("/play/no-call")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/store")));
    }

    @Test
    public void negativeTestWrongData() {
        stubFor(post("/store").willReturn(ok()));

        request(songDto)
                .post("/play/wrong-data")
                .then()
                .statusCode(200);

        verify(exactly(1), postRequestedFor(urlEqualTo("/store")));
        verify(postRequestedFor(urlEqualTo("/store")).withRequestBody(equalToJson(expectedJson)));
    }

    @Test
    public void multipleStates() {
        stubFor(post("/store")
                .inScenario("Repeat")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok())
                .willSetStateTo("STORED"));

        stubFor(post("/store")
                .inScenario("Repeat")
                .whenScenarioStateIs("STORED")
                .willReturn(badRequest()));

        request(songDto)
                .post("/play/")
                .then()
                .statusCode(200);

        request(songDto)
                .post("/play/")
                .then()
                .statusCode(400);

        verify(exactly(2), postRequestedFor(urlEqualTo("/store")));
    }

    private RequestSpecification request(SongDto body) {
        return given()
                .baseUri(format("%s:%d", serviceUrl, servicePort))
                .contentType(ContentType.JSON)
                .body(body);
    }
}
