package com.shamless.bookingtech.integration.automation;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    protected static WebDriver driver;

    protected void executeDriverByPath(String path) throws InterruptedException, MalformedURLException {
        System.setProperty("webdriver.gecko.driver", driverPath);
        FirefoxOptions options = manageOptions();
        setDriver(options, path);
        //setUserAgent(options);
        //terminateDriver();
        //setDriver(options, path);
    }

    private FirefoxOptions manageOptions() {
        log.info("Options settings...");
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("--remote-debugging-address=0.0.0.0");
        //options.addArguments("--remote-debugging-port=0");
        options.setHeadless(true);
        //options.addArguments("user-agent=\\" + "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        //options.setCapability("detach", true);
        //Map<String, Object> stringObjectMap = options.asMap();
        return options;
    }

    private void setDriver(FirefoxOptions options, String path) throws MalformedURLException {
        log.info("driver initializing...");
        //DesiredCapabilities capabilities = new DesiredCapabilities();

        //capabilities.setBrowserName("firefox");
        //capabilities.setPlatform(Platform.LINUX);
        //capabilities.setVersion("114.0.5735.106");
        //capabilities.setCapability("goog:chromeOptions", options);
        driver = new FirefoxDriver(options);

        //driver.manage().window().maximize();
        log.info("{} page is opening", path);
        driver.get(path);
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
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
