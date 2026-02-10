package edu.lgcns.team428.chungbaji_be.code.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "code", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "code_group", "code" })
})
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "code_id")
        private Integer codeId;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "code_group_id", referencedColumnName = "code_group_id", nullable = false)
        private CodeGroupEntity codeGroup;

        @Column(name = "code", length = 50, nullable = false)
        private String code;

        @Column(name = "code_desc", length = 50)
        private String codeDesc;
}
