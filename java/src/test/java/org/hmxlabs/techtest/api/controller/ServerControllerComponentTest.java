package org.hmxlabs.techtest.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hmxlabs.techtest.TestDataHelper;
import org.hmxlabs.techtest.server.api.controller.ServerController;
import org.hmxlabs.techtest.server.api.model.DataBody;
import org.hmxlabs.techtest.server.api.model.DataEnvelope;
import org.hmxlabs.techtest.server.api.model.DataHeader;
import org.hmxlabs.techtest.server.component.Server;
import org.hmxlabs.techtest.server.exception.HadoopClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hmxlabs.techtest.TestDataHelper.DUMMY_DATA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

	@Mock
	private Server serverMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@BeforeEach
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();
	}

	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {
		when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
	}

	@Test
	public void testGetDataPostCallWorksAsExpected() throws Exception {
		//Arrange
		DataEnvelope dataEnvelope = new DataEnvelope(
				new DataHeader("Data block", BlockTypeEnum.BLOCKTYPEA),
				new DataBody(DUMMY_DATA)
		);

		List<DataEnvelope> data = List.of(
				dataEnvelope
		);

		when(serverMock.getData(BlockTypeEnum.BLOCKTYPEA)).thenReturn(data);

		String testDataEnvelopeListJson = objectMapper.writeValueAsString(data);

		//Act
		MvcResult mvcResult = mockMvc.perform(get(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA)))
				.andExpect(status().isOk())
				.andReturn();

		List<DataEnvelope> responseData = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<DataEnvelope>>(){});

		//Assert
		assertThat(responseData).isNotNull();
		assertThat(responseData.get(0)).isEqualToComparingFieldByFieldRecursively(data.get(0));
	}

	@Test
	public void dataBlockWithMatchingNameExists_testPatchDataPostCallWorksAsExpected() throws Exception {
		//Arrange
		when(serverMock.updateDataEnvelope("Data block", BlockTypeEnum.BLOCKTYPEA)).thenReturn(HttpStatus.OK);

		//Act
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand("Data block", BlockTypeEnum.BLOCKTYPEA)))
				.andExpect(status().isOk())
				.andReturn();

		boolean success = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());

		//Assert
		assertThat(success).isTrue();
	}

	@Test
	public void dataBlockNotFound_testPatchDataPostCallWorksAsExpected() throws Exception {
		//Arrange
		when(serverMock.updateDataEnvelope("Data block", BlockTypeEnum.BLOCKTYPEA)).thenReturn(HttpStatus.NOT_FOUND);

		//Act
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand("Data block", BlockTypeEnum.BLOCKTYPEA)))
				.andExpect(status().isNotFound())
				.andReturn();

		boolean success = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());

		//Assert
		assertThat(success).isFalse();
	}
}
