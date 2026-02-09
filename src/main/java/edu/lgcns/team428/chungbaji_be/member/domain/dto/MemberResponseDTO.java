package edu.lgcns.team428.chungbaji_be.member.domain.dto;

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
    private String nickname ;

    // 변환 
    public static MemberResponseDTO fromEntity(MemberEntity entity){
        return MemberResponseDTO.builder().nickname(entity.getNickname()).build();
    }
}