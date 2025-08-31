package org.hmxlabs.techtest.client.component.impl;

import org.hmxlabs.techtest.client.api.model.DataEnvelope;
import org.hmxlabs.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHBIGDATA = "http://localhost:8090/hadoopserver/pushbigdata";

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    private final WebClient webClient;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);

        boolean isThereAMatchWithClientProvidedChecksum = webClient.post()
                .uri(URI_PUSHDATA)
                .bodyValue(dataEnvelope)
                .retrieve()
                .bodyToMono(boolean.class)
                .block();

        webClient.post()
                .uri(URI_PUSHBIGDATA)
                .bodyValue(dataEnvelope)
                .retrieve()
                .toBodilessEntity()
                .toFuture();
    }

    @Override
    public List<DataEnvelope> getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);

        DataEnvelope[] dataWithMatchingBlockType = webClient.get()
                .uri(URI_GETDATA.expand(blockType))
                .retrieve()
                .bodyToMono(DataEnvelope[].class)
                .block();

        return Arrays.asList(dataWithMatchingBlockType);
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);

        boolean isSuccess = webClient.patch()
                .uri(URI_PATCHDATA.expand(blockName, newBlockType))
                .retrieve()
                .bodyToMono(boolean.class)
                .block();

        return isSuccess;
    }


}
