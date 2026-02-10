package edu.lgcns.team428.chungbaji_be.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.lgcns.team428.chungbaji_be.bookmark.dao.BookmarkRepository;
import edu.lgcns.team428.chungbaji_be.bookmark.domain.dto.BookmarkRequestDTO;
import edu.lgcns.team428.chungbaji_be.bookmark.domain.dto.BookmarkResponseDTO;
import edu.lgcns.team428.chungbaji_be.bookmark.domain.entity.BookmarkEntity;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.dao.PolicyRepository;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PolicyRepository policyRepository;

    // 저장
    @Transactional
    public BookmarkResponseDTO register(BookmarkRequestDTO request) {
        System.out.println("bookmark service post call");

        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("cannot find"));

        PolicyEntity policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("cannot find"));

        

        BookmarkEntity entity = bookmarkRepository.save(request.toEntity(member, policy));

        return BookmarkResponseDTO.fromEntity(entity);
    }

    // 해제
    @Transactional
    public void delete(BookmarkRequestDTO request) {
        System.out.println("bookmark service delete call");

        MemberEntity member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("cannot find member"));

        PolicyEntity policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new RuntimeException("cannot find policy"));

        BookmarkEntity entity = bookmarkRepository
                .findByMemberAndPolicy(member, policy)
                .orElseThrow(() -> new RuntimeException("bookmark not found"));

        bookmarkRepository.delete(entity);

    }

    // 북마크한 정책만 리스트업
    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> listByMember(Integer memberId) {
        System.out.println("bookmark service listByMember call");

        // 회원 존재 확인
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("cannot find member "));

        // 해당 회원의 '활성(CREATED)' 북마크만 조회
        List<BookmarkEntity> bookmarks = bookmarkRepository.findByMemberAndStatus(member,
                BookmarkEntity.BookmarkStatus.CREATED).orElseThrow(() -> new RuntimeException("cannot find policies"));

        // DTO 변환
        return bookmarks.stream()
                .map(entity -> BookmarkResponseDTO.fromEntity(entity))
                .toList();
    }
}
