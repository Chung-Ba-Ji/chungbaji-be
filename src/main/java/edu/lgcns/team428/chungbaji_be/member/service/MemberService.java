package edu.lgcns.team428.chungbaji_be.member.service;

import org.springframework.stereotype.Service;

import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberRequestDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.dto.MemberResponseDTO;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository ;

    // 회원가입
    public MemberResponseDTO signUp(MemberRequestDTO request){
        System.out.println("member service signUp call");

        MemberEntity entity = memberRepository.save(MemberRequestDTO.toEntity(request));

        return MemberResponseDTO.fromEntity(entity) ;
    }
    
    // 회원정보 수정 
    public MemberResponseDTO update(Integer id, MemberRequestDTO request){
        System.out.println("member service update call");
        MemberEntity entity = memberRepository.findById(id).orElseThrow(()->
           new RuntimeException("cannot find"));

        
        return null;
    }

    // 비번 찾기
    public String searchPwd(String email, String phone_num){
        System.err.println("member service searchPwd");

        MemberEntity entity = memberRepository.findByEmailAndPhoneNum(email, phone_num).orElseThrow(() ->
                    new RuntimeException("cannot find"));

        String password = entity.getPassword();

        return password ;

    }

    // 회원 탈퇴
    public void delete(Integer id){
        System.out.println("member service delete call");

        MemberEntity entity = memberRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("cannot find"));
        
        memberRepository.delete(entity);
    }
    
}
