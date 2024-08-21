package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.db.model.Node;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends CrudRepository<Node, Long> {

}
