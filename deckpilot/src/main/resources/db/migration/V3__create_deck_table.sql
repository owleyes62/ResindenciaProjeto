create table if not exists deck (
    id bigserial primary key,
    name varchar(120) not null,
    archetype varchar(100) not null,
    play_style varchar(50) not null,
    format varchar(50) not null default 'TCG',
    win_condition text,
    how_to_pilot text,
    source varchar(30) not null default 'manual',
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);