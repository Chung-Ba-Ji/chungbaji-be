package edu.lgcns.team428.chungbaji_be.policy.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "policy")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "biz_id", nullable = false, unique = true)
    private String bizId; // API의 plcyNo 저장용

    @Column(nullable = false)
    private String title;

    @Column(name = "policy_description", columnDefinition = "TEXT")
    private String policyDescription;

    @Column(name = "support_content", columnDefinition = "TEXT")
    private String supportContent;

    @Column(name = "plcy_kywd_nm", columnDefinition = "TEXT")
    private String keyword;

    @Column(name = "sbiz_cd")
    private String sbizCd;

    @Column(name = "category_main", nullable = false)
    private Integer categoryMain; // 대분류 (code 테이블 참조 예정)

    @Column(name = "category_sub")
    private Integer categorySub; // 중분류 (code 테이블 참조 예정)

    @Column(name = "region_code", length = 5)
    private String regionCode; // 지역 코드 (region 테이블 참조 예정)

    @Column(name = "education_code")
    private Integer educationCode;

    @Column(name = "job_code")
    private Integer jobCode;

    @Column(name = "major_code")
    private Integer majorCode;

    @Column(name = "income_code")
    private Integer incomeCode;

    @Column(name = "special_code")
    private Integer specialCode;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "apply_start_date")
    private LocalDate applyStartDate;

    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "detail_url", length = 2048, nullable = false)
    private String detailUrl;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;
}