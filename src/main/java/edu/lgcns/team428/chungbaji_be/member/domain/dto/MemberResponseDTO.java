package edu.lgcns.team428.chungbaji_be.member.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private int memberId;
    private String email;
    private String nickname;
    private String phoneNum;
    private String gender;
    private LocalDate birthDate;

    private String region;
    private String education;
    private String job;
    private String major;
    private String income;
    private String special;

    // enum
    private MemberEntity.MemberStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 코드를 문자열로 변환해서 반환 
    public static MemberResponseDTO fromEntity(MemberEntity entity) {

        return MemberResponseDTO.builder()
                .memberId(entity.getMemberId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .phoneNum(entity.getPhoneNum())
                .gender(entity.getGender())
                .birthDate(entity.getBirthDate())

                // 사람이 이해하는 문자열로 변환
                .region(
                        entity.getRegion() != null
                                ? entity.getRegion().getRegionName()
                                : null)
                .education(
                        entity.getEducation() != null
                                ? entity.getEducation().getCodeDesc()
                                : null)
                .job(
                        entity.getJob() != null
                                ? entity.getJob().getCodeDesc()
                                : null)
                .major(
                        entity.getMajor() != null
                                ? entity.getMajor().getCodeDesc()
                                : null)
                .income(
                        entity.getIncome() != null
                                ? entity.getIncome().getCodeDesc()
                                : null)
                .special(
                        entity.getSpecial() != null
                                ? entity.getSpecial().getCodeDesc()
                                : null)

                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}