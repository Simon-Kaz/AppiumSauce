package app.nativeApp.ios;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollingUtil;

import java.net.URL;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class NativeIOSTest {

    private static String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private static String SAUCE_KEY = System.getenv("SAUCE_KEY");
    private static AppiumDriver driver;
    private static WebDriverWait wait;
    private static ScrollingUtil scrollingUtil;

    @BeforeClass
    public static void classSetUp() throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.iphone();
        capabilities.setCapability("build", "Native iOS Test Suite");
        capabilities.setCapability("name", "iPhone 5s 9.2");

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 5s");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
//        capabilities.setCapability(MobileCapabilityType.APP, "sauce-storage:UICatalog.zip");
        capabilities.setCapability(MobileCapabilityType.APP, "/Users/szymonk/Desktop/UICatalog.app");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);
        capabilities.setCapability(MobileCapabilityType.DEVICE_READY_TIMEOUT, 60);
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("appiumVersion", "1.4.16");
        capabilities.setCapability("sendKeyStrategy", "setValue"); //fastest typing method
        capabilities.setCapability("autoLaunch", "false");
        capabilities.setCapability("noReset", true); //to reuse the simulator/installed app between tests, rather than restart sim

//        driver = new IOSDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
//                , capabilities);
        driver = new IOSDriver(new URL("http://0.0.0.0:4723/wd/hub")
                , capabilities);

        wait = new WebDriverWait(driver, 15);
        scrollingUtil = new ScrollingUtil(driver);
        System.out.println("Launching sim");
    }

    @Before
    public void methodSetUp() throws Exception {
        System.out.println("launching app");
        driver.launchApp();
        //wait until main view loads
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.AccessibilityId("date_picker_button"))));
    }

    @After
    public void methodTearDown() throws Exception {
        System.out.println("closing app");
        driver.closeApp();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        driver.quit();
    }


    @Test
    public void datePickerTest() throws Exception {
        final String date = "Sun Dec 27";
        final String hour = "5";
        final String minute = "55";
        final String timePeriod = "PM";
        final String fullDate = "Dec 27, 2015, 5:55 PM";

        driver.findElement(MobileBy.AccessibilityId("date_picker_button"))
                .click();
        //wait for Date Picker view to load
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.className("UIAPickerWheel"))));

        List<WebElement> pickerWheel_elements = driver.findElements(MobileBy.className("UIAPickerWheel"));
        pickerWheel_elements.get(0).sendKeys(date);
        pickerWheel_elements.get(1).sendKeys(hour);
        pickerWheel_elements.get(2).sendKeys(minute);
        pickerWheel_elements.get(3).sendKeys(timePeriod);

        String dateValidation_element = driver.findElement(MobileBy.AccessibilityId("current_date")).getText();
        assertThat(dateValidation_element, is(fullDate));
    }


    @Test
    public void handlingSimpleAlertTest() {
        final String expected_alert_text = "A Short Title Is Best A message should be a short, complete sentence.";

        driver.findElement(MobileBy.AccessibilityId("alert_views_button"))
                .click();
        //wait for alert view to load by waiting for "simple" alert button
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.AccessibilityId("simple_alert_button"))))
                //and click on it
                .click();
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String titleAndMessage = alert.getText();

        assertThat(titleAndMessage, is(expected_alert_text));
        alert.accept();
    }


    @Test
    public void textInputAlertTest() {
        driver.findElement(MobileBy.AccessibilityId("alert_views_button"))
                .click();
        driver.findElement(MobileBy.AccessibilityId("text_entry_alert_button"))
                .click();
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String titleAndMessage = alert.getText();
        assertThat(titleAndMessage, is("A Short Title Is Best A message should be a short, complete sentence."));

        //input text
        String text_alert_message = "testing alert text input field";

        alert.sendKeys(text_alert_message);
        String alertTextInputField_value = driver.findElement(MobileBy.xpath("//UIAAlert//UIATextField")).getText();
        assertThat(alertTextInputField_value, is(text_alert_message));
    }


    @Test
    public void secureTextAlertTest() {
        final String secure_text_alert_message = "testing secure alert text input field";

        String stars = "";
        for (int i = 0; i < secure_text_alert_message.length(); i++) {
            stars = stars.concat("•");
        }

        driver.findElement(MobileBy.AccessibilityId("alert_views_button"))
                .click();
        driver.findElement(MobileBy.AccessibilityId("secure_text_entry_alert_button"))
                .click();
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        String titleAndMessage = alert.getText();
        assertThat(titleAndMessage, is("A Short Title Is Best A message should be a short, complete sentence."));

        //TODO: Verify why alert.sendKeys does not work - is it because its a secure text field?
        //alert.sendKeys(secure_text_alert_message);
        WebElement alertTextInput_field = driver.findElement(MobileBy.xpath("//UIAAlert//UIASecureTextField"));
        alertTextInput_field.sendKeys(secure_text_alert_message);

        String alertTextInputField_value = alertTextInput_field.getText();
        assertThat(alertTextInputField_value, is(not(secure_text_alert_message))); //assert that the message is NOT displayed
        assertThat(alertTextInputField_value, is(stars)); //assert that the message is NOT displayed
    }

    @Test
    public void defaultProgressBarTest() {
        driver.findElement(MobileBy.AccessibilityId("progress_views_button"))
                .click();
        //wait until loading is finished (by waiting for value to be 100%)
        wait.until(ExpectedConditions.textToBePresentInElementValue(driver.findElement(MobileBy.xpath("//UIAProgressIndicator[@name='default_progress_bar']")), "100%"));
        assertThat(driver.findElement(MobileBy.xpath("//UIAProgressIndicator[@name='default_progress_bar']")).getAttribute("value"), is("100%"));
    }

    @Test
    public void settingSliderValueTest(){
        final String expected_value = "75%";

        //sliders button is not in view, scroll to it using native Instruments scrolling method
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'sliders_button'\")")
        .click();

        //wait for slider to be visible
        //casting to IOSElement to access the .setValue method
        IOSElement default_slider = (IOSElement)wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.xpath("//UIASlider[@name='default_slider']"))));
        default_slider.setValue(expected_value);

        String current_value = default_slider.getAttribute("value");
        assertThat(current_value, is(expected_value));
    }
}
