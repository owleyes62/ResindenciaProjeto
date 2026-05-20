create table if not exists chat_session (
    id bigserial primary key,
    title varchar(150) not null default 'Nova conversa',
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);