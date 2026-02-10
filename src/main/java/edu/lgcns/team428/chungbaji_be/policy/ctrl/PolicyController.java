package edu.lgcns.team428.chungbaji_be.policy.ctrl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import edu.lgcns.team428.chungbaji_be.policy.domain.dto.*;
import edu.lgcns.team428.chungbaji_be.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

    // 데이터 동기화
    @PostMapping("/sync")
    public ResponseEntity<String> sync() {
        policyService.syncPolicies();
        return ResponseEntity.ok("Sync Success");
    }

    // 필터링 검색 (기존에 만드신 것)
    @GetMapping("/list")
    public ResponseEntity<List<PolicyResponseDTO>> getList(PolicySearchDTO searchDTO) {
        return ResponseEntity.ok(policyService.searchPolicies(searchDTO));
    }

    // 상세 조회 + 조회수 증가 (한번에 처리하거나 따로 분리)
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(policyService.getPolicyDetailAndIncreaseViewCount(id));
    }
}
