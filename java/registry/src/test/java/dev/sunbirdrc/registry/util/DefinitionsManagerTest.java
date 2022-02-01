package dev.sunbirdrc.registry.util;

import dev.sunbirdrc.pojos.OwnershipsAttributes;
import dev.sunbirdrc.registry.middleware.util.Constants;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DefinitionsManager.class, OSResourceLoader.class})
@ActiveProfiles(Constants.TEST_ENVIRONMENT)
public class DefinitionsManagerTest {

    @Autowired
    private DefinitionsManager definitionsManager;

    @Autowired
    private ResourceLoader resourceLoader;
    
    private OSResourceLoader osResourceLoader;

    @Test
    public void testIfResourcesCountMatchesFileDefinitions() {
        boolean flag = false;
        osResourceLoader = new OSResourceLoader(resourceLoader);
        try {
            int nDefinitions = definitionsManager.getAllKnownDefinitions().size();
            int nResources = osResourceLoader.getResources(Constants.RESOURCE_LOCATION).length;
            flag = (2*nDefinitions == nResources);
        } catch (IOException ioe) {

        }
        assertTrue(flag);
    }

    @Test
    public void testShouldReturnGetOwnershipAttributes() {
        String entity = "Student";
        List<OwnershipsAttributes> ownershipsAttributes = definitionsManager.getOwnershipAttributes(entity);
        assertEquals(1, ownershipsAttributes.size());
        assertEquals("/contactDetails/email", ownershipsAttributes.get(0).getEmail());
        assertEquals("/contactDetails/mobile", ownershipsAttributes.get(0).getMobile());
        assertEquals("/contactDetails/mobile", ownershipsAttributes.get(0).getUserId());
    }

    @Test
    public void testGetOwnershipAttributesForInvalidEntity() {
        String entity = "UnknownEntity";
        List<OwnershipsAttributes> ownershipsAttributes = definitionsManager.getOwnershipAttributes(entity);
        assertEquals(0, ownershipsAttributes.size());
    }

    @Test
    public void testGetOwnershipAttributesShouldReturnEmpty() {
        String entity = "Common";
        List<OwnershipsAttributes> ownershipsAttributes = definitionsManager.getOwnershipAttributes(entity);
        assertEquals(0, ownershipsAttributes.size());
    }

    @Test
    public void testShouldReturnTrueForValidEntityName() {
        String entity = "Common";
        assertTrue(definitionsManager.isValidEntityName(entity));
    }

    @Test
    public void testShouldReturnFalseForInValidEntityName() {
        String entity = "XYZ";
        assertFalse(definitionsManager.isValidEntityName(entity));
    }
}