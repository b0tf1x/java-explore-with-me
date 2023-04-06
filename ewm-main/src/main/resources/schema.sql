CREATE TABLE IF NOT EXISTS USERS
(
    ID    BIGINT generated by default as identity not null,
    EMAIL VARCHAR(255) not null,
    NAME  VARCHAR(255) not null,
    constraint pk_user primary key (id),
    constraint uq_user_email unique (email)
    );

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    ID     BIGINT generated by default as identity not null,
    PINNED BOOLEAN default false,
    TITLE  VARCHAR(255) not null,
    constraint pk_compilation primary key (id)
    );

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    ID   BIGINT generated by default as identity primary key,
    NAME VARCHAR(60) not null,
    constraint pk_category primary key (id),
    constraint uq_category_name unique (name)
    );

CREATE TABLE IF NOT EXISTS LOCATION
(
    ID  BIGINT generated by default as identity not null,
    LAT FLOAT not null,
    LON FLOAT not null,
    constraint pk_location primary key (id)
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    ID                 BIGINT generated by default as identity not null,
    ANNOTATION         VARCHAR(2000)               not null,
    CATEGORY_ID        BIGINT                      not null references categories (id),
    CREATED_ON         TIMESTAMP WITHOUT TIME ZONE not null,
    DESCRIPTION        VARCHAR(5000),
    EVENT_DATE         TIMESTAMP WITHOUT TIME ZONE not null,
    INITIATOR_ID       BIGINT                      not null references users (id),
    LOCATION_ID        BIGINT                      not null references location (id),
    PAID               BOOLEAN     DEFAULT FALSE,
    PARTICIPANT_LIMIT  INT         DEFAULT 0,
    PUBLISHED_ON       TIMESTAMP WITHOUT TIME ZONE not null,
    REQUEST_MODERATION BOOLEAN     DEFAULT TRUE,
    EVENT_STATE        VARCHAR(20) DEFAULT 'PENDING',
    TITLE              VARCHAR(500)                not null,
    constraint pk_event primary key (id)
    );

CREATE TABLE IF NOT EXISTS COMPILATION_EVENTS
(
    COMPILATION_ID BIGINT not null references compilations (id),
    EVENT_ID       BIGINT not null references events (id),
    CONSTRAINT PK_COMPILATION_EVENTS
    PRIMARY KEY (COMPILATION_ID, EVENT_ID)
    );

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID           BIGINT generated by default as identity primary key,
    CREATED      TIMESTAMP WITHOUT TIME ZONE not null,
    EVENT_ID     BIGINT                      not null,
    REQUESTER_ID BIGINT                      not null,
    STATUS       VARCHAR(20)                 NOT NULL,
    CONSTRAINT FK_USER_REQ FOREIGN KEY (requester_id) REFERENCES USERS (id) ON DELETE CASCADE,
    CONSTRAINT FK_EVENT_REQ FOREIGN KEY (event_id) REFERENCES EVENTS (id) ON DELETE CASCADE
    )