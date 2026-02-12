package edu.lgcns.team428.chungbaji_be.community.ctrl;

import edu.lgcns.team428.chungbaji_be.community.domain.dto.CommentRequestDTO;
import edu.lgcns.team428.chungbaji_be.community.domain.dto.PostRequestDTO;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.CommentEntity;
import edu.lgcns.team428.chungbaji_be.community.domain.entity.PostEntity;
import edu.lgcns.team428.chungbaji_be.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // --- Post Endpoints ---

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<PostEntity>> getAllPosts(@PageableDefault() Pageable pageable) {
        return ResponseEntity.ok(communityService.findAllPosts(pageable));
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostEntity> getPostById(@PathVariable Integer postId) {
        return communityService.findPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<PostEntity> createPost(@RequestBody PostRequestDTO postRequest) {
        System.out.println(postRequest);

        return ResponseEntity.ok(communityService.createPost(postRequest));
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostEntity> updatePost(@PathVariable Integer postId, @RequestBody PostRequestDTO postRequest) {
        try {
            return ResponseEntity.ok(communityService.updatePost(postId, postRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        communityService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    // 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<Page<PostEntity>> searchPosts(@RequestParam String keyword, @PageableDefault() Pageable pageable) {
        return ResponseEntity.ok(communityService.searchPosts(keyword, pageable));
    }

    // --- Comment Endpoints ---

    // 특정 게시글의 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentEntity>> getCommentsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(communityService.findCommentsByPostId(postId));
    }

    // 댓글 작성
    @PostMapping("/comments")
    public ResponseEntity<CommentEntity> createComment(@RequestBody CommentRequestDTO commentRequest) {
        try {
            return ResponseEntity.ok(communityService.createComment(commentRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentEntity> updateComment(@PathVariable Integer commentId, @RequestBody CommentRequestDTO commentRequest) {
        try {
            return ResponseEntity.ok(communityService.updateComment(commentId, commentRequest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        communityService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
