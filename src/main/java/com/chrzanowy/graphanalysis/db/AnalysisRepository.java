
package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends CrudRepository<Analysis, UUID> {

    List<Analysis> findAllByAlgorithmName(Algorithm algorithmName);

}
