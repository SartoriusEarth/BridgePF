package controllers;

import java.util.List;

import org.sagebionetworks.bridge.dynamodb.DynamoStudy;
import org.sagebionetworks.bridge.models.VersionHolder;
import org.sagebionetworks.bridge.models.studies.Study;
import org.sagebionetworks.bridge.services.StudyService;

import play.mvc.Result;

public class StudyController extends BaseController {

    private StudyService studyService;

    public void setStudyService(StudyService studyService) {
        this.studyService = studyService;
    }

    public Result getStudyForResearcher() throws Exception {
        // We want a signed in exception before a study not found exception
        // getAuthenticatedSession();
        Study study = studyService.getStudyByHostname(getHostname());
        getAuthenticatedResearchOrAdminSession(study);
        return okResult(study);
    }

    public Result updateStudyForResearcher() throws Exception {
        // We want a signed in exception before a study not found exception
        // getAuthenticatedSession();
        Study study = studyService.getStudyByHostname(getHostname());
        getAuthenticatedResearchOrAdminSession(study);

        Study studyUpdate = DynamoStudy.fromJson(requestToJSON(request()));
        studyUpdate.setIdentifier(study.getIdentifier());
        studyUpdate = studyService.updateStudy(studyUpdate);
        return okResult(new VersionHolder(studyUpdate.getVersion()));
    }

    public Result updateStudy(String identifier) throws Exception {
        getAuthenticatedAdminSession();

        Study studyUpdate = DynamoStudy.fromJson(requestToJSON(request()));
        studyUpdate = studyService.updateStudy(studyUpdate);
        return okResult(new VersionHolder(studyUpdate.getVersion()));
    }

    public Result getStudy(String identifier) throws Exception {
        getAuthenticatedAdminSession();

        Study study = studyService.getStudyByIdentifier(identifier);
        return okResult(study);
    }

    public Result getAllStudies() throws Exception {
        getAuthenticatedAdminSession();

        List<Study> studies = studyService.getStudies();
        return okResult(studies);
    }

    public Result createStudy() throws Exception {
        getAuthenticatedAdminSession();

        Study study = DynamoStudy.fromJson(requestToJSON(request()));
        study = studyService.createStudy(study);
        return okResult(new VersionHolder(study.getVersion()));
    }

    public Result deleteStudy(String identifier) throws Exception {
        getAuthenticatedAdminSession();

        studyService.deleteStudy(identifier);
        return okResult("Study deleted.");
    }

}
