package org.hmxlabs.techtest.service;

import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.hmxlabs.techtest.server.persistence.model.DataBodyEntity;
import org.hmxlabs.techtest.server.persistence.model.DataHeaderEntity;
import org.hmxlabs.techtest.server.persistence.repository.DataStoreRepository;
import org.hmxlabs.techtest.server.service.DataBodyService;
import org.hmxlabs.techtest.server.service.impl.DataBodyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hmxlabs.techtest.TestDataHelper.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataBodyServiceTests {

    public static final String TEST_NAME_NO_RESULT = "TestNoResult";

    @Mock
    private DataStoreRepository dataStoreRepositoryMock;

    private DataBodyService dataBodyService;
    private DataBodyEntity expectedDataBodyEntity;

    @BeforeEach
    public void setup() {
        DataHeaderEntity testDataHeaderEntity = createTestDataHeaderEntity(Instant.now());
        expectedDataBodyEntity = createTestDataBodyEntity(testDataHeaderEntity);

        dataBodyService = new DataBodyServiceImpl(dataStoreRepositoryMock);
    }

    @Test
    public void shouldSaveDataBodyEntityAsExpected(){
        dataBodyService.saveDataBody(expectedDataBodyEntity);

        verify(dataStoreRepositoryMock, times(1))
                .save(eq(expectedDataBodyEntity));
    }

    @Test
    public void getDataByBlockType_blockTypeAIsRequested_dataBlocksWithBlockTypeAExist_dataBlocksAreRetrievedSuccessfully() {
        //Arrange
        DataBodyEntity dataBlockA = createTestDataBodyEntityWithBlockType("Data block 1", BlockTypeEnum.BLOCKTYPEA);
        DataBodyEntity dataBlockB = createTestDataBodyEntityWithBlockType("Data block 2", BlockTypeEnum.BLOCKTYPEB);

        List<DataBodyEntity> dataBlocks = List.of(
                dataBlockA,
                dataBlockB
        );

        when(dataStoreRepositoryMock.findAll()).thenReturn(dataBlocks);

        //Act
        List<DataBodyEntity> expectedData = dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);

        //Assert
        assertThat(expectedData.size()).isEqualTo(1);
        assertThat(expectedData.get(0)).isEqualTo(dataBlockA);
    }

    @Test
    public void getDataByBlockType_blockTypeBIsRequested_onlyDataBlocksWithBlockTypeAExist_emptyListIsRetrievedSuccessfully() {
        //Arrange
        DataBodyEntity dataBlock1 = createTestDataBodyEntityWithBlockType("Data block 1", BlockTypeEnum.BLOCKTYPEA);
        DataBodyEntity dataBlock2 = createTestDataBodyEntityWithBlockType("Data block 2", BlockTypeEnum.BLOCKTYPEA);

        List<DataBodyEntity> dataBlocks = List.of(
                dataBlock1,
                dataBlock2
        );

        when(dataStoreRepositoryMock.findAll()).thenReturn(dataBlocks);

        //Act
        List<DataBodyEntity> expectedData = dataBodyService.getDataByBlockType(BlockTypeEnum.BLOCKTYPEB);

        //Assert
        assertThat(expectedData).isNotNull();
        assertThat(expectedData.size()).isEqualTo(0);
    }

    @Test
    public void getDataByBlockName_dataBlockRetrievedSuccessfully() {
        //Arrange
        DataBodyEntity dataBlockA = createTestDataBodyEntityWithBlockType("Data block 1", BlockTypeEnum.BLOCKTYPEA);
        DataBodyEntity dataBlockB = createTestDataBodyEntityWithBlockType("Data block 2", BlockTypeEnum.BLOCKTYPEB);

        List<DataBodyEntity> dataBlocks = List.of(
                dataBlockA,
                dataBlockB
        );

        when(dataStoreRepositoryMock.findAll()).thenReturn(dataBlocks);

        //Act
        Optional<DataBodyEntity> expectedDataBlock = dataBodyService.getDataByBlockName("Data block 1");

        //Assert
        assertThat(expectedDataBlock.isPresent()).isEqualTo(true);
        assertThat(expectedDataBlock.get()).isEqualTo(dataBlockA);
    }

    @Test
    public void getDataByBlockName_dataBlockDoesNotExist_emptyOptionalRetrievedSuccessfully() {
        //Arrange
        DataBodyEntity dataBlockA = createTestDataBodyEntityWithBlockType("Data block 1", BlockTypeEnum.BLOCKTYPEA);
        DataBodyEntity dataBlockB = createTestDataBodyEntityWithBlockType("Data block 2", BlockTypeEnum.BLOCKTYPEB);

        List<DataBodyEntity> dataBlocks = List.of(
                dataBlockA,
                dataBlockB
        );

        when(dataStoreRepositoryMock.findAll()).thenReturn(dataBlocks);

        //Act
        Optional<DataBodyEntity> expectedDataBlock = dataBodyService.getDataByBlockName("Data block 3");

        //Assert
        assertThat(expectedDataBlock.isEmpty()).isEqualTo(true);
    }
}
