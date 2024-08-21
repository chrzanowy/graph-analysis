package com.chrzanowy.graphanalysis.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "NODE")
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Node {

    private Long nodeId;

    private Long datasetId;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public Node(Long nodeId, Long datasetId) {
        this.nodeId = nodeId;
        this.datasetId = datasetId;
    }
}
