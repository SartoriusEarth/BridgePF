package webdriver.tests;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sagebionetworks.bridge.TestConstants;

import play.libs.F.Callback;
import play.test.TestBrowser;

public class BaseIntegrationTest {

    private TestBrowser configureDriver(TestBrowser b) {
        // Just ignore the existing TestBrowser because it's not flexible enough to add a header.
        // Force server to act like this is the PD project
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability("phantomjs.page.customHeaders.Bridge-Host", "pd.sagebridge.org");
        PhantomJSDriver driver = new PhantomJSDriver(capabilities);
        
        TestBrowser browser = new TestBrowser(driver, TestConstants.TEST_BASE_URL);
        Window window = browser.manage().window();
        window.setSize(new Dimension(1024, 1400));
        browser.manage().deleteAllCookies();
        browser.manage().timeouts().pageLoadTimeout(300, TimeUnit.SECONDS);
        return browser;
    }

    private Callback<TestBrowser> wrapAndConfigureDriver(
            final Callback<TestBrowser> callback) {
        return new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) throws Throwable {
                callback.invoke(configureDriver(browser));
            }
        };
    }

    protected void call(Callback<TestBrowser> callback) {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                TestConstants.PHANTOMJS_DRIVER,
                wrapAndConfigureDriver(callback));
    }

}
