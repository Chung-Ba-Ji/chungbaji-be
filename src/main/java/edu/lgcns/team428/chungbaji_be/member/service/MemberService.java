package edu.lgcns.team428.chungbaji_be.member.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.lgcns.team428.chungbaji_be.common.service.RefreshTokenService;
import edu.lgcns.team428.chungbaji_be.common.util.JwtProvider;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberResponseDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO request) {
        System.out.println("member service signUp call");

        // 이메일 중복이 되면 안됨 !!
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("already exist");
        }

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        MemberEntity entity = memberRepository.findByEmail(request.getEmail())
                .map(existing -> {
                    // 이메일이 있는데 status=ACTIVE → 중복가입 에러
                    if (existing.getStatus() == MemberEntity.MemberStatus.ACTIVE) {
                        throw new RuntimeException("already exist");
                    }

                    // 2) 이메일이 있는데 status=INACTIVE → 재활성화(상태 ACTIVE + 정보 갱신)
                    existing.reactivate(request, encodedPassword);
                    return existing;
                })
                .orElseGet(() -> memberRepository.save(MemberEntity.from(request, encodedPassword)));

        return MemberResponseDTO.fromEntity(entity);
    }

    // 회원정보 수정 - code/region 엔티티 완성 후 수정 필요
    @Transactional
    public MemberResponseDTO update(String email, MemberRequestDTO request) {
        System.out.println("member service update call");

        MemberEntity entity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("cannot find"));

        return null;
    }

    // 비번 찾기: 인증 성공 시 임시 비밀번호 발급 후 해싱 저장
    @Transactional
    public String searchPwd(String email, String phone_num) {
        System.err.println("member service searchPwd");

        MemberEntity entity = memberRepository.findByEmailAndPhoneNum(email, phone_num)
                .orElseThrow(() -> new RuntimeException("cannot find"));

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

}
