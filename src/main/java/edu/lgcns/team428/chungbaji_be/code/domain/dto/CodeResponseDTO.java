package edu.lgcns.team428.chungbaji_be.code.domain.dto;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.Code;
import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponseDTO {
    private String codeId;
    private String codeGroup;
    private String code;
    private String codeDesc;

    public static CodeResponseDTO fromEntity(Code entity) {
        return CodeResponseDTO.builder()
                .codeGroup(entity.getCodeGroup().getCodeGroup())
                .code(entity.getCode())
                .codeDesc(entity.getCodeDesc())
                .build();
    }
}
