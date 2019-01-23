package appmanager;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.FileUpload;
import org.gitlab4j.api.models.Project;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Properties;

public class ApplicationManager {
    private static String OS = System.getProperty("os.name").toLowerCase();
    private WebDriver driver;
    private Logger logger = LoggerFactory.getLogger(HelperBase.class);
    public final Properties properties;
    public final Properties gitlabProperties;

    private LoginPage loginPage;
    private SitePage sitePage;
    private PageSpeedPage pageSpeedPage;
    private AdminPage adminPage;
    private FaviconPage faviconPage;
    public String postTitle = "The quick brown fox jumps over the lazy dog " + System.currentTimeMillis();
    public static String baseUrl;
    public static String adminLogin;
    public static String adminPassword;
    public static String gitlabHostUrl;
    public static String gitlabApiToken;
    public static String pageSpeedUrl;
    public static String projectId;
    public static String databaseUrl;
    public static String databaseUser;
    public static String databasePass;
    Capabilities capabilities;


    public ApplicationManager() {
        properties = new Properties();
        gitlabProperties = new Properties();
    }

    public void init(String browser) throws IOException {
        if (OS.contains("win")) {
            driver = DriverFactory.initWindowsDriver(browser);
            logger.info("WINDOWS OS DETECTED");
        } else if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0) {
            driver = DriverFactory.initLinuxDriver(browser);
            logger.info("LINUX OS DETECTED");
        } else if (OS.contains("mac")) {
            driver = DriverFactory.initMacDriver(browser);
            logger.info("MAC OS DETECTED");
        } else {
            System.out.println("Unknown OS");
        }
        driver.manage().window().maximize();
        String target = System.getProperty("target", "local");
        properties.load(new FileReader(new File(String.format("src/main/resources/local.properties", target))));
        gitlabProperties.load(new FileReader(new File(String.format("src/main/resources/gitlab.properties", target))));
        gitlabHostUrl = gitlabProperties.getProperty("gitlabHostUrl");
        gitlabApiToken = gitlabProperties.getProperty("gitlabApiToken");
        baseUrl = properties.getProperty("web.baseUrl");
        adminLogin = properties.getProperty("web.adminLogin");
        adminPassword = properties.getProperty("web.adminPassword");
        pageSpeedUrl = properties.getProperty("web.pageSpeedUrl");
        projectId = properties.getProperty("projectId");
        databaseUrl = properties.getProperty("databaseUrl");
        databaseUser = properties.getProperty("databaseUser");
        databasePass = properties.getProperty("databasePass");
        pageSpeedPage = new PageSpeedPage(driver);
        adminPage = new AdminPage(driver);
        sitePage = new SitePage(driver);
        faviconPage = new FaviconPage(driver);
        capabilities = ((RemoteWebDriver) driver).getCapabilities();
    }

    public void stop() {
        driver.quit();
    }

    public String browserName() {
        return capabilities.getBrowserName().toUpperCase();
    }

    public String postTitle() {
        return postTitle;
    }

    public String browserVersion() {
        return capabilities.getVersion().toString();
    }

    public void loginToAdmin() {
        driver.get(baseUrl + "wp-admin/");
        loginPage = new LoginPage(driver);
        loginPage.loginToAdmin(adminLogin, adminPassword);
    }

    public void openBaseUrl() {
        driver.get(baseUrl);
    }

    public void openPostsPageInAdmin() {
        driver.get(baseUrl + "wp-admin/edit.php");
    }

    public void openTestPostUrl() {
        driver.get(baseUrl + postTitle.toLowerCase().replaceAll(" ", "-"));
    }

    public void openPageSpeedUrl() {
        driver.get("https://developers.google.com/speed/pagespeed/insights/");
    }

    public void openPageNotFoundUrl() {
        driver.get(baseUrl + "404");
    }

    public void openSearchPageUrl() {
        driver.get(baseUrl + "?s=");
    }

    public void openThemesPage() {
        driver.get(baseUrl + "wp-admin/themes.php");
    }

    public SitePage site() {
        return sitePage;
    }

    public PageSpeedPage pageSpeed() {
        return pageSpeedPage;
    }

    public AdminPage admin() {
        return adminPage;
    }

    public FaviconPage faviconPage() {
        return faviconPage;
    }

    public LoginPage loginPage() {
        return loginPage;
    }

    public void addPostDb(String content) {
        logger.info("CREATING TEST POST IN DATABASE ");
        try {
            String myDriver = "com.mysql.cj.jdbc.Driver";
            Class.forName(myDriver);
            Connection conn = DriverManager
                    .getConnection(databaseUrl, databaseUser, databasePass);
            Calendar calendar = Calendar.getInstance();
            Date date = new Date(calendar.getTime().getTime());
            String query = "insert into wp_posts " +
                    "(post_title, post_date, post_content, post_excerpt, to_ping, pinged, post_content_filtered, post_name) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, postTitle);
            preparedStmt.setDate(2, date);
            preparedStmt.setString(3, content);
            preparedStmt.setString(4, "");
            preparedStmt.setString(5, "");
            preparedStmt.setString(6, "");
            preparedStmt.setString(7, "");
            preparedStmt.setString(8, postTitle.toLowerCase().replaceAll(" ", "-"));
            preparedStmt.execute();
            conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

    public String getGitlabFileMarkdown(String screenshotName) throws GitLabApiException {
        logger.info("UPLOADING SCREENSHOT TO GITLAB ");
        GitLabApi gitLabApi = new GitLabApi(gitlabHostUrl, gitlabApiToken);
        Project project = gitLabApi.getProjectApi().getProject(projectId);
        FileUpload upload = gitLabApi.getProjectApi().uploadFile(project, new File("test-screenshots/" + screenshotName + ".png"));
        return upload.getMarkdown();
    }

    public void uploadIssueWithDescriptionToGitlab(String issueTitle, String description, String label) throws GitLabApiException {
        logger.info("UPLOADING ISSUE TO GITLAB WITH DESCRIPTION...");
        GitLabApi gitLabApi = new GitLabApi(gitlabHostUrl, gitlabApiToken);
        Project project = gitLabApi.getProjectApi().getProject(projectId);
        gitLabApi.getIssuesApi().createIssue(
                project.getId(),
                issueTitle,
                description,
                null,
                null,
                null,
                label,
                null,
                null,
                null,
                null);
    }

    public void uploadIssueWithScreenshotToGitlab(String issueTitle, String screenshotName) throws GitLabApiException {
        logger.info("UPLOADING ISSUE TO GITLAB WITH SCREENSHOT...");
        GitLabApi gitLabApi = new GitLabApi(gitlabHostUrl, gitlabApiToken);
        Project project = gitLabApi.getProjectApi().getProject(projectId);
        FileUpload upload = gitLabApi.getProjectApi().uploadFile(project, new File("test-screenshots/" + screenshotName + ".png"));
        gitLabApi.getIssuesApi().createIssue(
                project.getId(),
                issueTitle,
                upload.getMarkdown(),
                null,
                null,
                null,
                "Question",
                null,
                null,
                null,
                null);
    }

}