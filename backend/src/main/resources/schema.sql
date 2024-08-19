create table if not exists member (
    id bigint not null auto_increment,
    device_token varchar(255) not null unique,
    primary key (id)
);

create table if not exists meeting (
    id bigint not null auto_increment,
    `name` varchar(255) not null,
    `date` date not null,
    `time` time not null,
    address varchar(255) not null,
    latitude varchar(255) not null,
    longitude varchar(255) not null,
    invite_code varchar(255) not null,
    created_at timestamp not null default current_timestamp(),
    primary key (id)
);

create table if not exists mate (
    id bigint not null auto_increment,
    meeting_id bigint not null,
    member_id bigint not null,
    address varchar(255) not null,
    latitude varchar(255) not null,
    longitude varchar(255) not null,
    nickname varchar(255) not null,
    estimated_minutes bigint not null,
    primary key (id),
    constraint fk_meeting_id foreign key (meeting_id) references meeting (id),
    constraint fk_member_id foreign key (member_id) references member (id),
    constraint unique_meeting_and_nickname unique (meeting_id, nickname),
    constraint unique_meeting_and_member unique (meeting_id, member_id)
);

create table if not exists eta (
    id bigint not null auto_increment,
    mate_id bigint not null,
    remaining_minutes bigint not null,
    is_arrived boolean not null,
    is_missing boolean not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    primary key (id),
    constraint fk_eta_mate_id foreign key (mate_id) references mate (id)
);

create table if not exists notification (
    id bigint not null auto_increment,
    mate_id bigint not null,
    `type` varchar(225) check (`type` in ('departure_reminder','entry')) not null,
    status varchar(225) check (status in ('done','pending')) not null,
    send_at timestamp not null,
    created_at timestamp not null default current_timestamp(),
    primary key (id),
    constraint fk_notification_mate_id foreign key (mate_id) references mate (id)
);
