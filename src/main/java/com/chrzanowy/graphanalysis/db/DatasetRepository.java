package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.db.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {

}
