create table user_info
(
    user_seq     int auto_increment
        primary key,
    email        varchar(255) null,
    nickname     varchar(20)  not null,
    passwd       varchar(255) null,
    user_type    varchar(255) null,
    reg_date     datetime(6)  null,
    mod_date     datetime(6)  null,
    del_date     datetime(6)  null,
    mod_user_seq int          null,
    constraint email
        unique (email)
);