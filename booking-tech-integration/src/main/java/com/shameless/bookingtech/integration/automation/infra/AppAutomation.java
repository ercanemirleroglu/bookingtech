package com.shameless.bookingtech.integration.automation.infra;

import org.openqa.selenium.JavascriptExecutor;

import java.util.List;
import java.util.Optional;

public interface AppAutomation {

    List<AppElement> findAllElementsByCssSelector(String cssSelector, JavascriptExecutor jsExecutor);

    Optional<AppElement> findOneElementByCssSelector(String cssSelector, JavascriptExecutor jsExecutor);

    List<AppElement> findAllElementsByExecutor(String cssSelector, JavascriptExecutor jsExecutor);

    Optional<AppElement> findElementById(String id);

}
