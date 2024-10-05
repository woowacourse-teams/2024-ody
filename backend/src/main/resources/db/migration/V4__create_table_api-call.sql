create table if not exists api_call (
    id bigint not null auto_increment,
    client_type varchar(255) check (client_type in ('ODSAY', 'GOOGLE')) not null,
    `count` int not null,
    `date` date not null,
    primary key (id),
    unique (client_type, `date`)
);
