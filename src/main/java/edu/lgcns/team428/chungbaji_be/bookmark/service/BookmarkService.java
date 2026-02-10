package edu.lgcns.team428.chungbaji_be.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.lgcns.team428.chungbaji_be.bookmark.dao.BookmarkRepository;
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

        // 북마크 등록
        @Transactional
        public BookmarkResponseDTO register(String email, Integer policyId) {
                System.out.println("bookmark service register call");

                MemberEntity member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("member not found"));

                PolicyEntity policy = policyRepository.findById(policyId)
                                .orElseThrow(() -> new RuntimeException("policy not found"));

                // 상태 변경 또는 생성
                BookmarkEntity entity = bookmarkRepository.findByMemberAndPolicy(member, policy)
                                .map(b -> {
                                        if (b.getStatus() == BookmarkEntity.BookmarkStatus.DELETED)
                                                b.markCreated();
                                        return b;
                                })
                                .orElseGet(() -> bookmarkRepository
                                                .save(BookmarkEntity.builder().member(member).policy(policy).build()));

                return BookmarkResponseDTO.fromEntity(entity);
        }

        // 해제
        @Transactional
        public void delete(String email, Integer policyId) {
                System.out.println("bookmark service delete call");

                // 회원 존재 여부 확인
                MemberEntity member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("member not found: " + email));

                bookmarkRepository.findByMemberMemberIdAndPolicyPolicyId(
                                member.getMemberId(),
                                policyId).ifPresent(bookmark -> {
                                        if (bookmark.getStatus() != BookmarkEntity.BookmarkStatus.DELETED) {
                                                bookmark.markDeleted(); // 상태만 변경
                                        }
                                });

        }

        // 북마크한 정책만 리스트업
        @Transactional(readOnly = true)
        public List<BookmarkResponseDTO> listByMember(String email) {
                System.out.println("bookmark service listByMember call");

                // 회원 존재 확인
                MemberEntity member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("member not found"));

                // 해당 회원의 CREATED 북마크만 조회
                List<BookmarkEntity> bookmarks = bookmarkRepository.findAllByMemberAndStatus(member,
                                BookmarkEntity.BookmarkStatus.CREATED);

                // DTO 변환
                return bookmarks.stream()
                                .map(entity -> BookmarkResponseDTO.fromEntity(entity))
                                .toList();
        }
}
