CREATE TABLE IF NOT EXISTS products (
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price    NUMERIC(10,2) NOT NULL CHECK (price >= 0),
    stock    INTEGER NOT NULL CHECK (stock >= 0)
);