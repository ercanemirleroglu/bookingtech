package com.shameless.bookingtech.integration.automation.infra;

import com.shameless.bookingtech.integration.automation.model.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AppDriver implements AppAutomation {
    private UUID id;
    private WebDriver driver;
    private DeviceType deviceType;

    public AppDriver(WebDriver driver, DeviceType deviceType) {
        this.id = UUID.randomUUID();
        this.driver = driver;
        this.deviceType = deviceType;
    }

    public UUID getId() {
        return id;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public JavascriptExecutor javaScriptExecutor() {
        return  (JavascriptExecutor) driver;
    }

    @Override
    public List<AppElement> findAllElementsByCssSelector(String cssSelector, JavascriptExecutor jsExecutor) {
        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            log.warn("Element can not be found when fetching! Trying again execute script...");
            elements = (List<WebElement>) jsExecutor.executeScript("return document.querySelectorAll(\"" + cssSelector + "\")");
        }
        return elements.stream().map(e -> AppElement.builder().element(e).build()).collect(Collectors.toList());
    }

    @Override
    public Optional<AppElement> findOneElementByCssSelector(String cssSelector, JavascriptExecutor jsExecutor) {
        log.info("Elements fetching...");
        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            log.warn("Element can not be found when fetching! Trying again execute script...");
            elements = (List<WebElement>) jsExecutor.executeScript("return document.querySelectorAll(\"" + cssSelector + "\")");
        }
        return elements.stream().map(e -> AppElement.builder().element(e).build()).findFirst();
    }

    @Override
    public List<AppElement> findAllElementsByExecutor(String cssSelector, JavascriptExecutor jsExecutor) {
        log.info("Element fetching on execute script...");
        List<WebElement> elements = (List<WebElement>) jsExecutor.executeScript(
                "return arguments[0].querySelectorAll(\"" + cssSelector + "\")", driver);
        return elements.stream().map(e -> AppElement.builder().element(e).build()).collect(Collectors.toList());
    }

    @Override
    public Optional<AppElement> findElementById(String id) {
        return Optional.of(driver.findElement(By.id(id))).map(e -> AppElement.builder().element((WebElement) e).build());
    }

    public void terminateDriver() throws InterruptedException {
        if (driver != null) {
            driver.close();
            timeoutDriver(5);
            driver.quit();
            timeoutDriver(5);
            driver = null;
        }
        deviceType = null;
    }

    protected void timeoutDriver(long second) throws InterruptedException {
        Thread.sleep(second * 1000);
    }

    public void refreshPage(String path) throws InterruptedException {
        if (driver != null) {
            log.info("Refreshing page for {}", path);
            driver.get(path);
        } else {
            log.warn("There is a problem about browser. It is reopening...");
            terminateDriver();
        }
    }

    public void screenShot() throws IOException {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("/Users/ercanemirleroglu/Documents/Projects/screenshots/screenshot.png"));
    }

    public void timeout(int seconds) {
        try {
            log.info("Timeout: {} seconds", seconds);
            Thread.sleep(seconds * 1000L);
        }catch (InterruptedException e) {
            log.error("Interrupted Exeption: ", e);
        }
    }

    public void get(String url) {
        driver.get(url);
    }
}
