package edu.lgcns.team428.chungbaji_be.code.domain.service;

import edu.lgcns.team428.chungbaji_be.code.domain.dto.CodeResponseDTO;
import edu.lgcns.team428.chungbaji_be.code.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeRepository codeRepository;

    public List<CodeResponseDTO> getCodesByCodeGroupName(String codeGroupName) {
        String codeGroup = switch (codeGroupName) {
            case "jobs" -> "0013";
            case "educations" -> "0049";
            case "majors" -> "0011";
            case "incomes" -> "0043";
            case "specials" -> "0014";
            default -> throw new IllegalArgumentException("잘못된 카테고리입니다: " + codeGroupName);
        };

        return codeRepository.findByCodeGroupOrderByCodeAsc(codeGroup)
                .stream()
                .map(CodeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
