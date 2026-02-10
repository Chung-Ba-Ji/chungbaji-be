package edu.lgcns.team428.chungbaji_be.policy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Integer> {
    

    Optional<PolicyEntity> findByBizId(String bizId);

    // 조건 필터링: 값이 0이거나 NULL이면 해당 조건은 무시(Pass)하도록 수정
    @Query("SELECT p FROM PolicyEntity p WHERE " +
        "(:regionCode IS NULL OR :regionCode = '' OR p.regionCode = :regionCode) AND " +
        "(:age = 0 OR (p.minAge <= :age AND p.maxAge >= :age)) AND " +
        "(:jobCode = 0 OR p.jobCode = :jobCode) AND " +
        "(:educationCode = 0 OR p.educationCode = :educationCode) AND " +
        "(:majorCode = 0 OR p.majorCode = :majorCode) AND " + 
        "(:incomeCode = 0 OR p.incomeCode = :incomeCode) AND " + 
        "(:specialCode = 0 OR p.specialCode = :specialCode)")
    List<PolicyEntity> findFilteredPolicies(
        @Param("regionCode") String regionCode, 
        @Param("age") Integer age, 
        @Param("jobCode") Integer jobCode, 
        @Param("educationCode") Integer educationCode,
        @Param("majorCode") Integer majorCode, 
        @Param("incomeCode") Integer incomeCode, 
        @Param("specialCode") Integer specialCode
    );

    // 인기 정책 TOP 3 (조회수 기준)
    List<PolicyEntity> findTop3ByOrderByViewCountDesc();

    // 마감 임박 정책 TOP 3 (마감일이 오늘 이후인 것 중 가장 가까운 순)
    @Query("SELECT p FROM PolicyEntity p WHERE p.applyEndDate >= :today ORDER BY p.applyEndDate ASC")
    List<PolicyEntity> findTop3ByApplyEndDateAfterOrderByApplyEndDateAsc(@Param("today") LocalDate today);

    // 정책 키워드 중복 제거해서 가져오기 (해시태그용)
    @Query("SELECT DISTINCT p.keyword FROM PolicyEntity p WHERE p.keyword IS NOT NULL")
    List<String> findAllDistinctKeywords();

}