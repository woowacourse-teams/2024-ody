create table if not exists member_apple_token (
    id bigint not null auto_increment,
    member_id bigint not null unique,
    apple_refresh_token varchar(255) not null,
    primary key (id),
    constraint fk_member_apple_token_member_id foreign key (member_id) references member (id) ON DELETE CASCADE
);
