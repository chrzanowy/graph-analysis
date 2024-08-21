package com.chrzanowy.graphanalysis.db.model;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.db.converter.HashMapConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ANALYSIS")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long datasetId;

    @Enumerated(EnumType.STRING)
    private Algorithm algorithmName;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> analysisData;

    private LocalDateTime executionTime;

    private LocalDateTime completionTime;

    public Analysis(Long datasetId, Algorithm algorithmName, Map<String, Object> analysisData, LocalDateTime executionTime, LocalDateTime completionTime) {
        this.datasetId = datasetId;
        this.algorithmName = algorithmName;
        this.analysisData = analysisData;
        this.executionTime = executionTime;
        this.completionTime = completionTime;
    }
}
