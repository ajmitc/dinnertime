create table mealplan(
    id varchar(32) primary key,
    created_by varchar(32),
    created_on timestamp,
    profile_id varchar(32),
    name varchar(64)
);

create table mealplanentry(
    id varchar(32) primary key,
    mealplan_id varchar(32),
    mpdate date not null,
    -- Text, Recipe ID
    type varchar(32) not null,
    content varchar(128),
    notes varchar(1024),
    -- Breakfast, Lunch, Dinner, Default
    category varchar(32) default 'default'
);

create table user_profile(
    id varchar(32) primary key,
    username varchar(32) unique not null,
    password varchar(64) not null,
    profile_id varchar(32) not null,
    access_token varchar(64),
    access_token_expiration timestamp with time zone
);

create table shared_profile(
    user_id varchar(32),
    profile_id varchar(32),
    -- read_only, read_write
    permission varchar(32),
    primary key (user_id, profile_id)
);

create table recipe(
    id varchar(32) primary key,
    drive_id varchar(32),
    url varchar(256),
    rating numeric(1, 1)
);

create table recipe_label(
    recipe_id varchar(32),
    label varchar(32) not null,
    primary key(recipe_id, label)
);

create table profile_recipe(
    recipe_id varchar(32),
    profile_id varchar(32),
    favorite boolean default 'f',
    notes varchar(1024),
    primary key (recipe_id, profile_id)
);


