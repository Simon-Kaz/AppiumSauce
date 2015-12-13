package browser.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class IOSWebTest {

    private String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private String SAUCE_KEY = System.getenv("SAUCE_KEY");
    private AppiumDriver driver;

    @Before
    public void setUp() throws Exception {

        DesiredCapabilities capabilities = DesiredCapabilities.iphone();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"iPhone 6 Plus");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION,"9.2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Safari");
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("appiumVersion", "1.4.16");

        this.driver = new IOSDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                ,capabilities);
    }

    @Test
    public void iOSTest() throws Exception {
        driver.get("http://www.amazon.com/");
        assertEquals("Amazon.com", driver.getTitle());
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}


