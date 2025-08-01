CREATE TABLE members
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    member_role         VARCHAR(255) NOT NULL,
    nickname            VARCHAR(255),
    profile_image_url   VARCHAR(255),
    kakao_access_token  VARCHAR(512)
);

CREATE TABLE products
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    price     INT          NOT NULL,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE options
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    quantity   INT          NOT NULL,
    product_id BIGINT,
    version    BIGINT,
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE wishes
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT UK_MEMBER_PRODUCT UNIQUE (member_id, product_id)
);

CREATE TABLE product_orders
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT       NOT NULL,
    option_id       BIGINT       NOT NULL,
    quantity        INT          NOT NULL,
    order_date_time TIMESTAMP    NOT NULL,
    order_message   TEXT,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (option_id) REFERENCES options (id)
);