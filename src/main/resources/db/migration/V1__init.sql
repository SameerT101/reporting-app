
CREATE TABLE categories (
                            id   BIGSERIAL PRIMARY KEY,
                            name TEXT UNIQUE NOT NULL
);

CREATE TABLE products (
                          id          BIGSERIAL PRIMARY KEY,
                          name        TEXT NOT NULL,
                          price       NUMERIC,
                          category_id BIGINT REFERENCES categories(id)
);
