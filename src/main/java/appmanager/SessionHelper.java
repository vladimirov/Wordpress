package appmanager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SessionHelper extends HelperBase {

    public SessionHelper(WebDriver driver) {
        super(driver);
    }

    private By loginFormLocator = By.id("loginform");
    private By usernameLocator = By.name("log");
    private By passwordLocator = By.name("pwd");
    private By wpLoginButtonLocator = By.name("wp-submit");
    private By crmLoginButtonLocator = By.cssSelector("button.btn.btn-danger.btn-block.btn-flat");

    public void loginToAdmin(String username, String password) {
        waitToBePresent(loginFormLocator);
        click(loginFormLocator);
        type(usernameLocator, username);
        type(passwordLocator, password);
        click(wpLoginButtonLocator);
    }

    public void loginToCrm(String username, String password) {
        type(usernameLocator, username);
        type(passwordLocator, password);
        click(crmLoginButtonLocator);
    }


}
