-- 1. 지역 테이블

CREATE TABLE region (
    region_id           INT          PRIMARY KEY    AUTO_INCREMENT,
    region_code         CHAR(5)      NOT NULL       UNIQUE,
    region_name         VARCHAR(20),
    level               INT,
    parent_region_code  CHAR(5),

    CONSTRAINT
        CHECK (level IS NULL OR level >= 0),

    CONSTRAINT
        FOREIGN KEY (parent_region_code)
            REFERENCES region(region_code)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);



-- 2. 코드 관련 테이블

CREATE TABLE code_group (
    code_group_id     INT          PRIMARY KEY  AUTO_INCREMENT,
    code_group        VARCHAR(50)  NOT NULL     UNIQUE,
    code_group_desc   VARCHAR(50)
);


CREATE TABLE code (
    code_id      INT          PRIMARY KEY AUTO_INCREMENT,
    code_group   VARCHAR(50)  NOT NULL,
    code         VARCHAR(50)  NOT NULL,
    code_desc    VARCHAR(50),

    -- 같은 code_group 안에서는 code값이 unique
    UNIQUE KEY (code_group, code),

    CONSTRAINT
        FOREIGN KEY (code_group)
            REFERENCES code_group(code_group)
            ON UPDATE CASCADE ON DELETE RESTRICT
);



-- 회원테이블

CREATE TABLE member (
    member_id       INT             PRIMARY KEY    AUTO_INCREMENT   COMMENT '회원 고유번호',
    email           VARCHAR(255)    NOT NULL       UNIQUE           COMMENT '이메일',
    password        VARCHAR(255)    NOT NULL                        COMMENT '비밀번호',
    nickname        VARCHAR(20)     NOT NULL       UNIQUE           COMMENT '별명',
    phone_num       VARCHAR(20)     NOT NULL                        COMMENT '전화번호(하이픈 제외)',
    gender          CHAR(1)         NOT NULL                        COMMENT '성별(M/F)',
    birth_date      DATE                                            COMMENT '생년월일',
    region_code     VARCHAR(5)                                      COMMENT '지역',
    education_code	INT                                             COMMENT '학력 요건 코드',
    job_code	    INT                                             COMMENT '취업 요건 코드',
    major_code	    INT                                             COMMENT '전공 요건 코드',
    income_code	    INT                                             COMMENT '소득 요건 코드',
    special_code	INT                                             COMMENT '특화 요건 코드',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE'       COMMENT '계정 상태',
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_member_region     FOREIGN KEY (region_code)       REFERENCES region(region_code)  ON UPDATE CASCADE,
    CONSTRAINT fk_member_education  FOREIGN KEY (education_code)    REFERENCES code(code_id)        ON UPDATE CASCADE,
    CONSTRAINT fk_member_job        FOREIGN KEY (job_code)          REFERENCES code(code_id)        ON UPDATE CASCADE,
    CONSTRAINT fk_member_major      FOREIGN KEY (major_code)        REFERENCES code(code_id)        ON UPDATE CASCADE,
    CONSTRAINT fk_member_income     FOREIGN KEY (income_code)       REFERENCES code(code_id)        ON UPDATE CASCADE,
    CONSTRAINT fk_member_special    FOREIGN KEY (special_code)      REFERENCES code(code_id)        ON UPDATE CASCADE,
    CONSTRAINT ck_member_gender     CHECK (gender IN ('M', 'F')),
    CONSTRAINT ck_member_status     CHECK (status IN ('ACTIVE', 'INACTIVE', 'WITHDRAWN'))
) COMMENT='회원';




-- 정책테이블

