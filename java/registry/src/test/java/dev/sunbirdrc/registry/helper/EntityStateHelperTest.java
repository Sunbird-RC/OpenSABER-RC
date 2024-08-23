package dev.sunbirdrc.registry.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sunbirdrc.registry.entities.AttestationPolicy;
import dev.sunbirdrc.registry.exception.DuplicateRecordException;
import dev.sunbirdrc.registry.exception.EntityCreationException;
import dev.sunbirdrc.registry.identity_providers.pojos.IdentityException;
import dev.sunbirdrc.registry.identity_providers.pojos.IdentityManager;
import dev.sunbirdrc.registry.identity_providers.pojos.OwnerCreationException;
import dev.sunbirdrc.registry.middleware.service.ConditionResolverService;
import dev.sunbirdrc.registry.middleware.util.Constants;
import dev.sunbirdrc.registry.util.ClaimRequestClient;
import dev.sunbirdrc.registry.util.Definition;
import dev.sunbirdrc.registry.util.DefinitionsManager;
import dev.sunbirdrc.workflow.KieConfiguration;
import dev.sunbirdrc.workflow.RuleEngineService;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {ObjectMapper.class, ConditionResolverService.class, ClaimRequestClient.class, KieConfiguration.class})
@Import(EntityStateHelperTestConfiguration.class)
@ActiveProfiles(Constants.TEST_ENVIRONMENT)
class EntityStateHelperTest {

    @Mock
    ConditionResolverService conditionResolverService;

    @Mock
    ClaimRequestClient claimRequestClient;

    @Mock
    IdentityManager identityManager;

    DefinitionsManager definitionsManager;

    @Autowired
    KieContainer kieContainer;

    ObjectMapper m = new ObjectMapper();

    @BeforeEach
    void initMocks() throws IOException {
        MockitoAnnotations.openMocks(this);
        definitionsManager = new DefinitionsManager();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Definition> definitionMap = new HashMap<>();
        String studentSchema = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("Student.json"), Charset.defaultCharset());
        String instituteSchema = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("Institute.json"), Charset.defaultCharset());
        String studentWithPasswordSchema = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("StudentWithPassword.json"), Charset.defaultCharset());
        definitionMap.put("Student", new Definition(objectMapper.readTree(studentSchema)));
        definitionMap.put("StudentWithPassword", new Definition(objectMapper.readTree(studentWithPasswordSchema)));
        definitionMap.put("Institute", new Definition(objectMapper.readTree(instituteSchema)));
        ReflectionTestUtils.setField(definitionsManager, "definitionMap", definitionMap);
    }

    private void runTest(JsonNode existing, JsonNode updated, JsonNode expected, List<AttestationPolicy> attestationPolicies) throws IOException {
        RuleEngineService ruleEngineService = new RuleEngineService(kieContainer, identityManager, true);
        EntityStateHelper entityStateHelper = new EntityStateHelper(definitionsManager, ruleEngineService, conditionResolverService, claimRequestClient, true);
        ReflectionTestUtils.setField(entityStateHelper, "uuidPropertyName", "osid");
        ReflectionTestUtils.setField(entityStateHelper, "setDefaultPassword", false);
        updated = entityStateHelper.applyWorkflowTransitions(existing, updated, attestationPolicies);
        assertEquals(expected, updated);
    }

    void shouldMarkAsDraftWhenThereIsNewEntry() throws IOException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldMarkAsDraftWhenThereIsNewEntry.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("afterStateChange"),
                definitionsManager.getDefinition("Student").getOsSchemaConfiguration().getAttestationPolicies());
    }

    @NotNull
    private String getBaseDir() {
        return this.getClass().getResource("../../../../").getPath() + "entityStateHelper/";
    }

    void shouldMarkAsDraftIfThereIsAChange() throws IOException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldMarkAsDraftIfThereIsAChange.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("afterStateChange"), Collections.emptyList());
    }

    @Test
    void shouldBeNoStateChangeIfTheDataDidNotChange() throws IOException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldBeNoStateChangeIfTheDataDidNotChange.json"));
        JsonNode beforeUpdate = test.get("updated").deepCopy();
        runTest(test.get("existing"), test.get("updated"), test.get("existing"), Collections.emptyList());
    }

    @Test
    void shouldCreateNewOwnersForNewlyAddedOwnerFields() throws IOException, DuplicateRecordException, EntityCreationException, IdentityException {
        when(identityManager.createUser(any())).thenReturn("456");
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldAddNewOwner.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }

    @Test
    void shouldNotCreateNewOwners() throws IOException, DuplicateRecordException, EntityCreationException, IdentityException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldNotAddNewOwner.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }

    @Test
    void shouldNotModifyExistingOwners() throws IOException, DuplicateRecordException, EntityCreationException, IdentityException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldNotModifyExistingOwner.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }

    @Test
    void shouldNotAllowUserModifyingOwnerFields() throws IOException, DuplicateRecordException, EntityCreationException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldNotModifyOwnerDetails.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }

    @Test
    void shouldNotAllowUserModifyingSystemFields() throws IOException, DuplicateRecordException, EntityCreationException {
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldNotModifyOsStateByUser.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }

    @Test
    void shouldRemovePasswordOwnershipFields() throws IOException, OwnerCreationException, IdentityException {
        when(identityManager.createUser(any())).thenReturn("456");
        JsonNode test = m.readTree(new File(getBaseDir() + "shouldRemovePasswordOwnershipFields.json"));
        runTest(test.get("existing"), test.get("updated"), test.get("expected"), Collections.emptyList());
    }
}