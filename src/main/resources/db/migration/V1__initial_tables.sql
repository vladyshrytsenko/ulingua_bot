
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    username VARCHAR(255),
    native_lang VARCHAR(10),
    current_lang VARCHAR(10),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS languages (
    id SERIAL PRIMARY KEY,
    country_code VARCHAR(10) NOT NULL,
    unicode VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS words (
    id SERIAL PRIMARY KEY,
    lang_id INTEGER REFERENCES languages(id),
    original VARCHAR(64) NOT NULL,
    part_of_speech VARCHAR(50),
    gender VARCHAR(50),
    tense VARCHAR(50) NOT NULL,
    hieroglyphs VARCHAR(16),
    transliteration VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS user_words (
    user_id INTEGER REFERENCES users(id),
    word_id INTEGER REFERENCES words(id)
);

CREATE TABLE IF NOT EXISTS user_languages (
    user_id INTEGER REFERENCES users(id),
    lang_id INTEGER REFERENCES languages(id)
);