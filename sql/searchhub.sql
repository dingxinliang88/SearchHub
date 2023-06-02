create database searchhub;
use searchhub;

-- 文章表
create table if not exists article
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '文章' collate = utf8mb4_unicode_ci;