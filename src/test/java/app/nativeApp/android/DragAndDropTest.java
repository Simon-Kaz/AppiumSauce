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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class DragAndDropTest {

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
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".view.DragAndDropDemo");
        capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, ".view.DragAndDropDemo");
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
    public void dragAndDropOnFirstElementTest() {
        //find elements and store them for later use
        WebElement draggable_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("io.appium.android.apis:id/drag_dot_1"))));
        WebElement rightDroppable_element = driver.findElement(By.id("io.appium.android.apis:id/drag_dot_2"));

        //create a drag and drop action using TouchAction
        TouchAction action = new TouchAction(driver);
        action.longPress(draggable_element, 1000).moveTo(rightDroppable_element).release().perform();

        String result_text = driver.findElement(By.id("io.appium.android.apis:id/drag_result_text")).getText();
        assertThat(result_text, is("Dropped!"));

        String drag_text = driver.findElement(By.id("io.appium.android.apis:id/drag_text")).getText();
        assertThat(drag_text, containsString("app:id/drag_dot_1"));
    }
}
