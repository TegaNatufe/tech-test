package org.hmxlabs.techtest.server.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "DATA_STORE")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataBodyEntity {

    @Id
    @SequenceGenerator(name = "dataStoreSequenceGenerator", sequenceName = "SEQ_DATA_STORE", allocationSize = 1)
    @GeneratedValue(generator = "dataStoreSequenceGenerator")
    @Column(name = "DATA_STORE_ID")
    private Long dataStoreId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DATA_HEADER_ID")
    private DataHeaderEntity dataHeaderEntity;

    @Column(name = "DATA_BODY")
    private String dataBody;

    @Column(name = "CREATED_TIMESTAMP")
    private Instant createdTimestamp;

    @Column(name = "MD5_CHECKSUM")
    private String md5Checksum;

    @PrePersist
    public void setTimestamps() {
        if (createdTimestamp == null) {
            createdTimestamp = Instant.now();
        }
    }
}
