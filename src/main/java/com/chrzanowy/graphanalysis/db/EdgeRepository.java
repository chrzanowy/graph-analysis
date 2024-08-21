package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.db.model.Edge;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, Long> {

    Stream<Edge> findAllByDatasetId(long id);
}
