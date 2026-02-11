package edu.lgcns.team428.chungbaji_be.code.repository;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Integer> {
    Optional<CodeEntity> findByCodeGroupAndCodeDesc(String codeGroup, String codeDesc);
}
