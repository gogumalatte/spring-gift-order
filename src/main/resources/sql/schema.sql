CREATE TABLE products (
                          id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
                          name        VARCHAR(255)    NOT NULL UNIQUE,
                          price       INT             NOT NULL,
                          image_url   VARCHAR(4000)    NOT NULL
);

CREATE TABLE members (
                         id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
                         email       VARCHAR(255)    NOT NULL UNIQUE,
                         password    VARCHAR(255)    NOT NULL,
                         role        VARCHAR(50)     NOT NULL
);

CREATE TABLE wishes (
                        id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
                        member_id   BIGINT          NOT NULL,
                        product_id  BIGINT          NOT NULL,
                        quantity    INT             NOT NULL DEFAULT 1,
                        FOREIGN KEY (member_id) REFERENCES members(id),
                        FOREIGN KEY (product_id) REFERENCES products(id),
                        UNIQUE (member_id, product_id)
);

CREATE TABLE options (
                         id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
                         name        VARCHAR(50)     NOT NULL,
                         quantity    INT             NOT NULL,
                         product_id  BIGINT          NOT NULL,
                         FOREIGN KEY (product_id) REFERENCES products(id)
);