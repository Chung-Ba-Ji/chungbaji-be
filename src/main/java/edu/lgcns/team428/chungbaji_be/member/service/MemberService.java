package edu.lgcns.team428.chungbaji_be.member.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final CodeRepository codeRepository;

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    private static final String CG_EDUCATION = "0049";
    private static final String CG_JOB = "0013";
    private static final String CG_MAJOR = "0011";
    private static final String CG_INCOME = "0043";
    private static final String CG_SPECIAL = "0014";

    // 회원가입
    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO request) {
        System.out.println("member service signUp call");

        // 1) 필수값 검증(null 처리)
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new IllegalArgumentException("email은 필수입니다.");
        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new IllegalArgumentException("password는 필수입니다.");
        if (request.getNickname() == null || request.getNickname().isBlank())
            throw new IllegalArgumentException("nickname은 필수입니다.");
        if (request.getPhone_num() == null || request.getPhone_num().isBlank())
            throw new IllegalArgumentException("phone_num은 필수입니다.");
        if (request.getGender() == null || request.getGender().isBlank())
            throw new IllegalArgumentException("gender는 필수입니다.");
        if (request.getRegionSido() == null || request.getRegionSido().isBlank())
            throw new IllegalArgumentException("regionSido(시/도)는 필수입니다.");
        if (request.getRegionSigungu() == null || request.getRegionSigungu().isBlank())
            throw new IllegalArgumentException("regionSigungu(시/군/구)는 필수입니다.");

        // 지역 변환 (DB가 '서울특별시 마포구' 형태도 가지고 있으므로 둘 다 허용)
        RegionEntity region = findRegionByName(request.getRegionSido(), request.getRegionSigungu());

        // 코드 변환 (프론트가 문자열값(= code_desc)를 보낸다는 가정)
        CodeEntity education = findCodeByDescOrNull(CG_EDUCATION, request.getEducation());
        CodeEntity job = findCodeByDescOrNull(CG_JOB, request.getJob());
        CodeEntity major = findCodeByDescOrNull(CG_MAJOR, request.getMajor());
        CodeEntity income = findCodeByDescOrNull(CG_INCOME, request.getIncome());
        CodeEntity special = findCodeByDescOrNull(CG_SPECIAL, request.getSpecial());

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 이메일 기준 회원 처리
        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .map(existing -> {

                    // WITHDRAWN: 탈퇴 회원 재가입 금지
                    if (existing.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
                        throw new IllegalStateException("탈퇴한 회원은 재가입할 수 없습니다.");
                    }

                    // INACTIVE: 비활성 회원이면 재활성화
                    if (existing.getStatus() == MemberEntity.MemberStatus.INACTIVE) {

                        // 닉네임 변경 시에만 중복 체크
                        if (!existing.getNickname().equals(request.getNickname())
                                && memberRepository.existsByNickname(request.getNickname())) {
                            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
                        }

                        existing.reactivate(
                                encodedPassword,
                                request.getNickname(),
                                request.getPhone_num(),
                                request.getGender(),
                                request.getBirth_date(),
                                region,
                                education,
                                job,
                                major,
                                income,
                                special);
                        return existing;
                    }

                    // ACTIVE: 가입 불가
                    throw new IllegalStateException("이미 가입된 이메일입니다.");
                })
                .orElseGet(() -> {
                    // 신규 가입일 때만 닉네임 중복 체크
                    if (memberRepository.existsByNickname(request.getNickname()))
                        throw new IllegalStateException("이미 사용 중인 닉네임입니다.");

                    return MemberEntity.builder()
                            .email(request.getEmail())
                            .password(encodedPassword)
                            .nickname(request.getNickname())
                            .phoneNum(request.getPhone_num())
                            .gender(request.getGender())
                            .birthDate(request.getBirth_date())
                            .region(region)
                            .education(education)
                            .job(job)
                            .major(major)
                            .income(income)
                            .special(special)
                            .status(MemberEntity.MemberStatus.ACTIVE)
                            .build();
                });

        // DTO 반환
        MemberEntity saved = memberRepository.save(member);
        return MemberResponseDTO.fromEntity(saved);
    }

    private RegionEntity findRegionByName(String level1NameRaw, String level2NameRaw) {

        String level1Name = level1NameRaw.trim();
        String level2Name = level2NameRaw.trim();

        RegionEntity level1 = regionRepository
                .findByLevelAndRegionName(1, level1Name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 시도(레벨1) 지역입니다: [" + level1NameRaw + "]"));

        String fullLevel2Name = level1Name + " " + level2Name;

        return regionRepository
                .findByLevelAndParentRegion_RegionCodeAndRegionName(2, level1.getRegionCode(), level2Name)
                .or(() -> regionRepository.findByLevelAndParentRegion_RegionCodeAndRegionName(
                        2, level1.getRegionCode(), fullLevel2Name))
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 시군구(레벨2) 지역입니다: [" + level1NameRaw + " " + level2NameRaw + "]"));
    }

    // code_desc 기준 조회 (프론트가 "고교 졸업", "재직자" 등 desc를 보낸다는 가정)
    private CodeEntity findCodeByDescOrNull(String codeGroup, String codeDesc) {
        if (codeDesc == null || codeDesc.isBlank())
            return null;

        return codeRepository.findByCodeGroupAndCodeDesc(codeGroup, codeDesc)
                .orElseThrow(() -> new IllegalArgumentException(
                        "유효하지 않은 코드입니다. group=" + codeGroup + ", desc=" + codeDesc));
    }

    // 회원정보 수정
    @Transactional
    public MemberResponseDTO update(String email, MemberRequestDTO request) {
        System.out.println("member service update call");

        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("member not found"));

        // 닉네임 중복 체크
        String newNickname = trimToNull(request.getNickname());
        if (newNickname != null && !newNickname.equals(entity.getNickname())
                && memberRepository.existsByNickname(newNickname)) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        // region은 (sido, sigungu) 둘 다 들어온 경우에만 변경
        RegionEntity newRegion = null;
        String sido = trimToNull(request.getRegionSido());
        String sigungu = trimToNull(request.getRegionSigungu());
        if (sido != null || sigungu != null) {
            if (sido == null || sigungu == null) {
                throw new IllegalArgumentException("지역 수정은 regionSido/regionSigungu 둘 다 필요합니다.");
            }
            newRegion = findRegionByName(sido, sigungu);
        }

        CodeEntity education = findCodeByDescOrNull(CG_EDUCATION, request.getEducation());
        CodeEntity job = findCodeByDescOrNull(CG_JOB, request.getJob());
        CodeEntity major = findCodeByDescOrNull(CG_MAJOR, request.getMajor());
        CodeEntity income = findCodeByDescOrNull(CG_INCOME, request.getIncome());
        CodeEntity special = findCodeByDescOrNull(CG_SPECIAL, request.getSpecial());

        // null이면 기존정보 유지
        entity.updateProfilePatch(
                newNickname,
                trimToNull(request.getPhone_num()),
                trimToNull(request.getGender()),
                request.getBirth_date(),
                newRegion, 
                education, job, major, income, special 
        );

        return MemberResponseDTO.fromEntity(entity);
    }

    private String trimToNull(String s) {
        if (s == null)
            return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // 비번 찾기: 인증 성공 시 임시 비밀번호 발급 후 해싱 저장
    @Transactional
    public String searchPwd(String email, String phone_num) {
        System.err.println("member service searchPwd");

        MemberEntity entity = memberRepository.findByEmailAndPhoneNum(email, phone_num)
                .orElseThrow(() -> new RuntimeException("member not found"));

        // 임시 비번 발급
        String tempPassword = generateTempPassword(12);

        // 해싱해서 DB에 저장
        String encoded = passwordEncoder.encode(tempPassword);

        entity.updatePwd(encoded);

        return tempPassword;

    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(String email) {
        System.out.println("member service withdraw call");

        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("member not found"));

        // 이미 탈퇴한 회원인 경우
        if (entity.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
            return;
        }

        // 회원의 상태 정보 WITHDRAWN으로 변경(dirty checking)
        entity.withdraw();
        refreshTokenService.deleteToken(email);
    }

    // 로그인
    @Transactional
    public Map<String, Object> login(LoginRequestDTO request) {
        System.out.println("member service login call");

        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

        // 상태 체크 (활성화된 계정인 경우에만 로그인 가능)
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

        // redis에 refresh-token 저장
        refreshTokenService.saveToken(member.getEmail(), rt);

        // 발급한 토큰과 사용자정보 반환
        map.put("response", MemberResponseDTO.fromEntity(member));
        map.put("access-token", at);
        map.put("refresh-token", rt);

        return map;

    }

    // 로그아웃(= 토큰 관련 로직 추가)
    @Transactional
    public void logout(String accessToken) {
        System.out.println("member service logout call");

        String email = jwtProvider.getUserEmailFromToken(accessToken);
        refreshTokenService.deleteToken(email);
    }

    // 임시 비번 생성
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
