package edu.lgcns.team428.chungbaji_be.policy.service;

import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
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
import java.time.Period;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${YOUTH_POLICY_API_URL}")
    private String apiUrl;

    @Value("${YOUTH_POLICY_API_KEY}")
    private String apiKey;

    @Transactional
    public void syncPolicies() {
        String fullUrl = apiUrl + 
                         "?apiKeyNm=" + apiKey + 
                         "&pageNum=1" + 
                         "&pageSize=10" + 
                         "&rtnType=json";
        
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(fullUrl, HttpMethod.GET, null, Map.class);
            Map<String, Object> response = responseEntity.getBody();
            log.info("API 응답: {}", response);

            if (response != null) {
                Object resultCodeObj = response.get("resultCode");
                String resultCode = String.valueOf(resultCodeObj);

                if ("200".equals(resultCode)) {
                    Map<String, Object> result = (Map<String, Object>) response.get("result");
                    List<Map<String, Object>> apiList = (List<Map<String, Object>>) result.get("youthPolicyList");

                    if (apiList != null) {
                        log.info("가져온 데이터 개수: {}", apiList.size());
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

                        for (Map<String, Object> data : apiList) {
                            String bizId = String.valueOf(data.get("plcyNo"));

                            // 1. 기존 정책 조회 또는 새 객체 생성 (중복 저장 방지)
                            PolicyEntity policy = policyRepository.findByBizId(bizId)
                                    .orElseGet(() -> PolicyEntity.builder()
                                            .bizId(bizId)
                                            .viewCount(0)
                                            .build());

                            // 2. 데이터 매핑 (API 응답 필드와 Entity 필드 연결)
                            policy.setTitle(String.valueOf(data.get("plcyNm")));
                            policy.setPolicyDescription(String.valueOf(data.get("plcyExplnCn")));
                            policy.setSupportContent(String.valueOf(data.get("plcySprtCn")));
                            
                            // policy.setCategoryMain(0);
                            // policy.setCategorySub(0);
                            // policy.setRegionCode(String.valueOf(data.get("zipCd")));
                            
                            // [임시 데이터 주입] -----------------------------------------
                            // category_main: lclsfNm 그룹의 '1'(일자리) 코드
                            policy.setCategoryMain(1); 
                            
                            // category_sub: mclsfNm 그룹의 '1'(취업) 코드
                            policy.setCategorySub(1); 
                            
                            // region_code: 01_insert_data.sql에 있는 '11000'(서울특별시)
                            policy.setRegionCode("11000"); 
                            // ---------------------------------------------------------
                                                    
                            
                            policy.setDetailUrl(String.valueOf(data.get("aplyUrlAddr")));
                            policy.setMinAge(parseInteger(String.valueOf(data.get("sprtTrgtMinAge"))));
                            policy.setMaxAge(parseInteger(String.valueOf(data.get("sprtTrgtMaxAge"))));

                            // 3. 날짜 처리 (aplyYmd 필드 파싱)
                            String aplyYmd = String.valueOf(data.get("aplyYmd"));
                            if (aplyYmd != null && aplyYmd.contains(" ~ ")) {
                                try {
                                    String[] dates = aplyYmd.split(" ~ ");
                                    policy.setApplyStartDate(LocalDate.parse(dates[0].trim(), fmt));
                                    policy.setApplyEndDate(LocalDate.parse(dates[1].trim(), fmt));
                                } catch (Exception e) {
                                    log.warn("날짜 파싱 실패: {}, bizId: {}", aplyYmd, bizId);
                                }
                            }

                            // 4. 최종 저장
                            policyRepository.save(policy);
                            log.info("정책 저장 완료: {}", policy.getTitle());
                        }
                    }
                } else {
                    log.error("API 응답 실패 코드: {}", resultCode);
                }
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
            .supportContent(entity.getSupportContent())
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
    @Transactional
    public PolicyResponseDTO getPolicyDetailAndIncreaseViewCount(Integer id) {
    PolicyEntity entity = policyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 정책이 없습니다. id=" + id));
    
    // 조회수 증가
    entity.setViewCount(entity.getViewCount() + 1);
    
    // DTO 변환 후 반환
    return convertToResponseDTO(entity);
}



    // 인기/임박 위젯 데이터 가져오기
    @Transactional(readOnly = true)
    public Map<String, List<PolicyResponseDTO>> getWidgetData() {
        List<PolicyResponseDTO> popular = policyRepository.findTop3ByOrderByViewCountDesc()
                .stream().map(this::convertToResponseDTO).toList();
                
        List<PolicyResponseDTO> urgent = policyRepository.findTop3ByApplyEndDateAfterOrderByApplyEndDateAsc(LocalDate.now())
                .stream().map(this::convertToResponseDTO).toList();

        return Map.of("popularPolicies", popular, "urgentPolicies", urgent);
    }

    // 해시태그 리스트 조회
    @Transactional(readOnly = true)
    public List<String> getAllKeywords() {
        return policyRepository.findAllDistinctKeywords();
    }

    // 맞춤형 추천 (로그인 유저의 정보를 DTO로 받아서 필터링 검색 로직 재활용!)
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getRecommendPolicies(String email) {
        // 1. 유저 정보 가져오기 (MemberRepository 주입 필요)
        MemberEntity member = memberRepository.findByEmail(email)                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. 유저 정보를 PolicySearchDTO로 변환
        // 각 엔티티(Region, Code)에서 실제 DB 검색에 필요한 ID/코드를 추출합니다.
        PolicySearchDTO searchDTO = PolicySearchDTO.builder()
                .regionCode(member.getRegion() != null ? member.getRegion().getRegionCode() : null)
                .age(calculateAge(member.getBirthDate())) 
                .jobCode(member.getJob() != null ? member.getJob().getCodeId() : 0)
                .educationCode(member.getEducation() != null ? member.getEducation().getCodeId() : 0)
                .majorCode(member.getMajor() != null ? member.getMajor().getCodeId() : 0)
                .incomeCode(member.getIncome() != null ? member.getIncome().getCodeId() : 0)
                .specialCode(member.getSpecial() != null ? member.getSpecial().getCodeId() : 0)
                .build();

        // 3. 기존에 만든 검색 로직 재활용!
        return searchPolicies(searchDTO);
    }

    // 헬퍼 메소드: 생년월일로 만 나이 계산
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
