package com.shamless.bookingtech.integration.automation;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = manageOptions();
        setDriver(options, path);
        setUserAgent(options);
        terminateDriver();
        setDriver(options, path);
    }

    private ChromeOptions manageOptions() {
        log.info("Options settings...");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-debugging-address=0.0.0.0");
        //options.addArguments("--remote-debugging-port=0");
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("user-agent=\\" + "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        //Map<String, Object> stringObjectMap = options.asMap();
        return options;
    }

    private void setDriver(ChromeOptions options, String path) {
        log.info("driver initializing...");
        driver = new ChromeDriver(options);
        //driver.manage().window().maximize();
        log.info("{} page is opening", path);
        driver.get(path);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
    }

    private void setUserAgent(ChromeOptions options) {
        log.info("User-Agent fetching...");
        String useragent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        log.info("User-agent setting... {}", useragent);
        options.addArguments("user-agent=\\" + useragent);
    }

    protected void terminateDriver() {
        if (driver != null)
            driver.quit();
    }

    protected void timeoutDriver(long second) throws InterruptedException {
        Thread.sleep(second * 1000);
    }

    public void refreshPage(String path) {
        if (driver != null) {
            log.info("Refreshing page for {}", path);
            driver.get(path);
        } else {
            log.warn("There is a problem about browser. It is reopening...");
            terminateDriver();
        }
    }
}
