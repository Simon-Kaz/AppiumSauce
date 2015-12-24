package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class ScrollingUtil {
    private AppiumDriver driver;

    //default swipe deadzone, set to 20%
    private static final double DEFAULT_SWIPE_DEADZONE = 0.20;
    //default swipe duration, set to 1 second
    private static final int DEFAULT_SWIPE_DURATION = 1000;

    public static final int DEFAULT_SWIPE_COUNT = 3;

    public ScrollingUtil(AppiumDriver driver) {
        this.driver = driver;
    }

    public WebElement scrollToElement(WebElement elementToScrollTo) {
        return scrollToElement(elementToScrollTo, DEFAULT_SWIPE_COUNT);
    }

    public WebElement scrollToElement(WebElement elementToScrollTo, int maxSwipeCount) {
        if (isElementDisplayed(elementToScrollTo)) {
            return elementToScrollTo;
        } else {
            scrollUpScreenLength(maxSwipeCount);
            if (isElementDisplayed(elementToScrollTo)) {
                return elementToScrollTo;
            }
            for (int count = 0; count < maxSwipeCount; count++) {
                scrollDownScreenLength();
                if (isElementDisplayed(elementToScrollTo)) {
                    return elementToScrollTo;
                }
            }
            throw new NoSuchElementException(String.format("Cannot find element: %s)", elementToScrollTo));
        }
    }

    public void scrollDownScreenLength(int count) {

        int width = getScreenWidth();
        int height = getScreenHeight();

        //adjusting height to account for deadzone
        int swipeAreaAdjust = (int) (height * DEFAULT_SWIPE_DEADZONE);

        for (int i = 0; i < count; i++) {
            // width/2 - to get the center of the screen
            driver.swipe(width / 2, height - swipeAreaAdjust, width / 2, swipeAreaAdjust, DEFAULT_SWIPE_DURATION);
        }
    }

    public void scrollUpScreenLength(int count) {

        int width = getScreenWidth();
        int height = getScreenHeight();

        //adjusting height to account for deadzone
        int swipeAreaAdjust = (int) (height * DEFAULT_SWIPE_DEADZONE);

        for (int i = 0; i < count; i++) {
            // width/2 - to get the center of the screen
            driver.swipe(width / 2, swipeAreaAdjust, width / 2, height - swipeAreaAdjust, DEFAULT_SWIPE_DURATION);
        }
    }

    public void scrollDownScreenLength() {
        scrollDownScreenLength(1);
    }

    public void scrollUpScreenLength() {
        scrollUpScreenLength(1);
    }

    public WebElement scrollToiOSUIAutomation(String iosSelector) {
        String selectorString = String.format("%s.scrollToVisible();", iosSelector);
        return driver.findElement(MobileBy.IosUIAutomation(selectorString));
    }

    //test to verify that element is visible and enabled - isDisplayed does not always return correct results
    private boolean isElementDisplayed(WebElement element) {
        try {
            if (element.isDisplayed()) {
                return true;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }

    private int getScreenWidth() {
        return driver.manage().window().getSize().getWidth();
    }

    private int getScreenHeight() {
        return driver.manage().window().getSize().getHeight();
    }
}
