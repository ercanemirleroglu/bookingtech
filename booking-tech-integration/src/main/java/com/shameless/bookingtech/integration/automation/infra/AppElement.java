package com.shameless.bookingtech.integration.automation.infra;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class AppElement implements AppAutomation {
    private WebElement element;

    public void click(JavascriptExecutor jsExecutor) {
        log.info("Clicking Web Element...");
        try {
            element.click();
        }catch (ElementClickInterceptedException ex) {
            log.warn("Element can not be clicked. Trying again on javascript executor...");
            jsExecutor.executeScript("arguments[0].click();", element);
        }
        log.info("Clicked Web Element Successfully");
    }

    public void sendKeys(String text) {
        log.info("Sending keys as: {}", text);
        element.sendKeys(text);
        log.info("Send keys successfully as: {}", text);
    }

    @Override
    public List<AppElement> findAllElementsByCssSelector(String cssSelector, JavascriptExecutor jsExecutor) {
        List<WebElement> elements = element.findElements(By.cssSelector(cssSelector));
        if (elements.isEmpty()) {
            log.warn("Element can not be found when fetching! Trying again execute script...");
            elements = (List<WebElement>) jsExecutor.executeScript("return document.querySelectorAll(\"" + cssSelector + "\")");
        }
        return elements.stream().map(e -> AppElement.builder().element(e).build()).collect(Collectors.toList());
    }

    @Override
    public Optional<AppElement> findOneElementByCssSelector(String cssSelector, JavascriptExecutor jsExecutor) {
        log.info("Elements fetching...");
        List<WebElement> elements = element.findElements(By.cssSelector(cssSelector));
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
                "return arguments[0].querySelectorAll(\"" + cssSelector + "\")", element);
        return elements.stream().map(e -> AppElement.builder().element(e).build()).collect(Collectors.toList());
    }

    @Override
    public Optional<AppElement> findElementById(String id) {
        return Optional.of(element.findElement(By.id(id))).map(e -> AppElement.builder().element((WebElement) e).build());
    }

    public String getText() {
        return element.getText();
    }

    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    public String getAttribute(String attr) {
        return element.getAttribute(attr);
    }
}
