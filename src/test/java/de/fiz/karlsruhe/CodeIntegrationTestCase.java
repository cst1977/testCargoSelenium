package de.fiz.karlsruhe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class CodeIntegrationTestCase {

    private static final Logger logger = Logger.getLogger(CodeIntegrationTestCase.class);
    public static WebDriver driver;
    private static String SELENIUM_HUB_URL;
    private static String TARGET_SERVER_URL;
    private static String CORE_URL;

    @BeforeClass
    public static void setup() throws MalformedURLException, InterruptedException {
        SELENIUM_HUB_URL = getConfigurationProperty("SELENIUM_HUB_URL",
                                                    "test.selenium.hub.url",
                                                    "http://localhost:8888/wd/hub");
        logger.info("using Selenium hub at: " + SELENIUM_HUB_URL);

        TARGET_SERVER_URL = getConfigurationProperty("TARGET_SERVER_URL",
                                                     "test.target.server.url",
                                                     "http://localhost:8080/browser");
        logger.info("using target server at: " + TARGET_SERVER_URL);
        
        CORE_URL = getConfigurationProperty("CORE_URL","test.core.url",
                                            "http://localhost:8080");
        logger.info("using core url: " + CORE_URL);
        
        String PROXY = "proxy:8888";
        String nonProxyHosts = "localhost, 127.0.0.1, 141.66.74.88, 141.66.17.116, 141.66.0.0/16, .fiz-karlsruhe.de, escidev2:8484";
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(PROXY)
             .setFtpProxy(PROXY)
             .setSslProxy(PROXY)
             .setNoProxy(nonProxyHosts);
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);
        cap.setBrowserName("firefox");
        cap.setPlatform(org.openqa.selenium.Platform.ANY);
        cap.setJavascriptEnabled(true);
        
        //DesiredCapabilities capability = DesiredCapabilities.firefox();
        //capability.setBrowserName("firefox");
        //capability.setPlatform(org.openqa.selenium.Platform.ANY);
        
        /*
         * java -jar \
           /home/cst/Dokumente/Selenium/selenium-server-standalone-2.26.0.jar \
           -role hub -port 8888 >/dev/null &
         * 
         * java -jar \
           /home/cst/Dokumente/Selenium/selenium-server-standalone-2.26.0.jar \
           -role webdriver \ -hub http://localhost:8888/grid/register \ -browser
           browserName=firefox,platform=LINUX \ 
           >/dev/null &
         */
        //driver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), cap);
        driver = new FirefoxDriver(cap);
        driver.navigate().to(TARGET_SERVER_URL);
    }

    private static String getConfigurationProperty(String envKey, 
                                                   String sysKey, 
                                                   String defValue) {
        String retValue = defValue;
        String envValue = System.getenv(envKey);
        String sysValue = System.getProperty(sysKey);
        // system property prevails over environment variable
        if (sysValue != null) {
            retValue = sysValue;
        } else if (envValue != null) {
            retValue = envValue;
        }
        return retValue;
    }

    @Test
    public void test_first() throws InterruptedException {
        Thread.sleep(3000);
        driver.findElement(By.xpath("//input[@type='text']")).clear();
        driver.findElement(By.xpath("//input[@type='text']")).sendKeys(CORE_URL);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }
}