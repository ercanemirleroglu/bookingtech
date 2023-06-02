package com.shamless.bookingtech.integration.automation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class AutomationDriver {
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) {
        WebDriverManager.chromedriver().driverVersion("113.0.5672.63").setup();
        driver = new ChromeDriver();
        driver.get(path);
    }

    protected void terminateDriver() {
        if (driver != null)
            driver.quit();
    }
}
