package edu.lgcns.team428.chungbaji_be.member.domain.dto;

import java.time.LocalDate;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {
    private String email;
    private String password;
    private String nickname;
    private String phone_num;
    private String gender;
    private LocalDate birth_date;

    private String region;
    private String education;
    private String job;
    private String major;
    private String income;
    private String special;


    // 이후에 code(int)로 변환 로직 추가
    public MemberEntity toEntity() {
        return MemberEntity.builder().email(email).password(password)
                .nickname(nickname).phoneNum(phone_num)
                .gender(gender).birthDate(birth_date).build();
    }

    

}
