package edu.lgcns.team428.chungbaji_be.code.domain.controller;

import edu.lgcns.team428.chungbaji_be.code.domain.dto.CodeResponseDTO;
import edu.lgcns.team428.chungbaji_be.code.domain.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/codes")
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;

    @GetMapping("/{codeGroupName}")
    public ResponseEntity<List<CodeResponseDTO>> getCommonCodes(@PathVariable String codeGroupName) {
        return ResponseEntity.ok(codeService.getCodesByCodeGroupName(codeGroupName));
    }
}
