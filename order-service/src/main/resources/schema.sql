CREATE TABLE IF NOT EXISTS orders (
  id IDENTITY PRIMARY KEY,
  customer VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  total DECIMAL(19,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  id IDENTITY PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(200) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(19,2) NOT NULL,
  line_total DECIMAL(19,2) NOT NULL,
  CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(id)
);
