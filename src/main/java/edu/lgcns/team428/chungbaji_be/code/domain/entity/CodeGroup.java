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
public class CodeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codeGroupId;

    @Column(length = 50, nullable = false, unique = true)
    private String codeGroup;

    @Column(length = 50)
    private String codeGroupDesc;

}
