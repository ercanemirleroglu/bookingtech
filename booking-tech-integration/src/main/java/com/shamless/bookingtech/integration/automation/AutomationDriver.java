package com.shamless.bookingtech.integration.automation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;

public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) {
        System.setProperty("webdriver.gecko.driver", driverPath);
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("/opt/firefox/firefox");
        options.addArguments("-headless");
        //options.addArguments("--no-sandbox");
        driver = new FirefoxDriver(options);
        //driver.manage().window().maximize();
        driver.get(path);
    }

    protected void terminateDriver() {
        if (driver != null)
            driver.quit();
    }
}
