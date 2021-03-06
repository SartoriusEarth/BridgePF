package org.sagebionetworks.bridge.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sagebionetworks.bridge.TestConstants;
import org.sagebionetworks.bridge.dynamodb.DynamoStudy;
import org.sagebionetworks.bridge.models.User;
import org.sagebionetworks.bridge.models.studies.ConsentSignature;
import org.sagebionetworks.bridge.models.studies.Study;
import org.sagebionetworks.bridge.models.studies.StudyConsent;

import com.amazonaws.regions.Region;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import com.google.common.base.Charsets;

public class SendMailViaAmazonServiceTest {

    private SendMailViaAmazonService service;
    private AmazonSimpleEmailServiceClient emailClient;
    private StudyService studyService;
    private StudyConsent studyConsent;
    private ArgumentCaptor<SendRawEmailRequest> argument;

    @Before
    public void setUp() throws Exception {
        Study study = new DynamoStudy();
        study.setIdentifier("api");
        study.setMinAgeOfConsent(17);

        studyService = mock(StudyService.class);
        when(studyService.getStudyByIdentifier(TestConstants.TEST_STUDY_IDENTIFIER)).thenReturn(study);
        emailClient = mock(AmazonSimpleEmailServiceClient.class);
        when(emailClient.sendRawEmail(notNull(SendRawEmailRequest.class))).thenReturn(new SendRawEmailResult()
                .withMessageId("test message id"));
        argument = ArgumentCaptor.forClass(SendRawEmailRequest.class);

        service = new SendMailViaAmazonService();
        service.setFromEmail("test-sender@sagebase.org");
        service.setEmailClient(emailClient);
        service.setStudyService(studyService);

        studyConsent = new StudyConsent() {
            @Override
            public String getStudyKey() {
                return TestConstants.TEST_STUDY_IDENTIFIER;
            }
            @Override
            public long getCreatedOn() {
                return 0;
            }
            @Override
            public boolean getActive() {
                return true;
            }
            @Override
            public String getPath() {
                return "conf/email-templates/api-consent.html";
            }
            @Override
            public int getMinAge() {
                return 17;
            }
        };
    }

    @Test
    public void sendConsentEmail() {
        ConsentSignature consent = ConsentSignature.create("Test 2", "1950-05-05", null, null);
        User user = new User();
        user.setEmail("test-user@sagebase.org");
        service.sendConsentAgreement(user, consent, studyConsent);

        verify(emailClient).setRegion(any(Region.class));
        verify(emailClient).sendRawEmail(argument.capture());

        // validate from
        SendRawEmailRequest req = argument.getValue();
        assertEquals("Correct sender", "test-sender@sagebase.org", req.getSource());

        // validate to
        List<String> toList = req.getDestinations();
        assertEquals("Correct number of recipients", 1, toList.size());
        assertEquals("Correct recipient", "test-user@sagebase.org", toList.get(0));

        // Validate message content. MIME message must be ASCII
        String rawMessage = new String(req.getRawMessage().getData().array(), Charsets.US_ASCII);
        assertTrue("Contains consent content", rawMessage.contains("Had this been a real study"));
        assertTrue("Date transposed to document", rawMessage.contains("May 5, 1950"));
        assertTrue("Name transposed to document", rawMessage.contains("Test 2"));
    }

    @Test
    public void sendConsentEmailWithSignatureImage() {
        ConsentSignature consent = ConsentSignature.create("Eggplant McTester", "1970-05-01",
                TestConstants.DUMMY_IMAGE_DATA, "image/fake");
        User user = new User();
        user.setEmail("test-user@sagebase.org");
        service.sendConsentAgreement(user, consent, studyConsent);

        verify(emailClient).setRegion(any(Region.class));
        verify(emailClient).sendRawEmail(argument.capture());

        // validate from
        SendRawEmailRequest req = argument.getValue();
        assertEquals("Correct sender", "test-sender@sagebase.org", req.getSource());

        // validate to
        List<String> toList = req.getDestinations();
        assertEquals("Correct number of recipients", 1, toList.size());
        assertEquals("Correct recipient", "test-user@sagebase.org", toList.get(0));

        // Validate message content. MIME message must be ASCII
        String rawMessage = new String(req.getRawMessage().getData().array(), Charsets.US_ASCII);
        assertTrue("Contains consent content", rawMessage.contains("Had this been a real study"));
        assertTrue("Date transposed to document", rawMessage.contains("May 1, 1970"));
        assertTrue("Name transposed to document", rawMessage.contains("Eggplant McTester"));

        // Validate message contains signature image. To avoid coupling too closely with MIME implementation, just
        // validate that our content type shows up and that we contain the first few chars of the image data.
        assertTrue("Contains signature image MIME type", rawMessage.contains("image/fake"));
        assertTrue("Contains signature image data", rawMessage.contains(TestConstants.DUMMY_IMAGE_DATA.substring(
                0, 10)));
    }
}
