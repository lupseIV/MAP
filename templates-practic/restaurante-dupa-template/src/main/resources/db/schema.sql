-- Auto-generated Schema
-- Generated at: 2026-01-25

-- Table for RestaurantTable (Tables)
CREATE TABLE IF NOT EXISTS tables (
                                      id SERIAL PRIMARY KEY
);

-- Table for MenuItem (Menu)
CREATE TABLE IF NOT EXISTS menu_items (
                                          id SERIAL PRIMARY KEY,
                                          category VARCHAR(255) NOT NULL,
                                          item VARCHAR(255) NOT NULL,
                                          price DOUBLE PRECISION NOT NULL,
                                          currency VARCHAR(10) NOT NULL
);

-- Table for Order
CREATE TABLE IF NOT EXISTS orders (
                                      id SERIAL PRIMARY KEY,
                                      table_id INT NOT NULL REFERENCES tables(id),
                                      order_date TIMESTAMP NOT NULL,
                                      status VARCHAR(50) NOT NULL
);

-- Table for OrderItems (join table)
CREATE TABLE IF NOT EXISTS order_items (
                                           order_id INT NOT NULL REFERENCES orders(id),
                                           menu_item_id INT NOT NULL REFERENCES menu_items(id),
                                           PRIMARY KEY (order_id, menu_item_id)
);
