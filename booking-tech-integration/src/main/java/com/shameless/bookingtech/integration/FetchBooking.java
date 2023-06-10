package com.shameless.bookingtech.integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class FetchBooking {
    private static WebDriver driver;

    @BeforeClass
    public static void setup() {
        WebDriverManager.chromedriver().driverVersion("113.0.5672.63").setup();
        driver = new ChromeDriver();
        System.out.println();
    }

    @Test
    @Ignore
    public void testExample() throws InterruptedException {
        driver.get("https://www.booking.com/");
        Thread.sleep(30*1000);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        for (int i = 10; i > 0; i--) {
            WebElement element = driver.findElements(By.cssSelector("button.fc63351294.a822bdf511.e3c025e003.fa565176a8.f7db01295e.c334e6f658.ae1678b153"))
                    .stream().findFirst().orElse(null);
            if (element != null && element.isDisplayed()) {
                element.click();
                break;
            }
            Thread.sleep(3 * 1000);
        }
        WebElement locationInput = driver.findElement(By.id(":Ra9:"));
        locationInput.click();
        locationInput.sendKeys("Norwich");
        System.out.println("Bitti");
    }

    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
