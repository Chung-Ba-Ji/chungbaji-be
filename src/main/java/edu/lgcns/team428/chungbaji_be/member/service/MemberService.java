package edu.lgcns.team428.chungbaji_be.member.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.code.repository.CodeRepository;
import edu.lgcns.team428.chungbaji_be.common.service.RefreshTokenService;
import edu.lgcns.team428.chungbaji_be.common.util.JwtProvider;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
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

    // 회원가입
    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO request) {
        System.out.println("member service signUp call");

        // 문자열 → 엔티티(코드) 변환
        RegionEntity region = findRegionByName(request.getRegion());

        CodeEntity education = findCodeByDesc("EDUCATION", request.getEducation());
        CodeEntity job = findCodeByDesc("JOB", request.getJob());
        CodeEntity major = findCodeByDesc("MAJOR", request.getMajor());
        CodeEntity income = findCodeByDesc("INCOME", request.getIncome());
        CodeEntity special = findCodeByDesc("SPECIAL", request.getSpecial());

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 이메일 기준 회원 존재 여부 확인
        Optional<MemberEntity> optional = memberRepository.findByEmail(request.getEmail());

        // DB에 존재하지 않는 회원인 경우 save
        if (optional.isEmpty()) {
            MemberEntity member = MemberEntity.builder()
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

            MemberEntity saved = memberRepository.save(member);
            return MemberResponseDTO.fromEntity(saved);
        }

        // 회원 정보가 존재하는 경우
        MemberEntity existing = optional.get();

        // 탈퇴 회원(WITHDRAWN)은 재가입 불가
        if (existing.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("탈퇴한 회원은 재가입할 수 없습니다.");
        }

        // 비활성 회원은 재활성화(INACTIVE -> ACTIVE)
        if (existing.getStatus() == MemberEntity.MemberStatus.INACTIVE) {
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
            return MemberResponseDTO.fromEntity(existing);
        }

        // ACTIVE 인데 또 가입 시도
        throw new IllegalStateException("이미 가입된 회원입니다.");
    }

    // 회원정보 수정
    @Transactional
    public MemberResponseDTO update(String email, MemberRequestDTO request) {
        System.out.println("member service update call");

        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("member not found"));

        // 문자열 → RegionEntity
        RegionEntity region = findRegionOrNull(request.getRegion());

        // 문자열 → CodeEntity (codeDesc 기준)
        CodeEntity education = findCodeOrNull("EDUCATION", request.getEducation());
        CodeEntity job = findCodeOrNull("JOB", request.getJob());
        CodeEntity major = findCodeOrNull("MAJOR", request.getMajor());
        CodeEntity income = findCodeOrNull("INCOME", request.getIncome());
        CodeEntity special = findCodeOrNull("SPECIAL", request.getSpecial());

        // 엔티티 값 갱신 (자동 UPDATE - JPA 영속성)
        entity.updateProfile(
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

        return MemberResponseDTO.fromEntity(entity);
    }

    private RegionEntity findRegionOrNull(String regionName) {
        if (regionName == null || regionName.isBlank())
            return null;

        return regionRepository.findByRegionName(regionName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다: " + regionName));
    }

    private CodeEntity findCodeOrNull(String group, String desc) {
        if (desc == null || desc.isBlank())
            return null;

        return codeRepository.findByCodeGroup_CodeGroupAndCodeDesc(group, desc)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 코드입니다. group=" + group + ", desc=" + desc));
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
    public void deleteByEmail(String email) {
        System.out.println("member service delete call");

        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("member not found"));

        // 이미 탈퇴한 회원인 경우
        if (entity.getStatus() == MemberEntity.MemberStatus.WITHDRAWN) {
            return;
        }

        // 회원의 상태 정보 WITHDRAWN으로 변경 
        entity.withdraw(); // JPA의 영속성
    }

    // 로그인
    @Transactional
    public Map<String, Object> login(MemberRequestDTO request) {
        System.out.println("member service login call");

        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("Password not found");
        }

        Map<String, Object> map = new HashMap<>();

        // 토큰 생성
        String at = jwtProvider.createAT(member.getEmail());
        String rt = jwtProvider.CreateRT(member.getEmail());

        // redis에 refresh-token 저장
        refreshTokenService.saveToken(member.getEmail(), rt);

        // 발급한 토큰과 사용자정보 반환
        map.put("response", MemberResponseDTO.fromEntity(member));
        map.put("access-token", at);
        map.put("refresh-token", rt);

        return map;

    }

    // 로그아웃(토큰 관련 로직 추가)
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

    // region 문자열 -> 코드(int)
    private RegionEntity findRegionByName(String regionName) {
        if (regionName == null || regionName.isBlank())
            return null;

        return regionRepository.findByRegionName(regionName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다: " + regionName));
    }

    // code 문자열 -> 코드(int)
    private CodeEntity findCodeByDesc(String group, String desc) {
        if (desc == null || desc.isBlank())
            return null;

        return codeRepository.findByCodeGroup_CodeGroupAndCodeDesc(group, desc)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 코드입니다. group=" + group + ", desc=" + desc));
    }

}
