package browser.desktop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class IE11Test {

    private String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private String SAUCE_KEY = System.getenv("SAUCE_KEY");
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability("version", "11");
        capabilities.setCapability("platform", Platform.WIN8_1);
        this.driver = new RemoteWebDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                ,capabilities);
    }

    @Test
    public void webDriver() throws Exception {
        driver.get("http://www.amazon.com/");
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver wdriver) {
                return ((JavascriptExecutor) driver).executeScript(
                        "return document.readyState"
                ).equals("complete");
            }
        });
        assertEquals("Amazon.com: Online Shopping for Electronics, Apparel, Computers, Books, DVDs & more", driver.getTitle());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
