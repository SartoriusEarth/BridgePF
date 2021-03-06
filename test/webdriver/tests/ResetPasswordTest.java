package webdriver.tests;


import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.bridge.TestUserAdminHelper;
import org.sagebionetworks.bridge.TestUserAdminHelper.TestUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import play.libs.F.Callback;
import play.test.TestBrowser;
import webdriver.pages.AppPage;
import webdriver.pages.AppPage.RequestResetPasswordDialog;

@ContextConfiguration("classpath:test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class ResetPasswordTest extends BaseIntegrationTest {
    
    @Resource
    private TestUserAdminHelper helper;
    
    @Test
    public void resetPasswordCanBeCancelled() {
        call(new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                AppPage page = new AppPage(browser);
                RequestResetPasswordDialog dialog = page.openSignInDialog().openResetPasswordDialog();
                
                dialog.canCancel();
            }
        });
    }

    @Test
    public void resetPasswordPreventsInvalidEmailSubmission() {
        call(new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                AppPage page = new AppPage(browser);
                RequestResetPasswordDialog dialog = page.openSignInDialog().openResetPasswordDialog();
                
                dialog.submitInvalidEmailAddress("fooboo");
            }
        });
    }

    @Test
    public void resetPasswordSubmitsValidEmail() {
        call(new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                TestUser testUser = null;
                try {
                    testUser = helper.createUser(ResetPasswordTest.class);
                    
                    AppPage page = new AppPage(browser);
                    RequestResetPasswordDialog dialog = page.openSignInDialog().openResetPasswordDialog();
                    
                    dialog.submitEmailAddress(testUser.getUser().getEmail());
                } finally {
                    helper.deleteUser(testUser);
                }
            }
        });
    }

}
