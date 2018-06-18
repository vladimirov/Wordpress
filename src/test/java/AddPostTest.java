import org.testng.annotations.Test;

public class AddPostTest extends TestBase {

    @Test
    public void createPostTest() {
        app.openCrmUrl();
        app.goTo().copyTestContent();
        app.openAdminUrl();
        app.goTo().gotoPostsPage();
        app.site().addNewPostButtonClick();
        app.site().enterPostTitle();
        app.site().enterTestContent();
        app.site().scrollUp();
        app.site().publish();
        //make a screen shot
//        app.openTestPostPage();
//        app.site().screenShot();



    }

}
