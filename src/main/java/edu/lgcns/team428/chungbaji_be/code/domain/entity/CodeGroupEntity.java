package edu.lgcns.team428.chungbaji_be.code.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code_group")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodeGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_group_id")
    private Integer codeGroupId;

    @Column(name = "code_group", length = 50, nullable = false, unique = true)
    private String codeGroup;

    @Column(name = "code_group_desc", length = 50)
    private String codeGroupDesc;

}
