package edu.lgcns.team428.chungbaji_be.policy.service;

import edu.lgcns.team428.chungbaji_be.policy.dao.PolicyRepository;
import edu.lgcns.team428.chungbaji_be.policy.domain.dto.*;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${YOUTH_POLICY_API_URL}")
    private String apiUrl;

    @Value("${YOUTH_POLICY_API_KEY}")
    private String apiKey;

    @Transactional
    public void syncPolicies() {
        // 3. dataType=json 추가로 JSON 응답 강제
        String fullUrl = apiUrl + "?openApiVlak=" + apiKey + "&display=10&pageIndex=1&dataType=json";
        
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                null,
                Map.class
            );

            Map<String, Object> response = responseEntity.getBody();

            // API 응답 코드 확인
            if (response != null && "200".equals(String.valueOf(response.get("resultCode")))) {
                Map<String, Object> result = (Map<String, Object>) response.get("result");
                List<Map<String, String>> apiList = (List<Map<String, String>>) result.get("youthPolicyList");

                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

                for (Map<String, String> data : apiList) {
                    String bizId = data.get("plcyNo");

                    // 기존 정책 조회 또는 생성
                    PolicyEntity policy = policyRepository.findByBizId(bizId)
                            .orElseGet(() -> PolicyEntity.builder()
                                    .bizId(bizId)
                                    .viewCount(0)
                                    .build());

                    // 데이터 매핑
                    policy.setTitle(data.get("plcyNm"));
                    policy.setPolicyDescription(data.get("plcyExplnCn"));
                    policy.setSupportContent(data.get("plcySprtCn"));
                    policy.setDetailUrl(data.get("aplyUrlAddr"));
                    policy.setRegionCode(data.get("pvsnInstGroupCd"));
                    policy.setMinAge(parseInteger(data.get("sprtTrgtMinAge")));
                    policy.setMaxAge(parseInteger(data.get("sprtTrgtMaxAge")));

                    // 날짜 파싱
                    String aplyYmd = data.get("aplyYmd");
                    if (aplyYmd != null && aplyYmd.contains(" ~ ")) {
                        try {
                            String[] dates = aplyYmd.split(" ~ ");
                            policy.setApplyStartDate(LocalDate.parse(dates[0].trim(), fmt));
                            policy.setApplyEndDate(LocalDate.parse(dates[1].trim(), fmt));
                        } catch (Exception e) {
                            policy.setApplyStartDate(LocalDate.now());
                        }
                    }

                    policyRepository.save(policy);
                    log.info("정책 저장 완료: {}", policy.getTitle());
                }
            } else {
                log.error("API 응답 실패: {}", response != null ? response.get("resultMessage") : "응답 없음");
            }
        } catch (Exception e) {
            log.error("동기화 중 에러 발생!", e);
        }
    }


    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> searchPolicies(PolicySearchDTO search) {
        List<PolicyEntity> entities = policyRepository.findFilteredPolicies(
            search.getRegionCode(), search.getAge(), search.getJobCode(), 
            search.getEducationCode(), search.getMajorCode(), 
            search.getIncomeCode(), search.getSpecialCode()
        );

        return entities.stream().map(this::convertToResponseDTO).toList();
    }

    private PolicyResponseDTO convertToResponseDTO(PolicyEntity entity) {
        long dDay = 0;
        if (entity.getApplyEndDate() != null) {
            dDay = ChronoUnit.DAYS.between(LocalDate.now(), entity.getApplyEndDate());
        }

        return PolicyResponseDTO.builder()
            .policyId(entity.getPolicyId())
            .title(entity.getTitle())
            .policyDescription(entity.getPolicyDescription())
            .regionCode(entity.getRegionCode())
            .viewCount(entity.getViewCount())
            .applyEndDate(entity.getApplyEndDate())
            .detailUrl(entity.getDetailUrl())
            .dDay(dDay) 
            .build();
    }

    // String을 Integer로 안전하게 변환하는 헬퍼 메소드
    private Integer parseInteger(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    // 상세 조회
    @Transactional(readOnly = true)
    public PolicyResponseDTO getPolicyDetail(Integer id) {
        PolicyEntity entity = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책이 없습니다. id=" + id));
        
        // 이전에 만든 convertToResponseDTO 재사용 (단, 상세내용이 포함되도록 DTO 수정 필요할 수 있음)
        return convertToResponseDTO(entity);
    }

    // 조회수 증가
    @Transactional
    public void increaseViewCount(Integer id) {
        PolicyEntity entity = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책이 없습니다. id=" + id));
        entity.setViewCount(entity.getViewCount() + 1);
        // @Transactional이 있어서 자동으로 dirty checking되어 저장됨
    }
}