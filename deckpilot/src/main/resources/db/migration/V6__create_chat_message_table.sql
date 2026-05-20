create table if not exists chat_message (
    id bigserial primary key,
    session_id bigint not null,
    role varchar(20) not null,
    content text not null,
    created_at timestamp default current_timestamp,

    constraint fk_chat_message_session
        foreign key (session_id)
        references chat_session(id)
        on delete cascade
);
