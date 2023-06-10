package com.shameless.bookingtech.integration.automation;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) throws InterruptedException, MalformedURLException {
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
        /*options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");*/
        //options.addArguments("--window-size=1400,800");
        return options;
    }

    private void setDriver(ChromeOptions options, String path) {
        log.info("driver initializing...");
        driver = new ChromeDriver(options);
        log.info("{} page is opening", path);
        driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.MINUTES);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().setSize(new Dimension(1440, 900));
        driver.get(path);
    }

    private void setUserAgent(ChromeOptions options) {
        log.info("User-Agent fetching...");
        String useragent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        log.info("User-agent founded: {}", useragent);
        useragent = useragent.replace("HeadlessChrome", "Chrome");
        log.info("User-agent replaced with: {}", useragent);
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
