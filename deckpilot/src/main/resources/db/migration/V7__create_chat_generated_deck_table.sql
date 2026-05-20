create table if not exists chat_generated_deck (
    id bigserial primary key,
    session_id bigint not null,
    deck_id bigint not null,
    generation_index integer not null,
    user_message_id bigint,
    assistant_message_id bigint,
    created_at timestamp default current_timestamp,

    constraint fk_chat_generated_deck_session
        foreign key (session_id)
        references chat_session(id)
        on delete cascade,

    constraint fk_chat_generated_deck_deck
        foreign key (deck_id)
        references deck(id)
        on delete cascade,

    constraint fk_chat_generated_deck_user_message
        foreign key (user_message_id)
        references chat_message(id)
        on delete set null,

    constraint fk_chat_generated_deck_assistant_message
        foreign key (assistant_message_id)
        references chat_message(id)
        on delete set null,

    constraint uq_chat_generated_deck_session_index
        unique (session_id, generation_index)
);
