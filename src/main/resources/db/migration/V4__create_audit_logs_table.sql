CREATE TABLE audit_logs (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,

     user_id BIGINT,
     username VARCHAR(100),

     action VARCHAR(50),        -- CREATE, UPDATE, DELETE, LOGIN
     entity VARCHAR(100),       -- User, Order, Product
     entity_id BIGINT,

     old_value JSON,
     new_value JSON,

     ip varchar(50),
     user_agent text,

     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);