package app.nativeApp.android;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.LinkedList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(ConcurrentParameterized.class)
public class ParallelNativeAndroidTest implements SauceOnDemandSessionIdProvider {

    private String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private String SAUCE_KEY = System.getenv("SAUCE_KEY");
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(SAUCE_USERNAME, SAUCE_KEY);

    @Rule
    public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

    private String platformVersion;
    private String deviceName;
    private String sessionId;
    private AppiumDriver driver;
    private WebDriverWait wait;

    public ParallelNativeAndroidTest(String platformVersion, String deviceName) {
        super();
        this.platformVersion = platformVersion;
        this.deviceName = deviceName;
    }

    @ConcurrentParameterized.Parameters
    public static LinkedList devicesStrings() {
        LinkedList devices = new LinkedList();
        devices.add(new String[]{"4.3","Samsung Galaxy S3 Emulator"});
        devices.add(new String[]{"4.4","Samsung Galaxy S4 GoogleAPI Emulator"});
        devices.add(new String[]{"5.0","Android GoogleAPI Emulator"});
        devices.add(new String[]{"5.1","Android Emulator"});
        return devices;
    }

    @Before
    public void setUp() throws Exception {

        DesiredCapabilities capabilities = DesiredCapabilities.android();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);

        //other necessary capabilities
        capabilities.setCapability("name", "Parallel Android Native App Test Suite");
        capabilities.setCapability("build", "Parallel Android Native App Test Suite");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.APP, "sauce-storage:ApiDemos-debug.apk");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "io.appium.android.apis");
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".app.FragmentContextMenu");
        capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, ".app.FragmentContextMenu");
        capabilities.setCapability(MobileCapabilityType.DEVICE_READY_TIMEOUT, 40);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);
        capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.4.16");
        capabilities.setCapability("deviceOrientation", "portrait");

        this.driver = new AndroidDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                ,capabilities);
        this.sessionId = (driver.getSessionId()).toString();
        wait = new WebDriverWait(driver,10);

    }

    @Test
    public void contextMenuTest() throws Exception {
        WebElement longPress_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("io.appium.android.apis:id/long_press"))));
        TouchAction longPress_action = new TouchAction(driver);
        longPress_action.longPress(longPress_element, 2000).perform();

        //assert that the dialog menu is displayed after the long press
        assertThat(driver.findElement(By.id("android:id/select_dialog_listview")).isDisplayed(), is(true));

    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}
