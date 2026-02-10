package edu.lgcns.team428.chungbaji_be.policy.ctrl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import edu.lgcns.team428.chungbaji_be.policy.domain.dto.*;
import edu.lgcns.team428.chungbaji_be.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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

    // 필터링 검색 
    @GetMapping("/list")
    public ResponseEntity<List<PolicyResponseDTO>> getList(PolicySearchDTO searchDTO) {
        return ResponseEntity.ok(policyService.searchPolicies(searchDTO));
    }


    // 상세 조회 + 조회수 증가
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(policyService.getPolicyDetailAndIncreaseViewCount(id));
    }


    // 위젯용 인기/마감임박 데이터
    @GetMapping("/widgets")
    public ResponseEntity<Map<String, List<PolicyResponseDTO>>> getWidgets() {
        return ResponseEntity.ok(policyService.getWidgetData());
    }

    // 해시태그(키워드) 목록
    @GetMapping("/keywords")
    public ResponseEntity<List<String>> getKeywords() {
        return ResponseEntity.ok(policyService.getAllKeywords());
    }

    // 맞춤형 정책 추천 (로그인한 유저 정보를 SearchDTO 형태로 넘겨받음)
    @GetMapping("/recommend")
    public ResponseEntity<List<PolicyResponseDTO>> getRecommend(PolicySearchDTO searchDTO) {
        return ResponseEntity.ok(policyService.getRecommendPolicies(searchDTO));
    }
}

