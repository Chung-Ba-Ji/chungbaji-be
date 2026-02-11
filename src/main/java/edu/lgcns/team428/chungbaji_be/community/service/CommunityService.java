package edu.lgcns.team428.chungbaji_be.community.service;

import edu.lgcns.team428.chungbaji_be.code.domain.entity.CodeEntity;
import edu.lgcns.team428.chungbaji_be.code.repository.CodeRepository;
import edu.lgcns.team428.chungbaji_be.community.dao.CommentRepository;
import edu.lgcns.team428.chungbaji_be.community.dao.PostRepository;
import edu.lgcns.team428.chungbaji_be.community.domain.dto.CommentRequestDTO;
import edu.lgcns.team428.chungbaji_be.community.domain.dto.PostRequestDTO;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.CommentEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.member.dao.MemberRepository;
import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.dao.PolicyRepository;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PolicyRepository policyRepository;
    private final CodeRepository codeRepository;

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
    public PostEntity createPost(PostRequestDTO postRequest) {
        MemberEntity member = memberRepository.findById(postRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        PolicyEntity policy = policyRepository.findById(postRequest.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("policy not found"));
        CodeEntity code = codeRepository.findById(postRequest.getCodeId())
                .orElseThrow(() -> new IllegalArgumentException("code not found"));

        PostEntity post = postRequest.toEntity(member, policy, code);

        return postRepository.save(post);
    }

    // 게시글 수정
    @Transactional
    public PostEntity updatePost(Integer postId, PostRequestDTO postRequest) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        MemberEntity member = memberRepository.findById(postRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        PolicyEntity policy = policyRepository.findById(postRequest.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("policy not found"));
        CodeEntity code = codeRepository.findById(postRequest.getCodeId())
                .orElseThrow(() -> new IllegalArgumentException("code not found"));

        post.setMember(member);
        post.setPolicy(policy);
        post.setCode(code);
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setIsAnonymous(postRequest.getIsAnonymous());
        post.setStatus(postRequest.getStatus());

        return post;
    }

    // 게시글 삭제 (상태 변경 방식 또는 물리 삭제)
    @Transactional
    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    // 게시글 검색 (제목 또는 내용 키워드 기반)
    public Page<PostEntity> searchPosts(String keyword, Pageable pageable) {
        return null;
    }


    /**
     * 댓글 관련 로직
     */

    public Optional<CommentEntity> findCommentById(Integer commentId) {
        return commentRepository.findById(commentId);
    }

    public List<CommentEntity> findCommentsByPostId(Integer postId) {
        return commentRepository.findByPost_PostId(postId);
    }

    // 게시글 댓글 작성
    @Transactional
    public CommentEntity createComment(CommentRequestDTO commentRequest) {
        PostEntity post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        MemberEntity member = memberRepository.findById(commentRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        return commentRequest.toEntity(post, member);
    }

    // 게시글 댓글 수정
    @Transactional
    public CommentEntity updateComment(Integer commentId, CommentRequestDTO commentRequest) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));
        PostEntity post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        MemberEntity member = memberRepository.findById(commentRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("member not found"));

        comment.setPost(post);
        comment.setMember(member);
        comment.setContent(commentRequest.getContent());
        comment.setStatus(commentRequest.getStatus());

        return comment;
    }

    // 게시글 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
}
