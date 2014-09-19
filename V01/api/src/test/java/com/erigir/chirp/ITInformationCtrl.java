package com.erigir.chirp;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 * Integration tests for information control
 * Created by chrweiss on 9/18/14.
 */
public class ITInformationCtrl {
    private static final Logger LOG = LoggerFactory.getLogger(ITInformationCtrl.class);

    @Test
    public void testServerInfo()
    {
        LOG.info("Integration tested information control");

        String server=System.getProperty("serverToTest");//  "chirp-a.elasticbeanstalk.com";
        assertNotNull("serverToTest env prop must be set",server);

        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.
        WebDriver driver = new HtmlUnitDriver();

        String url = "http://"+server+"/v1/info/server";
        // And now use this to visit Google
        driver.get(url);
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        String src = driver.getPageSource();


        // Find the text input element by its name
        ///WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        //element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        //element.submit();

        LOG.info("Got page: {}",src);
        assertNotNull(src);

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        /*(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });*/

        // Should see: "cheese! - Google Search"
        //System.out.println("Page title is: " + driver.getTitle());

        //Close the browser
        driver.quit();
        //fail();
    }
}