CREATE TABLE policy (
    policy_id           INT             PRIMARY KEY AUTO_INCREMENT  COMMENT '정책 고유번호',
    title               VARCHAR(255)    NOT NULL                    COMMENT '정책명 (API: plcyNm)',
    policy_description  TEXT 	                                    COMMENT '정책 설명 (API: plcyExplnCn)',
    support_content     TEXT 							            COMMENT '지원 내용 (API: plcySprtCn)',
    category_main       INT             NOT NULL	                COMMENT '대분류 (API: lclsfNm)',
    category_sub        INT 				 			            COMMENT '중분류 (API: mclsfNm)',
    region_code         VARCHAR(5) 				                    COMMENT '지역 코드 (법정동코드 앞 5자리)',
    education_code      INT 				 				        COMMENT '학력 요건 코드 (API: schoolCd)',
    job_code            INT 				 				        COMMENT '취업 요건 코드 (API: jobCd)',
    major_code          INT 				 				        COMMENT '전공 요건 코드 (API: plcyMajorCd)',
    income_code         INT 	 				                    COMMENT '소득 요건 코드 (API: earnCndSeCd)',
    special_code        INT 				 				        COMMENT '특화 요건 코드 (API: sBizCd)',
    min_age             INT 						                COMMENT '최소 연령 (API: sprtTrgtMinAge)',
    max_age             INT 						                COMMENT '최대 연령 (API: sprtTrgtMaxAge)',
    apply_start_date    DATE 						                COMMENT '신청 시작일 (API: aplyYmd 기반 파싱)',
    apply_end_date      DATE 					                    COMMENT '신청 종료일 (API: aplyYmd 기반 파싱)',
    start_date 		    DATE 					                    COMMENT '사업 시작일 (API: bizPrdBgngYmd)',
    end_date 		    DATE 					                    COMMENT '사업 종료일 (API: bizPrdEndYmd)',
    detail_url 		    VARCHAR(2048) 	NOT NULL  		            COMMENT '상세 URL (API: aplyUrlAddr)',
    view_count 		    INT 	        NOT NULL    DEFAULT 0 		COMMENT '조회수',


    CONSTRAINT fk_policy_cat_main   FOREIGN KEY (category_main)   REFERENCES code(code_id), -- 대분류
    CONSTRAINT fk_policy_cat_sub    FOREIGN KEY (category_sub)    REFERENCES code(code_id), -- 중분류
    CONSTRAINT fk_policy_region     FOREIGN KEY (region_code)     REFERENCES region(region_code) ON UPDATE CASCADE, -- 지역
    CONSTRAINT fk_policy_education  FOREIGN KEY (education_code)  REFERENCES code(code_id) ON UPDATE CASCADE, -- 학력요건
    CONSTRAINT fk_policy_job        FOREIGN KEY (job_code)        REFERENCES code(code_id) ON UPDATE CASCADE, -- 취업요건
    CONSTRAINT fk_policy_major      FOREIGN KEY (major_code)      REFERENCES code(code_id) ON UPDATE CASCADE, -- 전공요건
    CONSTRAINT fk_policy_income     FOREIGN KEY (income_code)     REFERENCES code(code_id) ON UPDATE CASCADE, -- 소득
    CONSTRAINT fk_policy_special    FOREIGN KEY (special_code)    REFERENCES code(code_id) ON UPDATE CASCADE  -- 특화요건
);


