package app.nativeApp.ios;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollingUtil;
import utils.WebViewUtil;

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
    private static WebViewUtil webViewUtil;

    @BeforeClass
    public static void classSetUp() throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.iphone();
        capabilities.setCapability("build", "Native iOS Test Suite");
        capabilities.setCapability("name", "iPhone 5s 9.2");

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 5s");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.APP, "sauce-storage:UICatalog.zip");
//        capabilities.setCapability(MobileCapabilityType.APP, "/Users/szymonk/Desktop/UICatalog.zip");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 180);
        capabilities.setCapability(MobileCapabilityType.DEVICE_READY_TIMEOUT, 60);
        capabilities.setCapability("deviceOrientation", "portrait");
        capabilities.setCapability("appiumVersion", "1.4.16");
        capabilities.setCapability("sendKeyStrategy", "setValue"); //fastest typing method
        capabilities.setCapability("autoLaunch", "false");
        capabilities.setCapability("noReset", true); //to reuse the simulator/installed app between tests, rather than restart sim

        driver = new IOSDriver(new URL("http://" + SAUCE_USERNAME + ":" + SAUCE_KEY + "@ondemand.saucelabs.com:80/wd/hub")
                , capabilities);
//        driver = new IOSDriver(new URL("http://0.0.0.0:4723/wd/hub")
//                , capabilities);

        wait = new WebDriverWait(driver, 15);
        scrollingUtil = new ScrollingUtil(driver);
        webViewUtil = new WebViewUtil(driver);
        System.out.println("Launching sim");
    }

    @Before
    public void methodSetUp() throws Exception {
        System.out.println("launching app");
        driver.launchApp();
        //wait until main view loads
        webViewUtil.switchToNativeView();
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

        // Using Joda Time DateTime and DateTimeFormatter for this test!
        // Creating test data - using +3 days, hours and minutes
        // to make sure that all wheels are used
        DateTime testDate = new DateTime().plusDays(3).plusHours(3).plusMinutes(3);

        // hour of the day - short format
        // i tried to get it using .hourOfDay().getAsShortText() but it would only return 24hr format...
        DateTimeFormatter hourOfDayFormat = DateTimeFormat.forPattern("K");

        // half day of day - AM or PM
        // have to use a new formatter for this as there is no getter - let me know if I'm wrong!
        DateTimeFormatter halfDayFormat = DateTimeFormat.forPattern("aa");

        // short date - example "Sun Dec 27"
        // to be used with the date picker in gregorian calendar
        DateTimeFormatter shortDateFormat = DateTimeFormat.forPattern("E MMM d");

        // longDate - example pattern "Dec 27, 2015, 5:55 PM"
        // to be used for assertion at the end of the test
        DateTimeFormatter longDateFormat = DateTimeFormat.forPattern("MMM d, yyyy, K:mm aa");

        final String hour = testDate.toString(hourOfDayFormat);
        final String minute = testDate.minuteOfHour().getAsString();
        final String shortDate = testDate.toString(shortDateFormat);
        final String longDate = testDate.toString(longDateFormat);
        final String halfDay = testDate.toString(halfDayFormat);


        driver.findElement(MobileBy.AccessibilityId("date_picker_button"))
                .click();
        //wait for Date Picker view to load
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.className("UIAPickerWheel"))));

        List<WebElement> pickerWheel_elements = driver.findElements(MobileBy.className("UIAPickerWheel"));
        pickerWheel_elements.get(0).sendKeys(shortDate);
        pickerWheel_elements.get(1).sendKeys(hour);
        pickerWheel_elements.get(2).sendKeys(minute);
        pickerWheel_elements.get(3).sendKeys(halfDay);

        String dateValidation_element = driver.findElement(MobileBy.AccessibilityId("current_date")).getText();
        assertThat(dateValidation_element, is(longDate));
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
    public void settingSliderValueTest() {
        final String expected_value = "75%";

        //sliders button is not in view, scroll to it using native Instruments scrolling method
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'sliders_button'\")")
                .click();

        //wait for slider to become visible
        //casting to IOSElement to access the .setValue method
        IOSElement default_slider = (IOSElement) wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.xpath("//UIASlider[@name='default_slider']"))));
        default_slider.setValue(expected_value);

        String current_value = default_slider.getAttribute("value");
        assertThat(current_value, is(expected_value));
    }

    @Test
    public void stepperTest() {
        //scroll to and click the steppers button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'steppers_button'\")")
                .click();

        //wait for steppers view to load
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.AccessibilityId("default_stepper_value"))));

        //store some web elements for later use
        WebElement decrement_button = driver.findElement(MobileBy.xpath("//UIATableCell[@name='default_stepper_value']/UIAButton[1]"));
        WebElement increment_button = driver.findElement(MobileBy.xpath("//UIATableCell[@name='default_stepper_value']/UIAButton[2]"));

        //store current value and calculate the expected one
        int original_value = Integer.parseInt(driver.findElement(MobileBy.xpath("//UIAStaticText[@name='default_stepper_value']")).getAttribute("value"));
        int increment_value = 10;
        int expected_value = original_value + increment_value;

        for (int i = 0; i < increment_value; i++) {
            increment_button.click();
        }
        int current_value = Integer.parseInt(driver.findElement(MobileBy.xpath("//UIAStaticText[@name='default_stepper_value']")).getAttribute("value"));
        assertThat(current_value, is(expected_value));
        //assert that decrement button is disabled
        assertThat(decrement_button.isEnabled(), is(true));

        for (int i = 0; i < increment_value; i++) {
            decrement_button.click();
        }
        current_value = Integer.parseInt(driver.findElement(MobileBy.xpath("//UIAStaticText[@name='default_stepper_value']")).getAttribute("value"));
        assertThat(current_value, is(original_value));
        //assert that decrement button is disabled
        assertThat(decrement_button.isEnabled(), is(false));
    }

    @Test
    public void switchTest() {
        //scroll to and click the steppers button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'switches_button'\")")
                .click();

        //wait for switches view to load
        WebElement defaultSwitch_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.xpath("//UIASwitch[@name='default_switch']"))));

        //switch is ON by default (ON = value set to 1)
        assertThat(defaultSwitch_element.getAttribute("value"), is("1"));

        defaultSwitch_element.click();
        //switch should be OFF (OFF = value set to 0)
        assertThat(defaultSwitch_element.getAttribute("value"), is("0"));
        //if assertion is intermittent, try adding the wait below
        //wait.until(ExpectedConditions.textToBePresentInElementValue(default_switch, "0"));

        IOSElement tintedSwitch_element = (IOSElement) driver.findElement(MobileBy.xpath("//UIASwitch[@name='tinted_switch']"));

        //confirm that the switch is on by default
        assertThat(tintedSwitch_element.getAttribute("value"), is("1"));
        //set the value to 0 = OFF
        tintedSwitch_element.setValue("0");
        //switch should be OFF
        assertThat(tintedSwitch_element.getAttribute("value"), is("0"));
    }

    @Test
    public void textFieldTest() {

        final String sendKeysString = "testing textfield input with send keys";
        final String setValueString = "testing textfield input with setValue";

        //scroll to and click the text fields button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'text_fields_button'\")")
                .click();

        //using sendKeys and asserting via getText
        WebElement defaultTextField_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.xpath("//UIATextField[@name='default_text_field']"))));
        defaultTextField_element.sendKeys(sendKeysString);
        assertThat(defaultTextField_element.getText(), is(sendKeysString));

        //clear text field using .clear
        defaultTextField_element.clear();
        assertThat(defaultTextField_element.getText(), is("Placeholder text"));

        //scrolling to element first
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'tinted_text_field'\")");

        //using setValue and asserting via attribute "value"
        IOSElement tintedTextField_element = (IOSElement) driver.findElement(MobileBy.xpath("//UIATextField[@name='tinted_text_field']"));
        tintedTextField_element.setValue(setValueString);
        assertThat(tintedTextField_element.getAttribute("value"), is(setValueString));

        //clear text using setValue
        tintedTextField_element.setValue("");
        assertThat(defaultTextField_element.getText(), is("Placeholder text"));
    }

    @Test
    public void textViewTest() {
        final String defaultTextView_value = "This is a UITextView that uses attributed text. You can programmatically modify the display of the text by making it bold, highlighted, underlined, tinted, and more. These attributes are defined in NSAttributedString.h. You can even embed attachments in an NSAttributedString!\u2028￼";
        final String sendKeysString = "replacing text with sendKeys";
        final String setValueString = "replacing text with setValue";
        //scroll to and click the text view button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'text_view_button'\")")
                .click();
        WebElement textView_element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.xpath("//UIATextView[@name='text_view']"))));
        assertThat(textView_element.getAttribute("value"), is(defaultTextView_value));

        //replacing text in text view with sendKeys
        textView_element.sendKeys(sendKeysString);
        assertThat(textView_element.getAttribute("value"), is(sendKeysString));

        //replacing text in text view with setValue
        ((IOSElement) textView_element).setValue(setValueString);
        assertThat(textView_element.getAttribute("value"), is(setValueString));
    }

    @Test
    public void webViewManipulationTest() {
        //scroll to and click the web view button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'web_view_button'\")")
                .click();
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.className("UIAWebView"))));
        //switch to web view
        webViewUtil.switchToWebView();
        //switch to a different page
        driver.get("https://www.imgur.com");
        //wait for element to load
        WebElement header_element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#header")));
        assertThat(header_element.isDisplayed(), is(true));
        String webView_URL = driver.getCurrentUrl();
        assertThat(webView_URL, is("https://m.imgur.com/"));
    }
}
