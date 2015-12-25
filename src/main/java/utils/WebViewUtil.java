package utils;

import io.appium.java_client.AppiumDriver;

import java.util.Set;

public class WebViewUtil {
    private AppiumDriver driver;

    public WebViewUtil(AppiumDriver driver) {
        this.driver = driver;
    }

    public void switchToWebView() {
        Set<String> listOfViews = driver.getContextHandles();
        listOfViews.stream().filter(view -> view.contains("WEBVIEW")).forEach(driver::context);
    }
}