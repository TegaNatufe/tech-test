package org.hmxlabs.techtest.persistence.model;

import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.hmxlabs.techtest.server.persistence.model.DataBodyEntity;
import org.hmxlabs.techtest.server.persistence.model.DataHeaderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hmxlabs.techtest.TestDataHelper.TEST_NAME;
import static org.hmxlabs.techtest.TestDataHelper.createTestDataBodyEntity;

@ExtendWith(MockitoExtension.class)
public class DataBodyEntityTests {

    @Test
    public void assignDataBodyEntityFieldsShouldWorkAsExpected() {
        Instant expectedTimestamp = Instant.now();

        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);

        DataBodyEntity dataBodyEntity = createTestDataBodyEntity(dataHeaderEntity);

        assertThat(dataBodyEntity.getDataHeaderEntity()).isNotNull();
        assertThat(dataBodyEntity.getDataBody()).isNotNull();
    }

    /**
     * TODO: This test intentionally fails and the code it tests against will need to be fixed.
     */
    @Test
    public void checkTwoDataBodiesAreEqualAsExpected() {
        Instant expectedTimestamp = Instant.now();

        DataHeaderEntity dataHeaderEntity1 = new DataHeaderEntity();
        dataHeaderEntity1.setName(TEST_NAME);
        dataHeaderEntity1.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity1.setCreatedTimestamp(expectedTimestamp);
        DataBodyEntity dataBodyEntity1 = createTestDataBodyEntity(dataHeaderEntity1);

        DataHeaderEntity dataHeaderEntity2 = new DataHeaderEntity();
        dataHeaderEntity2.setName(TEST_NAME);
        dataHeaderEntity2.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity2.setCreatedTimestamp(expectedTimestamp);
        DataBodyEntity dataBodyEntity2 = createTestDataBodyEntity(dataHeaderEntity2);

        assertThat(dataBodyEntity1).isEqualToComparingFieldByFieldRecursively(dataBodyEntity2);
    }
}
