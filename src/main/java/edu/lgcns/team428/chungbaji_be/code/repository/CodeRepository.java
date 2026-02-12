package edu.lgcns.team428.chungbaji_be.code.repository;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, Integer> {
    List<CodeEntity> findByCodeGroupOrderByCodeAsc(String codeGroup);
    Optional<CodeEntity> findByCodeGroupAndCodeDesc(String codeGroup, String codeDesc);
}
