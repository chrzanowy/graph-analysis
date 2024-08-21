package com.chrzanowy.graphanalysis.db.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ALGORITHM_STATUS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AlgorithmStatus {

    @Id
    private String id;

    private boolean enabled;

}
