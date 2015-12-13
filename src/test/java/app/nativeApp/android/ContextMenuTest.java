package app.nativeApp.android;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

//Test suite to verify that the user can access context menu in a native Android app
public class ContextMenuTest {

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
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".app.FragmentContextMenu");
        capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, ".app.FragmentContextMenu");
        capabilities.setCapability("deviceReadyTimeout", 40);
        capabilities.setCapability("newCommandTimeout", 180);
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("appiumVersion", "1.4.16");

        this.driver = new AndroidDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                ,capabilities);
        wait = new WebDriverWait(driver, 10);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void contextMenuTest() {
        WebElement longPress_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("io.appium.android.apis:id/long_press"))));
        TouchAction longPress_action = new TouchAction(driver);

        longPress_action.longPress(longPress_element, 2000).perform();

        //assert that the dialog menu is displayed after the long press
        assertThat(driver.findElement(By.id("android:id/select_dialog_listview")).isDisplayed(), is(true));
    }
}
