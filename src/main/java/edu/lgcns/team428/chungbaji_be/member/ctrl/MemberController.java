package edu.lgcns.team428.chungbaji_be.member.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberResponseDTO;
import edu.lgcns.team428.chungbaji_be.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/member")
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

    // 회원정보 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<MemberResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody MemberRequestDTO request) {
        System.out.println("member controller update call");

        MemberResponseDTO updated = memberService.update(id, request);

        return ResponseEntity.ok(updated);
    }

    // 비번 찾기
    @PostMapping("/searchPwd")
    public ResponseEntity<String> searchPwd(@RequestBody String email, @RequestBody String phone_num) {
        System.out.println("member controller searchPwd call");
        
        String password = memberService.searchPwd(email, phone_num);
        return ResponseEntity.status(HttpStatus.OK).body(password);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        System.out.println("member controller delete call");
        memberService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }

}
