package com.chrzanowy.graphanalysis.db.model;

import com.chrzanowy.graphanalysis.config.TimeProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "DATASET")
@NoArgsConstructor
@Getter
@ToString
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime lastUpdated;

    public Dataset(String name) {
        this.name = name;
        this.lastUpdated = TimeProvider.getCurrentTime();
    }
}
