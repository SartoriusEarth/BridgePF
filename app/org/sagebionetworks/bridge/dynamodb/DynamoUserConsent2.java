package org.sagebionetworks.bridge.dynamodb;

import org.sagebionetworks.bridge.json.BridgeTypeName;
import org.sagebionetworks.bridge.json.DateTimeJsonDeserializer;
import org.sagebionetworks.bridge.json.DateTimeJsonSerializer;
import org.sagebionetworks.bridge.models.UserConsent;
import org.sagebionetworks.bridge.models.studies.StudyConsent;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@DynamoDBTable(tableName = "UserConsent2")
@BridgeTypeName("UserConsent")
public class DynamoUserConsent2 implements DynamoTable, UserConsent {

    // Schema attributes
    private String healthCodeStudy; // <health-code>:<study-key>
    private Long version;           // Version for optimistic locking

    // Value attributes
    private long signedOn;          // Time stamp is epoch time in milliseconds
    private boolean dataSharing;    // Whether the user agrees to share data for the study

    // User consent signature
    private String name;
    private String birthdate;
    private String imageData;
    private String imageMimeType;

    // Composite key parts copied over to avoid parsing
    private String healthCode;
    private String studyKey;
    private long consentCreatedOn;

    public DynamoUserConsent2() {}

    // Constructor to create a hash-key object
    DynamoUserConsent2(String healthCode, String studyKey) {
        this.healthCode = healthCode;
        this.studyKey = studyKey;
        healthCodeStudy = healthCode + ":" + studyKey;
    }

    // Constructor to create a hash-key object
    DynamoUserConsent2(String healthCode, StudyConsent consent) {
        this(healthCode, consent.getStudyKey());
        consentCreatedOn = consent.getCreatedOn();
    }

    // Copy constructor
    DynamoUserConsent2(DynamoUserConsent2 consent) {
        healthCodeStudy = consent.healthCodeStudy;
        signedOn = consent.signedOn;
        version = consent.version;
        name = consent.name;
        birthdate = consent.birthdate;
        imageData = consent.imageData;
        imageMimeType = consent.imageMimeType;
        healthCode = consent.healthCode;
        studyKey = consent.studyKey;
        consentCreatedOn = consent.consentCreatedOn;
    }

    @DynamoDBHashKey
    public String getHealthCodeStudy() {
        return healthCodeStudy;
    }
    public void setHealthCodeStudy(String healthCodeStudy) {
        this.healthCodeStudy = healthCodeStudy;
    }

    /**
     * Consent time stamp. Epoch time in milliseconds.
     */
    @DynamoDBAttribute
    public long getSignedOn() {
        return signedOn;
    }
    public void setSignedOn(long timestamp) {
        this.signedOn = timestamp;
    }

    @DynamoDBAttribute
    public boolean getDataSharing() {
        return dataSharing;
    }
    public void setDataSharing(boolean dataSharing) {
        this.dataSharing = dataSharing;
    }

    @DynamoDBAttribute
    public String getHealthCode() {
        return healthCode;
    }
    public void setHealthCode(String healthCode) {
        this.healthCode = healthCode;
    }

    @DynamoDBAttribute
    public String getStudyKey() {
        return studyKey;
    }
    public void setStudyKey(String studyKey) {
        this.studyKey = studyKey;
    }

    @DynamoDBAttribute
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    public long getConsentCreatedOn() {
        return consentCreatedOn;
    }
    @JsonDeserialize(using = DateTimeJsonDeserializer.class)
    public void setConsentCreatedOn(long consentCreatedOn) {
        this.consentCreatedOn = consentCreatedOn;
    }

    @DynamoDBAttribute
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getBirthdate() {
        return this.birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /** Image data, represented as a Base64 encoded string. May be null. Must be non-empty. */
    @DynamoDBAttribute
    public String getImageData() {
        return imageData;
    }
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    /**
     * MIME type of the image data (ex: "image/gif"). May be null. Must be non-empty. Must be present if imageData is
     * present. Must be absent if imageData is absent.
     */
    @DynamoDBAttribute
    public String getImageMimeType() {
        return imageMimeType;
    }
    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    @DynamoDBVersionAttribute
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DynamoUserConsent2 [version=" + version + ", signedOn=" + signedOn + ", dataSharing=" + dataSharing
                + ", name=" + name + ", birthdate=" + birthdate + ", hasImageData=" + (imageData != null)
                + ", imageMimeType=" + imageMimeType + ", studyKey=" + studyKey + ", consentCreatedOn="
                + consentCreatedOn + "]";
    }
}
