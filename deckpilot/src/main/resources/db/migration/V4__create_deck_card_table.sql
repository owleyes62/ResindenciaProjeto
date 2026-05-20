create table if not exists deck_card (
    id bigserial primary key,
    deck_id bigint not null,
    card_id bigint not null,
    copies integer not null,
    section varchar(20) not null,
    created_at timestamp default current_timestamp,

    constraint fk_deck_card_deck
        foreign key (deck_id)
        references deck(id)
        on delete cascade,

    constraint fk_deck_card_card
        foreign key (card_id)
        references card(id),

    constraint uq_deck_card_deck_card_section
        unique (deck_id, card_id, section)
);