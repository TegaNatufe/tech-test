package org.hmxlabs.techtest.service;

import org.hmxlabs.techtest.server.api.model.DataBody;
import org.hmxlabs.techtest.server.api.model.DataEnvelope;
import org.hmxlabs.techtest.server.api.model.DataHeader;
import org.hmxlabs.techtest.server.component.Server;
import org.hmxlabs.techtest.server.component.impl.ServerImpl;
import org.hmxlabs.techtest.server.mapper.ServerMapperConfiguration;
import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.hmxlabs.techtest.server.persistence.model.DataBodyEntity;
import org.hmxlabs.techtest.server.persistence.model.DataHeaderEntity;
import org.hmxlabs.techtest.server.service.DataBodyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hmxlabs.techtest.TestDataHelper.*;
import static org.hmxlabs.techtest.TestDataHelper.createTestDataBodyEntityWithBlockType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    private ModelMapper modelMapper;

    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    private Server server;

    @BeforeEach
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper);
    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);

        assertThat(success).isTrue();
        //verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    @Test
    public void saveDataEnvelope_MD5ChecksumMatchesClientChecksum_savesSuccessfullyAndReturnsTrue() throws NoSuchAlgorithmException, IOException {
        //Arrange
        DataEnvelope dataEnvelope = new DataEnvelope(
                new DataHeader("Data block", BlockTypeEnum.BLOCKTYPEA),
                new DataBody(DUMMY_DATA)
        );

        DataBodyEntity dataBodyEntityToExpect = modelMapper.map(dataEnvelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntityToExpect.setDataHeaderEntity(modelMapper.map(dataEnvelope.getDataHeader(), DataHeaderEntity.class));

        //Act
        boolean doCheckSumsMatch = server.saveDataEnvelope(dataEnvelope);

        //Assert
        assertThat(doCheckSumsMatch).isTrue();
    }

    @Test
    public void saveDataEnvelope_MD5ChecksumDoesNotMatchClientChecksum_savesSuccessfullyAndReturnsFalse() throws NoSuchAlgorithmException, IOException {        //Arrange
        DataEnvelope dataEnvelope = new DataEnvelope(
                new DataHeader("Data block", BlockTypeEnum.BLOCKTYPEA),
                new DataBody("RaNdOm1SeQuEnCe2Of3ChArAcTeRs4")
        );

        DataBodyEntity dataBodyEntityToExpect = modelMapper.map(dataEnvelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntityToExpect.setDataHeaderEntity(modelMapper.map(dataEnvelope.getDataHeader(), DataHeaderEntity.class));

        //Act
        boolean doCheckSumsMatch = server.saveDataEnvelope(dataEnvelope);

        //Assert
        assertThat(doCheckSumsMatch).isFalse();
    }

    @Test
    public void getData_blockTypeARequested_dataReturned()
    {
        //Arrange
        DataBodyEntity dataBlock1 = createTestDataBodyEntityWithBlockType("Data block 1", BlockTypeEnum.BLOCKTYPEA);
        DataBodyEntity dataBlock2 = createTestDataBodyEntityWithBlockType("Data block 2", BlockTypeEnum.BLOCKTYPEA);

        List<DataBodyEntity> dataBlocks = List.of(
                dataBlock1,
                dataBlock2
        );

        DataEnvelope dataEnvelope1 = new DataEnvelope(
                new DataHeader("Data block 1", BlockTypeEnum.BLOCKTYPEA),
                new DataBody(DUMMY_DATA)
        );
        DataEnvelope dataEnvelope2 = new DataEnvelope(
                new DataHeader("Data block 2", BlockTypeEnum.BLOCKTYPEA),
                new DataBody(DUMMY_DATA)
        );

        List<DataEnvelope> expectedData = List.of(
                dataEnvelope1,
                dataEnvelope2
        );

        when(dataBodyServiceImplMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(dataBlocks);

        //Act
        List<DataEnvelope> actualData = server.getData(BlockTypeEnum.BLOCKTYPEA);

        //Assert
        assertThat(actualData.size()).isEqualTo(2);
        assertThat(actualData.get(0)).isEqualToComparingFieldByFieldRecursively(expectedData.get(0));
        assertThat(actualData.get(1)).isEqualToComparingFieldByFieldRecursively(expectedData.get(1));
    }

    @Test
    public void updateDataEnvelope_success() {
        //Arrange
        String dataBlockName = "Data block";
        DataBodyEntity dataBodyEntity = createTestDataBodyEntityWithBlockType(dataBlockName, BlockTypeEnum.BLOCKTYPEA);

        when(dataBodyServiceImplMock.getDataByBlockName(dataBlockName)).thenReturn(Optional.of(dataBodyEntity));

        //Act
        HttpStatus httpStatus = server.updateDataEnvelope(dataBlockName, BlockTypeEnum.BLOCKTYPEB);

        //Assert
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateDataEnvelope_notFound() {
        //Arrange
        String dataBlockName = "Data block";
        DataBodyEntity dataBodyEntity = createTestDataBodyEntityWithBlockType(dataBlockName, BlockTypeEnum.BLOCKTYPEA);

        when(dataBodyServiceImplMock.getDataByBlockName(dataBlockName)).thenReturn(Optional.empty());

        //Act
        HttpStatus httpStatus = server.updateDataEnvelope(dataBlockName, BlockTypeEnum.BLOCKTYPEB);

        //Assert
        assertThat(httpStatus).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
