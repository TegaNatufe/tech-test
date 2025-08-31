package org.hmxlabs.techtest.server.component;

import org.hmxlabs.techtest.server.api.model.DataEnvelope;
import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope) throws IOException, NoSuchAlgorithmException;
    List<DataEnvelope> getData(BlockTypeEnum blockType);
    HttpStatus updateDataEnvelope(String name, BlockTypeEnum newBlockType);
}
