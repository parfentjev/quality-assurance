package ee.fakeplastictrees.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class AbstractWireMockTest {
    private WireMockServer wireMockServer;

    @BeforeSuite
    public void beforeSuite() {
        // start the server
        wireMockServer = new WireMockServer(options().port(8090));
        wireMockServer.start();

        // configure the client to connect to the same port
        WireMock.configureFor(8090);
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
