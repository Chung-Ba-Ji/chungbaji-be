package edu.lgcns.team428.chungbaji_be.community.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "post")
@DynamicInsert // insert 시 null인 필드 제외 (DB의 Default 값 활용)
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간 자동 기록
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private Code categoryCode;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob // TEXT 타입 대응
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 1)
    @ColumnDefault("'N'")
    private String isAnonymous;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'CREATED'")
    private String status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
