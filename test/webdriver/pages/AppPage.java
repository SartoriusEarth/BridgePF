package webdriver.pages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.sagebionetworks.bridge.TestConstants.CANCEL_ACTION;
import static org.sagebionetworks.bridge.TestConstants.CLOSE_ACTION;
import static org.sagebionetworks.bridge.TestConstants.EMAIL_INPUT;
import static org.sagebionetworks.bridge.TestConstants.JOIN_LINK;
import static org.sagebionetworks.bridge.TestConstants.JOIN_PAGE;
import static org.sagebionetworks.bridge.TestConstants.MENU_LINK;
import static org.sagebionetworks.bridge.TestConstants.PASSWORD_INPUT;
import static org.sagebionetworks.bridge.TestConstants.RESET_PASSWORD_DIALOG;
import static org.sagebionetworks.bridge.TestConstants.RESET_PASSWORD_LINK;
import static org.sagebionetworks.bridge.TestConstants.SEND_ACTION;
import static org.sagebionetworks.bridge.TestConstants.SIGN_IN_ACT;
import static org.sagebionetworks.bridge.TestConstants.SIGN_IN_DIALOG;
import static org.sagebionetworks.bridge.TestConstants.SIGN_IN_LINK;
import static org.sagebionetworks.bridge.TestConstants.SIGN_IN_MESSAGE;
import static org.sagebionetworks.bridge.TestConstants.SIGN_OUT_LINK;
import static org.sagebionetworks.bridge.TestConstants.TEST_BASE_URL;
import static org.sagebionetworks.bridge.TestConstants.USERNAME_INPUT;
import static org.sagebionetworks.bridge.TestConstants.USERNAME_LABEL;

import org.fluentlenium.core.domain.FluentWebElement;
import org.sagebionetworks.bridge.TestConstants;

import play.test.TestBrowser;

public class AppPage extends BasePage {

    public AppPage(TestBrowser browser) {
        super(browser);
        browser.goTo(TEST_BASE_URL);
        assertTrue("Title includes phrase 'Sage Bionetworks'", browser.pageSource().contains("Sage Bionetworks"));
    }

    public JoinPage getJoinPage() {
        signUpLink().click();
        waitUntilPresent(JOIN_PAGE);
        waitUntilScriptHasFocused(TestConstants.USERNAME_INPUT);
        return new JoinPage(browser);
    }

    public SignInDialog openSignInDialog() {
        signInLink().click();
        waitUntilPresent(SIGN_IN_DIALOG);
        waitUntilScriptHasFocused(USERNAME_INPUT);
        return new SignInDialog(browser);
    }
    
    public void signOut() {
        signOutLink().click();
        waitUntilPresent(SIGN_IN_LINK);
    }

    private FluentWebElement resetPasswordLink() {
        waitUntilPresent(RESET_PASSWORD_LINK);
        return browser.findFirst(RESET_PASSWORD_LINK);
    }

    private FluentWebElement signInLink() {
        waitUntilPresent(SIGN_IN_LINK);
        return browser.findFirst(SIGN_IN_LINK);
    }

    private FluentWebElement signOutLink() {
        waitUntilPresent(MENU_LINK);
        browser.findFirst(MENU_LINK).click();
        waitUntilPresent(SIGN_OUT_LINK);
        return browser.findFirst(SIGN_OUT_LINK);
    }

    private FluentWebElement signUpLink() {
        waitUntilPresent(JOIN_LINK);
        return browser.findFirst(JOIN_LINK);
    }

    public class SignInDialog extends BasePage {

        public SignInDialog(TestBrowser browser) {
            super(browser);
        }
        public RequestResetPasswordDialog openResetPasswordDialog() {
            resetPasswordLink().click();
            waitUntilPresent(RESET_PASSWORD_DIALOG);
            return new RequestResetPasswordDialog(browser);
        }
        public void signInWrong(String username, String password) {
            enterCredentials(username, password);
            signInAction().click();
            waitUntilDisplayed(SIGN_IN_MESSAGE);
            close();
        }

        public void signIn(String username, String password) {
            enterCredentials(username, password);
            signInAction().click();
            waitUntilDisplayed(USERNAME_LABEL);
            assertTrue("User label includes user's name", userLabel().getText().equals(username));
        }

        public void close() {
            closeButton().click();
            waitUntilNotPresent(SIGN_IN_DIALOG);
        }

        private void enterCredentials(String username, String password) {
            assertFalse("Sign in message is hidden", signInMessage().isDisplayed());
            assertFalse("Sign in action is disabled", signInAction().isEnabled());
            browser.fill(USERNAME_INPUT).with(username);
            browser.fill(PASSWORD_INPUT).with(password);
            assertTrue("Sign in action is enabled", signInAction().isEnabled());
        }

        private FluentWebElement closeButton() {
            waitUntilPresent(CLOSE_ACTION);
            return browser.findFirst(CLOSE_ACTION);
        }

        private FluentWebElement signInMessage() {
            waitUntilPresent(SIGN_IN_MESSAGE);
            return browser.findFirst(SIGN_IN_MESSAGE);
        }

        private FluentWebElement signInAction() {
            waitUntilPresent(SIGN_IN_ACT);
            return browser.findFirst(SIGN_IN_ACT);
        }

        private FluentWebElement userLabel() {
            waitUntilPresent(USERNAME_LABEL);
            return browser.findFirst(USERNAME_LABEL);
        }
    }

    public class RequestResetPasswordDialog extends BasePage {

        public RequestResetPasswordDialog(TestBrowser browser) {
            super(browser);
        }

        public void canCancel() {
            waitUntilPresent(RESET_PASSWORD_DIALOG);
            cancelButton().click();
            waitUntilNotPresent(RESET_PASSWORD_DIALOG);
        }

        public void submitInvalidEmailAddress(String email) {
            assertFalse("Email button is not enabled", sendEmailButton().isEnabled());
            browser.fill(EMAIL_INPUT).with(email);
            assertFalse("Email button is enabled", sendEmailButton().isEnabled());
            close();
        }

        public void submitEmailAddress(String email) {
            assertFalse("Email button is not enabled", sendEmailButton().isEnabled());
            browser.fill(EMAIL_INPUT).with(email);
            assertTrue("Email button is enabled", sendEmailButton().isEnabled());
            sendEmailButton().click();
            waitUntilNotPresent(RESET_PASSWORD_DIALOG);
            assertTrue("Message popup confirms an email was sent",
                    messagePopup().getText().contains("Please look for further instructions in your email inbox."));
        }

        public void close() {
            closeButton().click();
            waitUntilNotPresent(SIGN_IN_DIALOG);
        }

        private FluentWebElement closeButton() {
            waitUntilPresent(CLOSE_ACTION);
            return browser.findFirst(CLOSE_ACTION);
        }

        private FluentWebElement sendEmailButton() {
            waitUntilPresent(SEND_ACTION);
            return browser.findFirst(SEND_ACTION);
        }

        private FluentWebElement cancelButton() {
            waitUntilPresent(CANCEL_ACTION);
            return browser.findFirst(CANCEL_ACTION);
        }
    }
}
