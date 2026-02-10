package edu.lgcns.team428.chungbaji_be.member.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberResponseDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.SearchPwdRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signUp")
    public ResponseEntity<MemberResponseDTO> signUp(@RequestBody MemberRequestDTO request) {
        System.out.println("member controller signUp call");

        MemberResponseDTO created = memberService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<MemberResponseDTO> login(@RequestBody MemberRequestDTO request) {
        System.out.println("member controller login call");

        Map<String, Object> map = memberService.login(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + map.get("access-token"));
        headers.add("Refresh-token", (String) (map.get("refresh-token")));
        // 응답헤더 명시적 허용 
        headers.add("Access-Control-Expose-Headers", "Authorization,Refresh-token");

        System.out.println("access token value : " + headers.get("Authorization"));

        if (map.size() != 0) {
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body((MemberResponseDTO) map.get("response"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(null);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
        System.out.println("member controller login call");

        // 실제 토큰값만 획득
        String accessToken = authorization.replace("Bearer ", "");

        memberService.logout(accessToken);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    // 회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<MemberResponseDTO> update(@RequestBody MemberRequestDTO request) {
        System.out.println("member controller update call");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        MemberResponseDTO updated = memberService.update(email, request);

        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    // 비번 찾기
    @PostMapping("/searchPwd")
    public ResponseEntity<String> searchPwd(@RequestBody SearchPwdRequestDTO request) {
        System.out.println("member controller searchPwd call");

        String password = memberService.searchPwd(request.getEmail(), request.getPhone_num());

        return ResponseEntity.status(HttpStatus.OK).body(password);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete() {
        System.out.println("member controller delete call");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        memberService.deleteByEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
