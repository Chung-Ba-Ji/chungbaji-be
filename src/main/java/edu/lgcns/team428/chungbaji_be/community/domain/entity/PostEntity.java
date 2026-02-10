package edu.lgcns.team428.chungbaji_be.community.domain.entity;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;
import edu.lgcns.team428.chungbaji_be.policy.domain.entity.PolicyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post")
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 기록
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private CodeEntity categoryCode;

    @Column(nullable = false)
    private String title;

    @Lob // TEXT 타입 대응
    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(nullable = false, length = 1)
    private String isAnonymous = "N";

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "CREATED";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}