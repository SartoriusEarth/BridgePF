package org.sagebionetworks.bridge.heroku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagebionetworks.bridge.TestUtils;
import org.sagebionetworks.bridge.config.BridgeConfigFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class HerokuApiImplTest {

    @Resource
    HerokuApiImpl herokuApi;
    
    private String identifier;
    
    @After
    public void after() {
        if (identifier != null) {
            herokuApi.unregisterDomainForStudy(identifier);
        }
    }
    
    @Test
    public void crudDomains() {
        identifier = TestUtils.randomName();
        herokuApi.registerDomainForStudy(identifier);
        
        String domain = herokuApi.getDomainRegistrationForStudy(identifier);
        assertEquals("Has correct domain", BridgeConfigFactory.getConfig().getStudyHostname(identifier), domain);
        
        herokuApi.unregisterDomainForStudy(identifier);
        domain = herokuApi.getDomainRegistrationForStudy(identifier);
        
        assertNull("Now has no domain", domain);
        
        identifier = null;
    }
    
}
