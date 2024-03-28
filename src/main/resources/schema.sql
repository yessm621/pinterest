CREATE TABLE `FILE`
(
    `file_id`    BIGINT        NOT NULL comment '파일 ID' PRIMARY KEY AUTO_INCREMENT,
    `file_name`  VARCHAR(2000) NOT NULL comment '원본 파일명',
    `saved_name` VARCHAR(2000) NOT NULL comment '새로운 파일명/S3 링크',
    `saved_path` VARCHAR(2000) NOT NULL comment '파일 경로'
) CHARSET=UTF8;

CREATE TABLE MEMBER
(
    `member_id`   BIGINT       NOT NULL comment '사용자 ID' PRIMARY KEY AUTO_INCREMENT,
    `email`       VARCHAR(255) NOT NULL comment '이메일',
    `password`    VARCHAR(255) comment '패스워드',
    `nickname`    VARCHAR(100) comment '닉네임',
    `image`       VARCHAR(2000) comment '프로필 이미지',
    `file_id`     BIGINT comment '파일 ID',
    `member_role` VARCHAR(50) comment '사용자 권한',
    `provider`    VARCHAR(100) comment '로그인 방법',
    CONSTRAINT member_email_uk UNIQUE (email),
    CONSTRAINT member_file_fk FOREIGN KEY (file_id) REFERENCES FILE (file_id)
) CHARSET=UTF8;

CREATE TABLE BOARD
(
    `board_id`  BIGINT       NOT NULL comment '보드 ID' PRIMARY KEY AUTO_INCREMENT,
    `member_id` BIGINT       NOT NULL comment '사용자 ID',
    `title`     VARCHAR(255) NOT NULL comment '제목',
    CONSTRAINT board_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id)
) CHARSET=UTF8;


CREATE TABLE ARTICLE
(
    `article_id` BIGINT       NOT NULL comment '핀 ID' PRIMARY KEY AUTO_INCREMENT,
    `member_id`  BIGINT       NOT NULL comment '사용자 ID',
    `board_id`   BIGINT       NOT NULL comment '보드 ID',
    `title`      VARCHAR(255) NOT NULL comment '제목',
    `content`    VARCHAR(2000) comment '내용',
    `file_id`    BIGINT       NOT NULL comment '파일 ID',
    `hashtag`    VARCHAR(100) comment '해시태그',
    CONSTRAINT article_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id),
    CONSTRAINT article_board_fk FOREIGN KEY (board_id) REFERENCES BOARD (board_id)
) CHARSET=UTF8;

CREATE TABLE ARTICLE_LIKE
(
    `article_like_id` BIGINT NOT NULL comment '핀 좋아요 ID' PRIMARY KEY AUTO_INCREMENT,
    `member_id`       BIGINT NOT NULL comment '사용자 ID',
    `board_id`        BIGINT NOT NULL comment '보드 ID',
    `article_id`      BIGINT NOT NULL comment '핀 ID',
    CONSTRAINT article_like_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id),
    CONSTRAINT article_like_board_fk FOREIGN KEY (board_id) REFERENCES BOARD (board_id),
    CONSTRAINT article_like_article_fk FOREIGN KEY (article_id) REFERENCES ARTICLE (article_id)
) CHARSET=UTF8;


CREATE TABLE COMMENT
(
    `comment_id` BIGINT       NOT NULL comment '댓글 ID' PRIMARY KEY AUTO_INCREMENT,
    `member_id`  BIGINT       NOT NULL comment '사용자 ID',
    `article_id` BIGINT       NOT NULL comment '핀 ID',
    `content`    VARCHAR(2000) NOT NULL comment '댓글 내용',
    CONSTRAINT comment_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id),
    CONSTRAINT comment_article_fk FOREIGN KEY (article_id) REFERENCES ARTICLE (article_id)
) CHARSET=UTF8;


CREATE TABLE FOLLOW
(
    `follow_id`   BIGINT NOT NULL comment '팔로우 ID' PRIMARY KEY AUTO_INCREMENT,
    `from_member` BIGINT NOT NULL comment 'from 사용자',
    `to_member`   BIGINT NOT NULL comment 'to 사용자',
    CONSTRAINT follow_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id),
    CONSTRAINT follow_member_fk FOREIGN KEY (member_id) REFERENCES MEMBER (member_id)
) CHARSET=UTF8;
