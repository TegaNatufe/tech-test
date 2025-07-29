package org.hmxlabs.techtest.server.service;

import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.hmxlabs.techtest.server.persistence.model.DataBodyEntity;

import java.util.List;
import java.util.Optional;

public interface DataBodyService {
    void saveDataBody(DataBodyEntity dataBody);
    List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType);
    Optional<DataBodyEntity> getDataByBlockName(String blockName);
}
