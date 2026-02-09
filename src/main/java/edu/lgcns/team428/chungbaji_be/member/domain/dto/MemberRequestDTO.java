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
    private char gender;
    private LocalDate birth_date;
    
    // 코드로 변환 필요 
    private String region;
    private String income ;

    // 외부 테이블 참조 필요 
    public static MemberEntity toEntity(MemberRequestDTO request){
        return MemberEntity.builder().email(request.getEmail()).password(request.getPassword())
        .build();
    }
}
