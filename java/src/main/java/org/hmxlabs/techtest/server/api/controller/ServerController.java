package org.hmxlabs.techtest.server.api.controller;

import org.hmxlabs.techtest.server.api.model.DataEnvelope;
import org.hmxlabs.techtest.server.component.Server;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {

        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);

        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }

    @GetMapping(value = "/data/{blockType}")
    public ResponseEntity<List<DataEnvelope>> getData(@Valid @PathVariable("blockType") BlockTypeEnum blockType) {

        log.info("Query received for data with block type {}", blockType);
        List<DataEnvelope> data = server.getData(blockType);

        log.info("Query completed for data with block type: {}", blockType);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(value = "/update/{name}/{newBlockType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateData(@Valid @PathVariable("name") String name, @Valid @PathVariable("newBlockType") BlockTypeEnum newBlockType) {

        log.info("Request received to update data block {} with the block type {}", name, newBlockType);
        HttpStatus status = server.updateDataEnvelope(name, newBlockType);

        if(status == HttpStatus.NOT_FOUND) {
            log.info("Request failed: data block {} could not be found", name, newBlockType);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Request completed to update data block {} with the block type {}", name, newBlockType);
        return ResponseEntity.ok(true);
    }
}
