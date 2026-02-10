package edu.lgcns.team428.chungbaji_be.policy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Integer> {
    

    Optional<PolicyEntity> findByBizId(String bizId);
    
    //조건필터링
    @Query("SELECT p FROM PolicyEntity p WHERE " +
        "p.regionCode = :regionCode AND " +
        "p.minAge <= :age AND p.maxAge >= :age AND " +
        "p.jobCode = :jobCode AND " +
        "p.educationCode = :educationCode AND " +
        "(:majorCode IS NULL OR p.majorCode = :majorCode) AND " + 
        "(:incomeCode IS NULL OR p.incomeCode = :incomeCode) AND " + 
        "(:specialCode IS NULL OR p.specialCode = :specialCode)")
    List<PolicyEntity> findFilteredPolicies(
        @Param("regionCode") String regionCode, 
        @Param("age") Integer age, 
        @Param("jobCode") Integer jobCode, 
        @Param("educationCode") Integer educationCode,
        @Param("majorCode") Integer majorCode, 
        @Param("incomeCode") Integer incomeCode, 
        @Param("specialCode") Integer specialCode
    );

}