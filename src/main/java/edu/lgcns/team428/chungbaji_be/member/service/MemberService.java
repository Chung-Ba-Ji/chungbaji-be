package edu.lgcns.team428.chungbaji_be.member.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.code.repository.CodeRepository;
import edu.lgcns.team428.chungbaji_be.common.service.RefreshTokenService;
import edu.lgcns.team428.chungbaji_be.common.util.JwtProvider;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.LoginRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberResponseDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.region.domain.entity.RegionEntity;
import edu.lgcns.team428.chungbaji_be.region.repository.RegionRepository;
import edu.lgcns.team428.chungbaji_be.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final ScheduleService scheduleService;
    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final CodeRepository codeRepository;

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    // 공통 코드 그룹
    private static final String CG_EDUCATION = "0049";
    private static final String CG_JOB = "0013";
    private static final String CG_MAJOR = "0011";
    private static final String CG_INCOME = "0043";
    private static final String CG_SPECIAL = "0014";

    /**
     * 회원가입
     */
    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO request) {
        log.info("Member SignUp Call - Parameter: {}", request.toString());

        validateSignUpRequest(request);

        // 지역 변환: 시군구 코드로 직접 조회
        RegionEntity region = regionRepository.findByRegionCode(request.getRegionSigungu())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역 코드입니다: " + request.getRegionSigungu()));

        // 코드 변환: DTO의 수정된 필드명(educationCode 등) 사용
        CodeEntity education = findCodeByCodeOrNull(CG_EDUCATION, request.getEducationCode());
        CodeEntity job = findCodeByCodeOrNull(CG_JOB, request.getJobCode());
        CodeEntity major = findCodeByCodeOrNull(CG_MAJOR, request.getMajorCode());
        CodeEntity income = findCodeByCodeOrNull(CG_INCOME, request.getIncomeCode());
        CodeEntity special = findCodeByCodeOrNull(CG_SPECIAL, request.getSpecialCode());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .map(existing -> {
                    if (existing.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
                        throw new IllegalStateException("탈퇴한 회원은 재가입할 수 없습니다.");
                    }
                    if (existing.getStatus() == MemberEntity.MemberStatus.INACTIVE) {
                        checkNicknameDuplicate(existing.getNickname(), request.getNickname());
                        existing.reactivate(encodedPassword, request.getNickname(), request.getPhoneNum(),
                                request.getGender(), request.getBirthDate(), region,
                                education, job, major, income, special);
                        return existing;
                    }
                    throw new IllegalStateException("이미 가입된 이메일입니다.");
                })
                .orElseGet(() -> {
                    checkNicknameDuplicate(null, request.getNickname());
                    return MemberEntity.builder()
                            .email(request.getEmail())
                            .password(encodedPassword)
                            .nickname(request.getNickname())
                            .phoneNum(request.getPhoneNum())
                            .gender(request.getGender())
                            .birthDate(request.getBirthDate())
                            .region(region)
                            .education(education)
                            .job(job)
                            .major(major)
                            .income(income)
                            .special(special)
                            .status(MemberEntity.MemberStatus.ACTIVE)
                            .build();
                });

        return MemberResponseDTO.fromEntity(memberRepository.save(member));
    }

    /**
     * 로그인
     */
    @Transactional
    public Map<String, Object> login(LoginRequestDTO request) {
        log.info("Member Login Call - Email: {}", request.getEmail());

        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 상태 체크
        if (member.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
            throw new RuntimeException("탈퇴한 회원은 로그인할 수 없습니다.");
        }
        if (member.getStatus() == MemberEntity.MemberStatus.INACTIVE) {
            throw new RuntimeException("비활성화된 계정입니다.");
        }

        Map<String, Object> map = new HashMap<>();

        // 토큰 생성
        String at = jwtProvider.createAT(member.getEmail());
        String rt = jwtProvider.createRT(member.getEmail());

        // Redis/DB에 Refresh Token 저장
        refreshTokenService.saveToken(member.getEmail(), rt);

        map.put("response", MemberResponseDTO.fromEntity(member));
        map.put("access-token", at);
        map.put("refresh-token", rt);

        return map;
    }

    /**
     * 회원정보 수정
     */
    @Transactional
    public MemberResponseDTO update(String email, MemberRequestDTO request) {
        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        checkNicknameDuplicate(entity.getNickname(), request.getNickname());

        RegionEntity newRegion = null;
        if (!isEmpty(request.getRegionSigungu())) {
            newRegion = regionRepository.findByRegionCode(request.getRegionSigungu())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 지역 코드입니다."));
        }

        CodeEntity education = findCodeByCodeOrNull(CG_EDUCATION, request.getEducationCode());
        CodeEntity job = findCodeByCodeOrNull(CG_JOB, request.getJobCode());
        CodeEntity major = findCodeByCodeOrNull(CG_MAJOR, request.getMajorCode());
        CodeEntity income = findCodeByCodeOrNull(CG_INCOME, request.getIncomeCode());
        CodeEntity special = findCodeByCodeOrNull(CG_SPECIAL, request.getSpecialCode());

        entity.updateProfilePatch(
                request.getNickname(), request.getPhoneNum(), request.getGender(),
                request.getBirthDate(), newRegion, education, job, major, income, special
        );

        return MemberResponseDTO.fromEntity(entity);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String accessToken) {
        String email = jwtProvider.getUserEmailFromToken(accessToken);
        refreshTokenService.deleteToken(email);
    }

    /**
     * 비밀번호 찾기 (임시 비밀번호 발급)
     */
    @Transactional
    public String searchPwd(String email, String phoneNum) {
        MemberEntity entity = memberRepository.findByEmailAndPhoneNum(email, phoneNum)
                .orElseThrow(() -> new RuntimeException("일치하는 회원 정보가 없습니다."));

        String tempPassword = generateTempPassword(12);
        entity.updatePwd(passwordEncoder.encode(tempPassword));

        return tempPassword;
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdraw(String email) {
        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (entity.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) return;

        scheduleService.deleteAllSchedulesByMember(entity);
        entity.withdraw();
        refreshTokenService.deleteToken(email);
    }

    // --- Helper Methods ---

    private CodeEntity findCodeByCodeOrNull(String codeGroup, String code) {
        if (isEmpty(code)) return null;
        return codeRepository.findByCodeGroupAndCode(codeGroup, code)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 코드: " + codeGroup + ":" + code));
    }

    private void validateSignUpRequest(MemberRequestDTO request) {
        if (isEmpty(request.getEmail())) throw new IllegalArgumentException("email은 필수입니다.");
        if (isEmpty(request.getPassword())) throw new IllegalArgumentException("password는 필수입니다.");
        if (isEmpty(request.getNickname())) throw new IllegalArgumentException("nickname은 필수입니다.");
        if (isEmpty(request.getPhoneNum())) throw new IllegalArgumentException("phone_num은 필수입니다.");
        if (isEmpty(request.getGender())) throw new IllegalArgumentException("gender는 필수입니다.");
        if (isEmpty(request.getRegionSigungu())) throw new IllegalArgumentException("지역 정보는 필수입니다.");
    }

    private boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    private void checkNicknameDuplicate(String current, String target) {
        if (target != null && !target.equals(current) && memberRepository.existsByNickname(target)) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }
    }

    private static final String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateTempPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}