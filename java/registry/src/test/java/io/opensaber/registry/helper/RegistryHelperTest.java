package io.opensaber.registry.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.opensaber.registry.middleware.util.Constants;
import io.opensaber.registry.model.DBConnectionInfoMgr;
import io.opensaber.registry.service.DecryptionHelper;
import io.opensaber.registry.service.IReadService;
import io.opensaber.registry.service.ISearchService;
import io.opensaber.registry.service.RegistryService;
import io.opensaber.registry.sink.shard.ShardManager;
import io.opensaber.registry.util.ViewTemplateManager;
import io.opensaber.validators.IValidate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles(Constants.TEST_ENVIRONMENT)
@SpringBootTest(classes = { ObjectMapper.class})
public class RegistryHelperTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@InjectMocks
	private RegistryHelper registryHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private ISearchService searchService;
	
	@Mock
	private ViewTemplateManager viewTemplateManager;
	
	@Mock
    private ShardManager shardManager;

	@Mock
	RegistryService registryService;

	@Mock
	IReadService readService;

	@Mock
	private DBConnectionInfoMgr dbConnectionInfoMgr;

	@Mock
	private DecryptionHelper decryptionHelper;

	@Mock
	private IValidate validationService;

	@Mock
	private EntityStateHelper entityStateHelper;

	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;

	@Before
    public void initMocks(){
		ReflectionTestUtils.setField(registryHelper, "auditSuffix", "Audit");
		ReflectionTestUtils.setField(registryHelper, "auditSuffixSeparator", "_");
		ReflectionTestUtils.setField(registryHelper, "uuidPropertyName", "osid");
		ReflectionTestUtils.setField(registryHelper, "objectMapper", objectMapper);
		MockitoAnnotations.initMocks(this);
		registryHelper.uuidPropertyName = "osid";
	}

	@Test
	public void getAuditLogTest() throws Exception {

		// Data creation
		String inputJson = "{\"Teacher\":{ \"filters\":{ \"recordId\":{\"eq\":\"12c61cc3-cc6a-4a96-8409-e506fb26ddbb\"} } } }";

		String result = "{ \"Teacher_Audit\": [{ \"auditId\": \"66fecb96-b87c-44b5-a930-3de96503aa13\", \"recordId\": \"12c61cc3-cc6a-4a96-8409-e506fb26ddbb\","
				+ " \"timeStamp\": \"2019-12-23 16:56:50.905\", \"date\": 1578566074000, \"@type\": \"Teacher_Audit\", \"action\": \"ADD\", "
				+ "\"auditJson\": [ \"op\", \"path\" ], \"osid\": \"1-d28fd315-bc28-4db0-b7f8-130ff164ba01\", \"userId\": \"35448199-0a7b-4491-a796-b053b9fcfd29\","
				+ " \"transactionId\": [ 870924631 ] }] }";

		JsonNode jsonNode = null;
		JsonNode resultNode = null;
		jsonNode = objectMapper.readTree(inputJson);
		resultNode = objectMapper.readTree(result);

		Mockito.when(searchService.search(ArgumentMatchers.any())).thenReturn(resultNode);
		Mockito.when(viewTemplateManager.getViewTemplate(ArgumentMatchers.any())).thenReturn(null);

		JsonNode node = registryHelper.getAuditLog(jsonNode);
		Assert.assertEquals(jsonNode.get("Teacher").get("filters").get("recordId").get("eq"), node.get("Teacher_Audit").get(0).get("recordId"));
	}

	@Test
	public void shouldAbleToGetThePropertyIdForTheRequestBody() throws Exception {
		String entityName = "Student";
		String entityId = "";
		JsonNode requestBody = new ObjectMapper().readTree("{\n" +
				"    \"program\": \"test123\",\n" +
				"    \"graduationYear\": \"2021\",\n" +
				"    \"marks\": \"78\",\n" +
				"    \"institute\": \"DC universe\"\n" +
				"}\n");
		String propertyURI = "educationDetails";
		ObjectNode student = new ObjectMapper().createObjectNode();
		JsonNode studentNodeContent = new ObjectMapper().readTree("{\n" +
				"        \"educationDetails\": [\n" +
				"            {\n" +
				"                \"graduationYear\": \"2022\",\n" +
				"                \"institute\": \"CD universe\",\n" +
				"                \"osid\": \"1-8d6dfb25-7789-44da-a6d4-eacf93e3a7bb\",\n" +
				"                \"program\": \"8th\",\n" +
				"                \"marks\": \"99\"\n" +
				"            },\n" +
				"            {\n" +
				"                \"graduationYear\": \"2021\",\n" +
				"                \"institute\": \"DC universe\",\n" +
				"                \"osid\": \"1-7d9dfb25-7789-44da-a6d4-eacf93e3a7aa\",\n" +
				"                \"program\": \"test123\",\n" +
				"                \"marks\": \"78\"\n" +
				"            }\n" +
				"        ],\n" +
				"        \"contactDetails\": {\n" +
				"            \"osid\": \"1-096cd663-6ba9-49f8-af31-1ace9e31bc31\",\n" +
				"            \"mobile\": \"9000090000\",\n" +
				"            \"osOwner\": \"556302c9-d8b4-4f60-9ac1-c16c8839a9f3\",\n" +
				"            \"email\": \"ram@gmail.com\"\n" +
				"        },\n" +
				"        \"osid\": \"1-b4907dc2-d3a8-49dc-a933-2b473bdd2ddb\",\n" +
				"        \"identityDetails\": {\n" +
				"            \"osid\": \"1-9f50f1b3-99cc-4fcb-9e51-e0dbe0be19f9\",\n" +
				"            \"gender\": \"Male\",\n" +
				"            \"identityType\": \"\",\n" +
				"            \"dob\": \"1999-01-01\",\n" +
				"            \"fullName\": \"First Avenger\",\n" +
				"            \"identityValue\": \"\",\n" +
				"            \"osOwner\": \"556302c9-d8b4-4f60-9ac1-c16c8839a9f3\"\n" +
				"        },\n" +
				"        \"osOwner\": \"556302c9-d8b4-4f60-9ac1-c16c8839a9f3\"\n" +
				"    }");
		student.set("Student", studentNodeContent);
		Mockito.when(readService.getEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(student);
		String propertyId = registryHelper.getPropertyIdAfterSavingTheProperty(entityName, entityId, requestBody, propertyURI);
		String actualPropertyId = "1-7d9dfb25-7789-44da-a6d4-eacf93e3a7aa";
		Assert.assertEquals(propertyId, actualPropertyId);
	}

	@Test
	public void addEntityPropertyTest() throws Exception {
		JsonNode tests = objectMapper.readTree(new File("src/test/resources/addPropTests.json"));
		JsonNode existingNode = tests.get("existingNode");

		RegistryHelper registryHelperSpy = Mockito.spy(registryHelper);
		Mockito.doReturn(existingNode).when(registryHelperSpy).readEntity(
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.eq(false),
				ArgumentMatchers.isNull(),
				ArgumentMatchers.eq(false)
		);
		Mockito.when(dbConnectionInfoMgr.getUuidPropertyName()).thenReturn("osid");
		ArrayNode testScenarios = (ArrayNode) tests.get("scenarios");
		for (JsonNode tc : testScenarios) {
			registryHelperSpy.addEntityProperty(
					"Student",
					"123",
					tc.get("propertyPath").asText(),
					tc.get("newPropertyNode")
			);

		}

		Mockito.verify(registryService, Mockito.times(testScenarios.size())).updateEntity(
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				stringArgumentCaptor.capture()
		);
		List<String> actualUpdatedNodes = stringArgumentCaptor.getAllValues();
		for (int i = 0; i < actualUpdatedNodes.size(); i++) {
			JsonNode actualNode = objectMapper.readTree(actualUpdatedNodes.get(i));
			JsonNode expected = testScenarios.get(i).get("expected");
			Assert.assertEquals(actualNode, expected);
		}
	}

	@Test
	public void updateEntityPropertyTests() throws Exception {
		JsonNode tests = objectMapper.readTree(new File("src/test/resources/updatePropTests.json"));
		JsonNode existingNode = tests.get("existingNode");

		RegistryHelper registryHelperSpy = Mockito.spy(registryHelper);
		Mockito.doReturn(existingNode).when(registryHelperSpy).readEntity(
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.eq(false),
				ArgumentMatchers.isNull(),
				ArgumentMatchers.eq(false)
		);
		Mockito.when(dbConnectionInfoMgr.getUuidPropertyName()).thenReturn("osid");
		ArrayNode testScenarios = (ArrayNode) tests.get("scenarios");
		for (JsonNode tc : testScenarios) {
			registryHelperSpy.updateEntityProperty(
					"Student",
					"123",
					tc.get("propertyPath").asText(),
					tc.get("updatedPropNode")
			);

		}

		Mockito.verify(registryService, Mockito.times(testScenarios.size())).updateEntity(
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				ArgumentMatchers.any(),
				stringArgumentCaptor.capture()
		);
		List<String> actualUpdatedNodes = stringArgumentCaptor.getAllValues();
		for (int i = 0; i < actualUpdatedNodes.size(); i++) {
			JsonNode actualNode = objectMapper.readTree(actualUpdatedNodes.get(i));
			JsonNode expected = testScenarios.get(i).get("expected");
			Assert.assertEquals(actualNode, expected);
		}
	}

}