-- 정책지역
CREATE TABLE policy_region (
    policy_region_id  INT         NOT NULL AUTO_INCREMENT,
    policy_id         INT         NOT NULL,
    region_code       CHAR(5)     NOT NULL,

    PRIMARY KEY (policy_region_id),

    -- 같은 정책에 같은 지역 중복 등록 방지
    UNIQUE KEY (policy_id, region_code),

    CONSTRAINT
        FOREIGN KEY (policy_id)
            REFERENCES policy(policy_id)
            ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT
        FOREIGN KEY (region_code)
            REFERENCES region(region_code)
            ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 북마크

CREATE TABLE bookmark (
    bookmark_id  INT            PRIMARY KEY     AUTO_INCREMENT                                  COMMENT '북마크 고유번호',
    policy_id    INT            NOT NULL                                                        COMMENT '정책 고유번호',
    member_id    INT            NOT NULL                                                        COMMENT '회원 고유번호',
    status       VARCHAR(20)    NOT NULL DEFAULT 'CREATED'                                      COMMENT '상태',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP                              COMMENT '작성 일시',
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '마지막 수정 일시',

    CONSTRAINT uq_bookmark_member_policy UNIQUE (member_id, policy_id),
    CONSTRAINT fk_bookmark_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON UPDATE CASCADE,
    CONSTRAINT ck_bookmark_status CHECK (status IN ('CREATED', 'DELETED'))
) COMMENT='북마크';



-- 게시글

CREATE TABLE post (
    post_id       INT           PRIMARY KEY AUTO_INCREMENT,
    member_id     INT           NOT NULL,
    policy_id     INT           NOT NULL,
    code_id       INT           NOT NULL,
    title         VARCHAR(255)  NOT NULL,
    content       TEXT,
    is_anonymous  CHAR(1)       NOT NULL    DEFAULT 'N',
    status        VARCHAR(20)   NOT NULL    DEFAULT 'CREATED',
    created_at    TIMESTAMP     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_post_status
        CHECK (status IN ('CREATED', 'DELETED')),

    -- is_anonymous 값 검증 (Y 또는 N만 허용)
    CONSTRAINT ck_is_anonymous
        CHECK (is_anonymous IN ('Y', 'N')),
        
    CONSTRAINT fk_post_code
        FOREIGN KEY (code_id)
            REFERENCES code(code_id)
            ON UPDATE CASCADE ON DELETE RESTRICT,

    -- POLICY PK가 바뀌면 POST FK도 바뀜, 대응되는 POST 데이터가 있으면 POLICY 데이터 삭제 불가
    CONSTRAINT fk_post_policy
        FOREIGN KEY (policy_id)
            REFERENCES policy(policy_id)
            ON UPDATE CASCADE ON DELETE RESTRICT,

    -- MEMBER PK가 바뀌면 POST FK도 바뀜, 대응되는 POST 데이터가 있으면 MEMBER 데이터 삭제 불가
    CONSTRAINT fk_post_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON UPDATE CASCADE ON DELETE RESTRICT
);



-- 댓글

CREATE TABLE comment (
    comment_id  INT         PRIMARY KEY AUTO_INCREMENT,
    post_id	    INT         NOT NULL,
    member_id	INT         NOT NULL,
    content	    TEXT,
    created_at	TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at	TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status	    VARCHAR(20) NOT NULL DEFAULT 'CREATED',

    CONSTRAINT ck_comment_status
        CHECK (status IN ('CREATED', 'DELETED')),

    -- MEMBER PK가 바뀌면 COMMENT FK도 바뀜, 대응되는 COMMENT 데이터가 있으면 MEMBER 데이터 삭제 불가
    CONSTRAINT fk_comment_member
        FOREIGN KEY (member_id)
            REFERENCES member(member_id)
            ON UPDATE CASCADE ON DELETE RESTRICT,

    -- POST PK가 바뀌면 COMMENT FK도 바뀜, 대응되는 COMMENT 데이터가 있으면 POST 데이터 삭제 불가
    CONSTRAINT fk_comment_post
        FOREIGN KEY (post_id)
            REFERENCES post(post_id)
            ON UPDATE CASCADE ON DELETE RESTRICT
);


CREATE TABLE schedule (
    schedule_id INT         AUTO_INCREMENT PRIMARY KEY                              COMMENT '일정 고유번호',
    member_id   INT         NOT NULL                                                COMMENT '회원 고유번호 (member.member_id 참조)',
    policy_id   INT         NOT NULL                                                COMMENT '정책 고유번호 (policy.policy_id 참조)',
    start_date  DATE                                                                COMMENT '일정 시작일',
    end_date    DATE                                                                COMMENT '일정 종료일',
    is_alarm    CHAR(1)     NOT NULL DEFAULT 'N'                                    COMMENT '알림 여부 (Y/N)',
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'                               COMMENT '상태 (ACTIVE, DELETED)',
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP                               COMMENT '작성 일시',
    updated_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP   COMMENT '수정 일시',

    CONSTRAINT ck_schedule_status CHECK (status IN ('ACTIVE', 'DELETED')),
    CONSTRAINT fk_schedule_member FOREIGN KEY (member_id)  REFERENCES member(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_policy FOREIGN KEY (policy_id)  REFERENCES policy(policy_id) ON DELETE CASCADE,

    -- 알림 여부 체크 제약조건
    CONSTRAINT ck_is_alarm CHECK (is_alarm IN ('Y', 'N'))
);
