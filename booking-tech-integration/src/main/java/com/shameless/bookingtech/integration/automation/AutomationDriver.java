package com.shameless.bookingtech.integration.automation;

import com.shameless.bookingtech.integration.automation.model.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AutomationDriver {
    @Value("${application.drivers.geckoDriverPath}")
    private String driverPath;
    @Value("${application.drivers.chromeDriverPath}")
    private String chromeDriverPath;
    @Value("${application.commands.killCompletely}")
    private boolean killCompletely;
    protected static WebDriver driver;
    protected static DeviceType deviceType;

    protected void executeDriverByPath(String path) throws InterruptedException, MalformedURLException {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = manageOptions();
        setDriver(options, path, false);
        setUserAgent(options);
        terminateDriver();
        setDriver(options, path, true);
    }

    private void setDeviceType() {
        //Dimension dimensionInfo = getDimensionInfo();
        int width = 400;
        int height = 300;
        log.info("Device width is : {}, height is : {}", width, height);
        deviceType = Arrays.stream(DeviceType.values())
                .filter(d -> d.getDeviceTypeSpec().widthCompability(getDimensionInfo().width))
                .findFirst().get();
        log.info("Device type is : {}", deviceType.name());
    }

    private ChromeOptions manageOptions() {
        log.info("Options settings...");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-debugging-address=0.0.0.0");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        //options.addArguments("--no-sandbox");
        options.addArguments("--window-size=400,300");
        return options;
    }

    private void setDriver(ChromeOptions options, String path, boolean isMax) {
        log.info("driver initializing...");
        driver = new ChromeDriver(options);
        log.info("{} page is opening", path);
        driver.manage().timeouts().pageLoadTimeout(2, TimeUnit.MINUTES);
        driver.manage().timeouts().implicitlyWait(45, TimeUnit.SECONDS);
        driver.get(path);
        if (isMax) {
            setDeviceType();
        }
    }

    private void setUserAgent(ChromeOptions options) {
        log.info("User-Agent fetching...");
        String useragent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        log.info("User-agent founded: {}", useragent);
        useragent = useragent.replace("HeadlessChrome", "Chrome");
        log.info("User-agent replaced with: {}", useragent);
        options.addArguments("user-agent=\\" + useragent);
    }

    protected void terminateDriver() throws InterruptedException {
        if (driver != null) {
            driver.close();
            timeoutDriver(5);
            driver.quit();
            timeoutDriver(5);
            driver = null;
            killProcess("chromedriver");
            killProcess("chrome");
            killProcess("google-chrome");
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

    protected Dimension getDimensionInfo(){
        return driver.manage().window().getSize();
    }

    protected Map<String, Object> getChromeCapabilities(){
        Capabilities chromeCapabilities = ((ChromeDriver) driver).getCapabilities();
        return chromeCapabilities.asMap();
    }

    public void screenShot() throws IOException {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File("/Users/ercanemirleroglu/Documents/Projects/screenshots/screenshot.png"));
    }

    private void killProcess(String processName){
        if (killCompletely) {
            log.info(">>>>>>> Starting kill process: {}", processName);
            String findProcessCommand = "pgrep -f " + processName;
            try {
                Process process = Runtime.getRuntime().exec(findProcessCommand);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                List<Integer> processIds = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    log.info("Line trim: {}", line.trim());
                    processIds.add(Integer.parseInt(line.trim()));
                }
                reader.close();
                process.waitFor();
                for (int processId : processIds) {
                    log.info("processId: {}", processId);
                    Runtime.getRuntime().exec("kill " + processId);
                }
            } catch (IOException | InterruptedException e) {
                log.error("killProcessError: {}", processName);
                e.printStackTrace();
            }
        }
    }
}
