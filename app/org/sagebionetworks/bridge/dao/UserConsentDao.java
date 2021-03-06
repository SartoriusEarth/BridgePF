package org.sagebionetworks.bridge.dao;

import org.sagebionetworks.bridge.models.UserConsent;
import org.sagebionetworks.bridge.models.studies.ConsentSignature;
import org.sagebionetworks.bridge.models.studies.StudyConsent;

public interface UserConsentDao {

    /**
     * Gives consent to the specified study.
     */
    void giveConsent(String healthCode, StudyConsent consent);

    /**
     * Withdraws consent to the specified study.
     */
    boolean withdrawConsent(String healthCode, String studyIdentifier);

    /**
     * Gets the time stamp of the consent document the user has consented.
     * Returns null if the user has not consented yet.
     */
    Long getConsentCreatedOn(String healthCode, String studyIdentifier);

    /**
     * Whether the user has consented to the specified study consent document.
     */
    boolean hasConsented(String healthCode, StudyConsent consent);

    /**
     * Whether the user has consented to the specified study.
     * @param healthCode
     * @param studyIdentifier
     * @return
     */
    boolean hasConsented(String healthCode, String studyIdentifier);

    /**
     * Get the user consent record that consents the user to this study.
     * @param healthCode
     * @param consent
     * @return
     */
    UserConsent getUserConsent(String healthCode, StudyConsent consent);

    /**
     * Get the user consent record that consents the user to this study.
     * @param healthCode
     * @param studyIdentifier
     * @return
     */
    UserConsent getUserConsent(String healthCode, String studyIdentifier);

    /**
     * Returns a non-null consent signature, consisting of the signature name and birthdate. Throws
     * EntityNotFoundException if no consent signature is found.
     */
    ConsentSignature getConsentSignature(String healthCode, StudyConsent consent);

    /**
     * @param studyKey
     * @return
     */
    long getNumberOfParticipants(String studyKey);
}
