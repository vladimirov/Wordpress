package appmanager;

import antlr.Tool;
import org.openqa.selenium.*;
import org.testng.internal.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperBase {

    protected WebDriver driver;

    public HelperBase(WebDriver driver) {
        this.driver = driver;
    }

    protected void click(By locator) {
        driver.findElement(locator).click();
    }

//    protected void type(By locator, String text) {
//        click(locator);
//        if (text != null) {
//            String existingText = driver.findElement(locator).getAttribute("value");
//            if (!text.equals(existingText)) {
//                driver.findElement(locator).clear();
//                driver.findElement(locator).sendKeys(text);
//            }
//        }
//    }

    protected void type(By locator, String text) {
        click(locator);
        driver.findElement(locator).sendKeys(text);
    }

    protected void attach(By locator, File file) {
        if (file != null) {
            driver.findElement(locator).sendKeys(file.getAbsolutePath());
        }
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void screenShot() {
        File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String filename = new SimpleDateFormat("yyyyMMddhhmmss'.png'").format(new Date());
        File dest = new File("C:\\Projects\\Wordpress/" + filename);
        Utils.copyFile(scr, dest);
    }

}
