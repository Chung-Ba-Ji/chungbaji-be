package edu.lgcns.team428.chungbaji_be.code.repository;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<Code, Integer> {
}
