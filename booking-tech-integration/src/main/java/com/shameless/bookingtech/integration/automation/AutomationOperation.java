package com.shameless.bookingtech.integration.automation;

import com.shameless.bookingtech.integration.automation.model.DeviceType;
import com.shameless.bookingtech.integration.automation.model.ReturnAttitude;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class AutomationOperation extends AutomationDriver {

    AutomationOperation(){
        super();
    }

    public void start(String path) throws InterruptedException, MalformedURLException {
        log.info("redirect to {}", path);
        executeDriverByPath(path);
        log.info("{} page is opened. Now it's loading...", path);
    }

    public void finish(){
        log.info("Session is closing...");
        terminateDriver();
        log.info("Session is closed successfully");
    }

    public void timeout(long second) throws InterruptedException {
        log.info("Waiting {} seconds...", second);
        timeoutDriver(second);
    }

    public void click(WebElement webElement) {
        log.info("Clicking Web Element...");
        try {
            webElement.click();
        }catch (ElementClickInterceptedException ex) {
            log.warn("Element can not be clicked. Trying again on javascript executor...");
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", webElement);
        }
        log.info("Clicked Web Element Successfully");
    }

    public void click(Optional<WebElement> webElement) {
        webElement.ifPresentOrElse(this::click, ()-> {
            log.error("Error occurred on click!");
            throw new NoSuchElementException("Error occurred on click!");
        });
    }

    public void sendKeys(String text, WebElement webElement) {
        log.info("Sending keys as: {}", text);
        webElement.sendKeys(text);
        log.info("Send keys successfully as: {}", text);
    }

    public void sendKeys(String text, Optional<WebElement> webElement) {
        webElement.ifPresentOrElse(we -> this.sendKeys(text, we), () -> System.out.println("hata"));

    }

    public List<WebElement> findElementsByCssSelector(String cssSelector) {
        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            log.warn("Element can not be found when fetching! Trying again execute script...");
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            elements = (List<WebElement>) jsExecutor.executeScript("return document.querySelectorAll(\"" + cssSelector + "\")");
        }
        return elements;
    }

    public Optional<WebElement> findElementByCssSelector(String cssSelector, ReturnAttitude attitude) {
        log.info("Elements fetching...");
        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            log.warn("Element can not be found when fetching! Trying again execute script...");
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            elements = (List<WebElement>) jsExecutor.executeScript("return document.querySelectorAll(\"" + cssSelector + "\")");
            if (elements.isEmpty()) {
                if (ReturnAttitude.ERROR.equals(attitude)) throw new NoSuchElementException("Element could not be found!");
                else if (ReturnAttitude.EMPTY.equals(attitude)) return Optional.empty();
                else throw new IllegalArgumentException("Select return attitude!");
            }
        }
        return elements.stream().findFirst();
    }

    public List<WebElement> findElementsByExecutor(WebElement parent, String cssSelector){
        log.info("Element fetching on execute script...");
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        List<WebElement> elements = (List<WebElement>) jsExecutor.executeScript(
                "return arguments[0].querySelectorAll(\"" + cssSelector + "\")", parent);
        return elements;
    }

    public Optional<WebElement> findElementById(String id) {
        try {
            return Optional.of(driver.findElement(By.id(id)));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Dimension getDim(){
        return getDimensionInfo();
    }

    public Map<String, Object> getCap(){
        return getChromeCapabilities();
    }


    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
