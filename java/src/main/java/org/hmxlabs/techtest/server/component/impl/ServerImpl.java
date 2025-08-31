package org.hmxlabs.techtest.server.component.impl;

import org.hmxlabs.techtest.TechTestApplication;
import org.hmxlabs.techtest.server.api.model.DataBody;
import org.hmxlabs.techtest.server.api.model.DataEnvelope;
import org.hmxlabs.techtest.server.api.model.DataHeader;
import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.hmxlabs.techtest.server.persistence.model.DataBodyEntity;
import org.hmxlabs.techtest.server.persistence.model.DataHeaderEntity;
import org.hmxlabs.techtest.server.service.DataBodyService;
import org.hmxlabs.techtest.server.component.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) throws NoSuchAlgorithmException {
        String theMD5Checksum = calculateMD5Checksum(envelope.getDataBody().getDataBody());
        boolean hashMatchesClientProvidedChecksum = theMD5Checksum.equals(TechTestApplication.MD5_CHECKSUM);

        // Save to persistence.
        persist(envelope);

        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        return hashMatchesClientProvidedChecksum;
    }

    /**
     * @param blockType
     * @return Returns a list of data blocks with a matching blockType.
     */
    @Override
    public List<DataEnvelope> getData(BlockTypeEnum blockType) {
        List<DataBodyEntity> dataBodyEntities = dataBodyServiceImpl.getDataByBlockType(blockType);
        return mapToDataEnvelopes(dataBodyEntities);
    }

    /**
     * @param name
     * @param newBlockType
     * @return Updates an existing block's data type. Data block can be uniquely identified by its name
     */
    @Override
    public HttpStatus updateDataEnvelope(String name, BlockTypeEnum newBlockType) {
        Optional<DataBodyEntity> dataBodyEntityOptional = dataBodyServiceImpl.getDataByBlockName(name);

        if(dataBodyEntityOptional.isEmpty()) {
            log.info("Data could not be found, data name: {}", name);
            return HttpStatus.NOT_FOUND;
        }

        DataBodyEntity dataBodyEntity = dataBodyEntityOptional.get();
        dataBodyEntity.getDataHeaderEntity().setBlocktype(newBlockType);
        saveData(dataBodyEntity);

        log.info("Data updated successfully, data name: {}", name);
        return HttpStatus.OK;
    }

    private String calculateMD5Checksum(String dataBody) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = dataBody.getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] theMD5digest = messageDigest.digest(bytesOfMessage);
        BigInteger bigInt = new BigInteger(1, theMD5digest);
        String theMD5Checksum = bigInt.toString(16);

        return theMD5Checksum;
    }

    private List<DataEnvelope> mapToDataEnvelopes(List<DataBodyEntity> dataBodyEntities) {
        List<DataEnvelope> dataEnvelopes = new ArrayList<DataEnvelope>();

        for(DataBodyEntity dataBodyEntity : dataBodyEntities) {
            DataHeader dataHeader = new DataHeader(dataBodyEntity.getDataHeaderEntity().getName(), dataBodyEntity.getDataHeaderEntity().getBlocktype());
            DataBody dataBody = new DataBody(dataBodyEntity.getDataBody());
            DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);

            dataEnvelopes.add(dataEnvelope);
        }

        return dataEnvelopes;
    }

    private void persist(DataEnvelope envelope) throws NoSuchAlgorithmException {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        dataBodyEntity.setMd5Checksum(calculateMD5Checksum(dataBodyEntity.getDataBody()));

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

}
