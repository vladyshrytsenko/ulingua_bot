
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    username VARCHAR(255),
    native_lang VARCHAR(10),
    current_lang VARCHAR(10),
    localization VARCHAR(10),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    daily_limit SMALLINT NOT NULL,

    CONSTRAINT unique_chat_id UNIQUE (chat_id)
);

CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    country_code VARCHAR(10) NOT NULL,
    unicode VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS words (
    id SERIAL PRIMARY KEY,
    lang_id INTEGER REFERENCES languages(id),
    original VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_words (
    user_id BIGINT REFERENCES users(id),
    word_id BIGINT REFERENCES words(id),
    progress TEXT NOT NULL,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (user_id, word_id)
);
