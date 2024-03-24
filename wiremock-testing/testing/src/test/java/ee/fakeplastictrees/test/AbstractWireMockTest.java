package ee.fakeplastictrees.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import ee.fakeplastictrees.test.transformer.LoanCalculatorRequestTransformer;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public abstract class AbstractWireMockTest {
    private final int port = 8090;

    private WireMockServer wireMockServer;

    @BeforeSuite
    public void beforeSuite() {
        // start the server
        wireMockServer = new WireMockServer(wireMockConfig()
                .port(port)
                .extensions(new LoanCalculatorRequestTransformer()));

        wireMockServer.start();

        // configure the client to connect to the same port
        WireMock.configureFor(port);
    }

    @BeforeMethod
    public void resetStubs() {
        // remove all mappings between tests
        WireMock.reset();
    }

    @AfterSuite
    public void afterSuite() {
        // stop the server after all tests
        wireMockServer.stop();
    }
}
