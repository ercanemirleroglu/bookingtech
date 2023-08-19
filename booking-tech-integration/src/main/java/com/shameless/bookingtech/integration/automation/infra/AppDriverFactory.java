package com.shameless.bookingtech.integration.automation.infra;

import com.shameless.bookingtech.integration.automation.model.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AppDriverFactory {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    @Value("${application.automation.pageLoadTimeout}")
    private long pageLoadTimeout;
    @Value("${application.automation.implicitlyWait}")
    private long implicitlyWait;
    @Value("${application.automation.setScriptTimeout}")
    private long setScriptTimeout;

    public AppDriver createDriver(String path) throws MalformedURLException, InterruptedException {
        WebDriver driver = executeDriverByPath(path);
        DeviceType deviceType = setDeviceType(driver);
        return new AppDriver(driver, deviceType);
    }

    protected WebDriver executeDriverByPath(String path) throws InterruptedException, MalformedURLException {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = manageOptions();
        WebDriver driver = setDriver(options, path, false);
        setUserAgent(driver, options);
        terminateDriver(driver);
        return setDriver(options, path, true);
    }

    public void terminateDriver(WebDriver driver) throws InterruptedException {
        if (driver != null) {
            driver.close();
            Thread.sleep(5 * 1000);
            driver.quit();
            Thread.sleep(5 * 1000);
            driver = null;
        }
    }

    private ChromeOptions manageOptions() {
        log.info("Options settings...");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1440,900");
        return options;
    }

    private WebDriver setDriver(ChromeOptions options, String path, boolean isMax) {
        log.info("driver initializing...");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(setScriptTimeout, TimeUnit.SECONDS);
        driver.get(path);
        if (isMax) {
            setDeviceType(driver);
        }
        return driver;
    }

    private DeviceType setDeviceType(WebDriver driver) {
        Dimension dimensionInfo = getDimensionInfo(driver);
        log.info("Device width is : {}, height is : {}", dimensionInfo.width, dimensionInfo.height);
        DeviceType deviceType = Arrays.stream(DeviceType.values())
                .filter(d -> d.getDeviceTypeSpec().widthCompability(getDimensionInfo(driver).width))
                .findFirst().get();
        log.info("Device type is : {}", deviceType.name());
        return deviceType;
    }

    protected Dimension getDimensionInfo(WebDriver driver){
        return driver.manage().window().getSize();
    }

    private void setUserAgent(WebDriver driver, ChromeOptions options) {
        log.info("User-Agent fetching...");
        String useragent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        log.info("User-agent founded: {}", useragent);
        useragent = useragent.replace("HeadlessChrome", "Chrome");
        log.info("User-agent replaced with: {}", useragent);
        options.addArguments("user-agent=\\" + useragent);
    }
}
