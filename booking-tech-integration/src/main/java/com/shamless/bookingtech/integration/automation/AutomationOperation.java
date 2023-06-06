package com.shamless.bookingtech.integration.automation;

import com.shamless.bookingtech.integration.automation.model.ReturnAttitude;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AutomationOperation extends AutomationDriver {

    AutomationOperation(){
        super();
    }

    public void start(String path) {
        log.info("{} page is opening...", path);
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
        Thread.sleep(second * 1000);
    }

    public void click(WebElement webElement) {
        log.info("Clicking Web Element...");
        webElement.click();
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
        return driver.findElements(By.cssSelector(cssSelector));
    }

    public Optional<WebElement> findElementByCssSelector(String cssSelector, ReturnAttitude attitude) {
        List<WebElement> elements = driver.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            if (ReturnAttitude.ERROR.equals(attitude)) throw new NoSuchElementException("Element could not be found!");
            else if (ReturnAttitude.EMPTY.equals(attitude)) return Optional.empty();
            else throw new IllegalArgumentException("Select return attitude!");
        }
        return elements.stream().findFirst();
    }

    public Optional<WebElement> findElementById(String id) {
        try {
            return Optional.of(driver.findElement(By.id(id)));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }


}
