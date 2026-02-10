package edu.lgcns.team428.chungbaji_be.member.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;
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

@Entity
@Table(name = "member")
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

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

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_code", referencedColumnName = "region_code")
    private RegionEntity region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_code", referencedColumnName = "code_id")
    private CodeEntity education;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_code", referencedColumnName = "code_id")
    private CodeEntity job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_code", referencedColumnName = "code_id")
    private CodeEntity major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_code", referencedColumnName = "code_id")
    private CodeEntity income;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_code", referencedColumnName = "code_id")
    private CodeEntity special;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE; // 기본적으로 회원 정보 생성 시 자동 활성화

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

    public enum MemberStatus {
        ACTIVE, INACTIVE, WITHDRAWN
    }

    // 비번 해싱 저장
    public static MemberEntity from(MemberRequestDTO dto, String password) {
        return MemberEntity.builder().email(dto.getEmail()).password(password)
                .nickname(dto.getNickname()).phoneNum(dto.getPhone_num())
                .gender(dto.getGender()).birthDate(dto.getBirth_date()).build();
    }

    public void updatePwd(String encodedPassword) {
        password = encodedPassword;
    }

    // 회원가입 조건처리용: 이메일이 DB에 존재하지만 비활성화인 경우 재활성화(INACTIVE -> ACTIVE)
    public void reactivate(
            String encodedPassword,
            String nickname,
            String phoneNum,
            String gender,
            LocalDate birthDate,
            RegionEntity region,
            CodeEntity education,
            CodeEntity job,
            CodeEntity major,
            CodeEntity income,
            CodeEntity special) {
        this.password = encodedPassword;
        this.nickname = nickname;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.birthDate = birthDate;
        this.region = region;
        this.education = education;
        this.job = job;
        this.major = major;
        this.income = income;
        this.special = special;
        this.status = MemberStatus.ACTIVE;
    }

    // 회원 탈퇴 시 상태 변경
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    // 회원정보 수정용
    public void updateProfile(
            String nickname,
            String phoneNum,
            String gender,
            LocalDate birthDate,
            RegionEntity region,
            CodeEntity education,
            CodeEntity job,
            CodeEntity major,
            CodeEntity income,
            CodeEntity special) {
        if (nickname != null && !nickname.isBlank())
            this.nickname = nickname;
        if (phoneNum != null && !phoneNum.isBlank())
            this.phoneNum = phoneNum;
        if (gender != null && !gender.isBlank())
            this.gender = gender;

        // 생년월일은 null 허용 정책이면 그대로 세팅, 아니면 조건 걸어도 됨
        if (birthDate != null)
            this.birthDate = birthDate;

        // 코드/지역은 null이면 "변경하지 않음"으로 처리
        if (region != null)
            this.region = region;
        if (education != null)
            this.education = education;
        if (job != null)
            this.job = job;
        if (major != null)
            this.major = major;
        if (income != null)
            this.income = income;
        if (special != null)
            this.special = special;
    }

}
