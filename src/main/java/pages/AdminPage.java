package pages;

import appmanager.HelperBase;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static appmanager.ApplicationManager.baseUrl;

public class AdminPage extends HelperBase {

    public AdminPage(WebDriver driver) {
        super(driver);
    }

    private JavascriptExecutor jse = (JavascriptExecutor) driver;
    private String postTitle = "The quick brown fox jumps over the lazy dog " + System.currentTimeMillis();
    private By addPostButtonLocator = By.xpath("//a[@href='post-new.php']");
    private By postTitleInputLocator = By.name("post_title");
    private By textTabLocator = By.id("content-html");
    private By textAreaLocator = By.className("wp-editor-area");
    private By publishPostButtonLocator = By.id("publish");
    private By hiddenPublishInputLocator = By.id("original_publish");
    private By successMessageLocator = By.id("message");
    private By helpLinkLocator = By.id("contextual-help-link");
    private By themeScreenshotBlankLocator = By.cssSelector("div.theme-screenshot.blank");

    public String url() throws URISyntaxException {
        URI uri = new URI(String.valueOf(driver.getCurrentUrl()));
        String path = uri.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public void gotoPostsPage() {
        click(By.linkText("Posts"));
    }

    public void addNewPostButtonClick() {
        click(addPostButtonLocator);
    }

    public void enterPostTitle() {
        try {
            type(postTitleInputLocator, postTitle);
        } catch (Exception e) {
            click(By.xpath("//div[@class='components-popover__content']/button"));//closeTipsPopUp
            type(By.id("post-title-0"), postTitle);
        }
    }

    public void enterTestContent() {
        try {
            click(textTabLocator);
            type(textAreaLocator, testContent());
        } catch (Exception e) {
            click(By.xpath("//div[@class='editor-inserter']"));
            click(By.xpath("//button[@class='editor-block-types-list__item editor-block-list-item-paragraph']"));
            type(By.id("mce_0"), readTextFile());
        }
    }

    public void publishPost(){
        click(By.cssSelector("button.components-button.editor-post-publish-panel__toggle.is-button.is-primary"));
        try{
            click(By.cssSelector("button.components-button.editor-post-publish-button.is-button.is-default.is-primary.is-large"));
            waitToBePresent(By.xpath("//div[text()='Published']"));
        } catch (Exception e) {
            click(By.cssSelector("button.components-button.editor-post-publish-button.is-button.is-default.is-primary.is-large"));
            waitToBePresent(By.xpath("//div[text()='Published']"));
        }
    }

//    public void publishPost() {
//        try {
//            click(By.cssSelector("button.components-button.editor-post-publish-panel__toggle.is-button.is-primary"));
//            click(By.cssSelector("button.components-button.editor-post-publish-button.is-button.is-default.is-primary.is-large"));
//            waitToBePresent(By.xpath("//div[text()='Published']"));
//        } catch (Exception e) {
////            scrollTillElementIsVisible(helpLinkLocator);
//            jse.executeScript("document.getElementById('original_publish').setAttribute('type', 'text')");//to change attribute of element
//            type(hiddenPublishInputLocator, "test");
//            submit(hiddenPublishInputLocator);
//            if (isAlertPresent()) {
//                driver.switchTo().alert().accept();
//            }
//            click(publishPostButtonLocator);
//            waitToBePresent(successMessageLocator);
//        }
//    }

    public void logoutFromAdmin() {
        click(By.cssSelector("li#wp-admin-bar-my-account"));
        hoverOnElement(By.cssSelector("li#wp-admin-bar-my-account"));
        waitTillElementIsVisible(By.cssSelector("li#wp-admin-bar-logout"));
        click(By.cssSelector("li#wp-admin-bar-logout"));
    }

    public void openTestPostUrl() {
        driver.get(baseUrl + postTitle.toLowerCase().replaceAll(" ", "-"));
    }

    public String testContent() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/test-content.txt")),
                StandardCharsets.UTF_8);
    }

    private String readLineByLineJava8() {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get("src/main/resources/test-content.txt"), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public String readTextFile() {
        StringBuilder returnValue = new StringBuilder();
        FileReader file;
        String line = "";
        try {
            file = new FileReader("src/main/resources/test-content.txt");
            try (BufferedReader reader = new BufferedReader(file)) {
                while ((line = reader.readLine()) != null) {
                    returnValue.append(line).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException("IO Error occured");
        }
        return returnValue.toString();
    }

    public boolean themeScreenshotIsBlank() {
        try {
            isElementPresent(themeScreenshotBlankLocator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}