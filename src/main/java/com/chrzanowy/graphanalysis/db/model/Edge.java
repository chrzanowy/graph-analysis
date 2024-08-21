package com.chrzanowy.graphanalysis.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "EDGE")
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long sourceId;

    private Long targetId;

    private Long datasetId;

    public Edge(Long sourceId, Long targetId, Long datasetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.datasetId = datasetId;
    }
}
