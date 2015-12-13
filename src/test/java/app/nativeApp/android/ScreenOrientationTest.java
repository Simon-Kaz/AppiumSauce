package app.nativeApp.android;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

//This test suite verifies that he user is able to set and verify the device orientation.
public class ScreenOrientationTest {

    private String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private String SAUCE_KEY = System.getenv("SAUCE_KEY");
    private AppiumDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.android();
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Samsung Galaxy S4 Emulator");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "4.4");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.APP, "sauce-storage:ApiDemos-debug.apk");
        capabilities.setCapability("browserName", "");
        capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "io.appium.android.apis");
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".app.ScreenOrientation");
        capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, ".app.ScreenOrientation");
        capabilities.setCapability("deviceReadyTimeout", 40);
        capabilities.setCapability("newCommandTimeout", 180);
        capabilities.setCapability("appiumVersion", "1.4.16");

        this.driver = new AndroidDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                , capabilities);
        wait = new WebDriverWait(driver, 10);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void setOrientationToLandscapeTest() {
        setOrientation("LANDSCAPE");

        ScreenOrientation currentOrientation = driver.getOrientation();
        assertThat(currentOrientation, is(ScreenOrientation.LANDSCAPE));
    }

    @Test
    public void setOrientationToPortraitTest() {
        setOrientation("PORTRAIT");

        ScreenOrientation currentOrientation = driver.getOrientation();
        assertThat(currentOrientation, is(ScreenOrientation.PORTRAIT));
    }


    public void setOrientation(String orientation) {
        //wait until dropdown button is visible and click on it to expand the dropdown list
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("io.appium.android.apis:id/orientation"))))
                .click();

        //get a list of VISIBLE options (you will need to scroll down to access the elements out of view)
        List<WebElement> dropdownOptions = driver.findElements(MobileBy.AndroidUIAutomator("new UiSelector().className(android.widget.CheckedTextView)"));

        for (WebElement ele : dropdownOptions) {
            if (ele.getText().equals(orientation)) {
                ele.click();
                break;
            }
        }
    }


}
