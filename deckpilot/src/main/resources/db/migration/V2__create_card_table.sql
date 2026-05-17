create table if not exists card (
    id bigserial primary key,
    external_id bigint,
    name varchar(255) not null unique,
    card_type varchar(255),
    race varchar(255),
    attribute varchar(255),
    level integer,
    atk integer,
    defense integer,
    description text,
    image_url text,
    image_small_url text,
    image_cropped_url text,
    source varchar(100) not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);