
package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.db.model.AlgorithmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgorithmRepository extends JpaRepository<AlgorithmStatus, String> {

}
