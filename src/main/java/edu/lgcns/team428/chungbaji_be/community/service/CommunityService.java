package edu.lgcns.team428.chungbaji_be.community.service;

import edu.lgcns.team428.chungbaji_be.community.dao.CommentRepository;
import edu.lgcns.team428.chungbaji_be.community.dao.PostRepository;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.CommentEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     *  게시글 관련 로직
     */

    // 게시글 전체 조회 (페이징 적용)
    public Page<PostEntity> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // 게시글 상세 조회
    public Optional<PostEntity> findPostById(Integer postId) {
        return postRepository.findById(postId);
    }

    // 게시글 작성
    @Transactional
    public PostEntity createPost(PostEntity post, Integer memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        return null;
    }

    // 게시글 수정
    @Transactional
    public PostEntity updatePost(Integer postId, PostEntity updateInfo) {
        return null;
    }

    // 게시글 삭제 (상태 변경 방식 또는 물리 삭제)
    @Transactional
    public void deletePost(Integer postId) { }

    // 게시글 검색 (제목 또는 내용 키워드 기반)
    public Page<PostEntity> searchPosts(String keyword, Pageable pageable) { return null; }


    /**
     * 댓글 관련 로직
     */

    // 7. 게시글 댓글 작성
    @Transactional
    public CommentEntity createComment(Integer postId, Integer memberId, CommentEntity comment) { return null; }

    // 8. 게시글 댓글 수정
    @Transactional
    public CommentEntity updateComment(Integer commentId, String content) { return null; }

    // 9. 게시글 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId) { }
}
