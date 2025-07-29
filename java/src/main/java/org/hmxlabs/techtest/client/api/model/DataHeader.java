package org.hmxlabs.techtest.client.api.model;

import org.hmxlabs.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@RequiredArgsConstructor
public class DataHeader {

    @NotNull
    public final String name;

    @NotNull
    private final BlockTypeEnum blockType;

}
