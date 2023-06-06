package com.shamless.bookingtech.integration.automation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-debugging-address=0.0.0.0");
        //options.addArguments("--remote-debugging-port=0");
        options.addArguments("--headless=new");
        //options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        //options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        //driver.manage().window().maximize();
        driver.get(path);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    }

    protected void terminateDriver() {
        if (driver != null)
            driver.quit();
    }

    protected void timeoutDriver(long second) throws InterruptedException {
        Thread.sleep(second * 1000);
    }
}
