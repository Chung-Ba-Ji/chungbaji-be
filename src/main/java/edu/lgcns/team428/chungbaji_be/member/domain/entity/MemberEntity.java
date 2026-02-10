package edu.lgcns.team428.chungbaji_be.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

// 참조 엔티티(CodeEntity, RegionEntity) 생성되면 주석 해제할 것

@Entity
@Table(name = "MEMBER")
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int member_id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNum;

    @Column(nullable = false, length = 1)
    private String gender;

    private LocalDate birthDate;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "region_code", referencedColumnName = "region_code")
    // private RegionEntity region;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "education_code", referencedColumnName = "code_id")
    // private CodeEntity education;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "job_code", referencedColumnName = "code_id")
    // private CodeEntity job;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "major_code", referencedColumnName = "code_id")
    // private CodeEntity major;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "income_code", referencedColumnName = "code_id")
    // private CodeEntity income;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "special_code", referencedColumnName = "code_id")
    // private CodeEntity special;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    public enum MemberStatus {
        ACTIVE, INACTIVE, WITHDRAWN
    }


}
