package app.nativeApp.ios;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScrollingUtil;
import utils.WebViewUtil;

import java.net.URL;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class WebViewIssueIOSTest {

    private static String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
    private static String SAUCE_KEY = System.getenv("SAUCE_KEY");
    private static AppiumDriver driver;
    private static WebDriverWait wait;
    private static ScrollingUtil scrollingUtil;
    private static WebViewUtil webViewUtil;

    @BeforeClass
    public static void classSetUp() throws Exception {
        DesiredCapabilities capabilities = DesiredCapabilities.iphone();
        capabilities.setCapability("build", "Native WebView Support iOS Test Suite");
        capabilities.setCapability("name", "iPhone WebView Support 5s 9.2");

        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 5s");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
        capabilities.setCapability(MobileCapabilityType.APP, "sauce-storage:UICatalog.zip");
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
    public void webViewAddressBarTest(){
        //scroll to and click the web view button
        scrollingUtil.scrollToiOSUIAutomation("target.frontMostApp().mainWindow().tableViews()[0].cells().firstWithPredicate(\"name = 'web_view_button'\")")
                .click();
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(MobileBy.className("UIAWebView"))));
        //switch to web view
        webViewUtil.switchToWebView();
        String webView_URL = driver.getCurrentUrl();
        assertThat(webView_URL, is("http://www.apple.com/"));
    }

    @Test
    public void webViewManipulationTest(){
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
    }
}
