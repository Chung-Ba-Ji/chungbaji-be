package edu.lgcns.team428.chungbaji_be.member.service;

import java.util.HashMap;
import java.util.Map;

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

    // 회원가입
    @Transactional
    public MemberResponseDTO signUp(MemberRequestDTO request) {
        System.out.println("member service signUp call");

        // 이메일 중복이 되면 안됨 !!
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("already exist");
        }

        MemberEntity entity = memberRepository.save(request.toEntity());

        return MemberResponseDTO.fromEntity(entity);
    }

    // 회원정보 수정 - code/region 엔티티 완성 후 수정
    @Transactional
    public MemberResponseDTO update(Integer id, MemberRequestDTO request) {
        System.out.println("member service update call");

        MemberEntity entity = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("cannot find"));

        return null;
    }

    // 비번 찾기
    @Transactional
    public String searchPwd(String email, String phone_num) {
        System.err.println("member service searchPwd");

        MemberEntity entity = memberRepository.findByEmailAndPhoneNum(email, phone_num)
                .orElseThrow(() -> new RuntimeException("cannot find"));

        String password = entity.getPassword();

        return password;

    }

    // 회원 탈퇴
    @Transactional
    public void delete(Integer id) {
        System.out.println("member service delete call");

        MemberEntity entity = memberRepository.findById(id).orElseThrow(() -> new RuntimeException("cannot find"));

        memberRepository.delete(entity);
    }

    // 로그인(토큰 관련 로직 추가)
    @Transactional
    public Map<String, Object> login(MemberRequestDTO request) {
        System.out.println("member service login call");

        MemberEntity member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

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

}
