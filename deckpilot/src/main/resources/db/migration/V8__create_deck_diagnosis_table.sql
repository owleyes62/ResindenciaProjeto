create table if not exists deck_diagnosis (
    id bigserial primary key,
    deck_id bigint not null,
    summary text,
    strengths text,
    risks text,
    suggestions text,
    checks_json text,
    source varchar(30) not null default 'local',
    created_at timestamp default current_timestamp,

    constraint fk_deck_diagnosis_deck
        foreign key (deck_id)
        references deck(id)
        on delete cascade
);
