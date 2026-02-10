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

    // 추후에 코드를 문자열로 변환하는 로직 추가
    public static MemberResponseDTO fromEntity(MemberEntity entity) {
        return MemberResponseDTO.builder().memberId(entity.getMember_id()).email(entity.getEmail())
                .nickname(entity.getNickname()).phoneNum(entity.getPhoneNum()).gender(entity.getGender())
                .birthDate(entity.getBirthDate()).status(entity.getStatus()).createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt()).build();
    }

}