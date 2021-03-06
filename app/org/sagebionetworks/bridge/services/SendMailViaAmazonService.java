package org.sagebionetworks.bridge.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.PreencodedMimeBodyPart;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sagebionetworks.bridge.exceptions.BridgeServiceException;
import org.sagebionetworks.bridge.json.DateUtils;
import org.sagebionetworks.bridge.models.User;
import org.sagebionetworks.bridge.models.studies.ConsentSignature;
import org.sagebionetworks.bridge.models.studies.Study;
import org.sagebionetworks.bridge.models.studies.StudyConsent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class SendMailViaAmazonService implements SendMailService {

    private static final String EMAIL_SUBJECT = "Consent Agreement for %s";
    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM d, yyyy");
    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String HEADER_CONTENT_DISPOSITION_VALUE = "inline";
    private static final String HEADER_CONTENT_ID_VALUE = "<consentSignature>";
    private static final String HEADER_CONTENT_TRANSFER_ENCODING_VALUE = "base64";
    private static final Logger LOG = LoggerFactory.getLogger(SendMailViaAmazonService.class);
    private static final String MIME_TYPE_TEXT_HTML = "text/html";
    private static final Region region = Region.getRegion(Regions.US_EAST_1);

    private String fromEmail;
    private AmazonSimpleEmailServiceClient emailClient;
    private StudyService studyService;

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setEmailClient(AmazonSimpleEmailServiceClient emailClient) {
        this.emailClient = emailClient;
    }

    public void setStudyService(StudyService studyService) {
        this.studyService = studyService;
    }

    @Override
    public void sendConsentAgreement(User user, ConsentSignature consentSignature, StudyConsent studyConsent) {
        Study study = studyService.getStudyByIdentifier(studyConsent.getStudyKey());

        try {
            // Create email using JavaMail
            Session mailSession = Session.getInstance(new Properties(), null);
            MimeMessage mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setFrom(new InternetAddress(fromEmail));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            mimeMessage.setSubject(String.format(EMAIL_SUBJECT, study.getName()), Charsets.UTF_8.name());

            MimeMultipart mimeMultipart = new MimeMultipart();

            // Generate body
            String body = createSignedDocument(consentSignature, studyConsent);
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, MIME_TYPE_TEXT_HTML);
            mimeMultipart.addBodyPart(bodyPart);

            // Write signature image as an attachment, if it exists. Because of the validation in ConsentSignature, if
            // imageData is present, so will imageMimeType.
            // We need to send the signature image as an embedded image in an attachment because some email providers
            // (notably Gmail) block inline Base64 images.
            if (consentSignature.getImageData() != null) {
                // Use pre-encoded MIME part since our image data is already base64 encoded.
                MimeBodyPart attachmentPart = new PreencodedMimeBodyPart(HEADER_CONTENT_TRANSFER_ENCODING_VALUE);
                attachmentPart.setContentID(HEADER_CONTENT_ID_VALUE);
                attachmentPart.setHeader(HEADER_CONTENT_DISPOSITION, HEADER_CONTENT_DISPOSITION_VALUE);
                attachmentPart.setContent(consentSignature.getImageData(), consentSignature.getImageMimeType());
                mimeMultipart.addBodyPart(attachmentPart);
            }

            // Convert MimeMessage to raw text to send to SES.
            mimeMessage.setContent(mimeMultipart);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(byteOutputStream);
            RawMessage sesRawMessage = new RawMessage(ByteBuffer.wrap(byteOutputStream.toByteArray()));

            SendRawEmailRequest req = new SendRawEmailRequest(sesRawMessage);
            req.setSource(fromEmail);
            req.setDestinations(Collections.singleton(user.getEmail()));
            emailClient.setRegion(region);
            SendRawEmailResult result = emailClient.sendRawEmail(req);

            LOG.info(String.format("Sent email to SES with message ID %s", result.getMessageId()));
        } catch (AmazonClientException | IOException | MessagingException ex) {
            throw new BridgeServiceException(ex);
        }
    }

    private String createSignedDocument(ConsentSignature consent, StudyConsent studyConsent)
            throws IOException {

        String filePath = studyConsent.getPath();
        FileSystemResource resource = new FileSystemResource(filePath);
        InputStreamReader isr = new InputStreamReader(resource.getInputStream(), "UTF-8");
        String consentAgreementHTML = CharStreams.toString(isr);

        String signingDate = fmt.print(DateUtils.getCurrentMillisFromEpoch());
        // The dates we're showing are internally stored as UTC dates, so convert to 
        // LocalDate which will show the date the user entered.
        LocalDate localDate = LocalDate.parse(consent.getBirthdate());
        String birthdate = fmt.print(localDate);

        String html = consentAgreementHTML.replace("@@name@@", consent.getName());
        html = html.replace("@@birth.date@@", birthdate);
        html = html.replace("@@signing.date@@", signingDate);
        return html;
    }
}
