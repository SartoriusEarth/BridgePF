package org.sagebionetworks.bridge.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.sagebionetworks.bridge.TestConstants.TEST_STUDY_IDENTIFIER;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.bridge.dynamodb.DynamoHealthCode;
import org.sagebionetworks.bridge.dynamodb.DynamoHealthId;
import org.sagebionetworks.bridge.dynamodb.DynamoTestUtil;
import org.sagebionetworks.bridge.models.HealthId;
import org.sagebionetworks.bridge.models.studies.Study;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class HealthCodeServiceImplTest {

    @Resource
    private StudyService studyService;

    @Resource
    private HealthCodeService healthCodeService;

    @Before
    public void before() {
        clearDynamo();
    }

    @After
    public void after() {
        clearDynamo();
    }

    @Test
    public void test() {
        Study study = studyService.getStudyByIdentifier(TEST_STUDY_IDENTIFIER);
        HealthId healthId1 = healthCodeService.create(study);
        assertNotNull(healthId1);
        assertEquals(healthId1.getCode(), healthCodeService.getHealthCode(healthId1.getId()));
        HealthId healthId2 = healthCodeService.create(study);
        assertFalse(healthId1.getId().equals(healthId2.getId()));
        assertFalse(healthId1.getCode().equals(healthId2.getCode()));
        assertEquals(TEST_STUDY_IDENTIFIER, healthCodeService.getStudyIdentifier(healthId1.getCode()));
        assertEquals(TEST_STUDY_IDENTIFIER, healthCodeService.getStudyIdentifier(healthId2.getCode()));
        assertNull(healthCodeService.getStudyIdentifier("abcdef"));
    }

    private void clearDynamo() {
        DynamoTestUtil.clearTable(DynamoHealthCode.class);
        DynamoTestUtil.clearTable(DynamoHealthId.class);
    }
}
