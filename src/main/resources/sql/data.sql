-- 회원 초기 데이터
INSERT INTO members (email, password, member_role) VALUES ('user@example.com', '$2a$10$/yZS/3sclhWloFTyogEIY.GUv3mKAytUmyr29/5rtltVVCAbR.sTK', 'USER');
INSERT INTO members (email, password, member_role) VALUES ('admin@example.com', '$2a$10$5QrNbYuaqZQgiRl50deLLOqIzpmWuiZM1YK2aaj9i6ooL7.JseKua', 'ADMIN');

-- 상품 초기 데이터
INSERT INTO products (name, price, image_url) VALUES ('라이언 인형', 25000, 'https://example.com/ryan-doll.jpg');
INSERT INTO products (name, price, image_url) VALUES ('춘식이 필통', 12000, 'https://example.com/choonsik-case.jpg');
INSERT INTO products (name, price, image_url) VALUES ('어피치 머그컵', 15000, 'https://example.com/apeach-mug.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 4', 1000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 5', 2000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 6', 3000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 7', 4000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 8', 5000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 9', 6000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 10', 7000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 11', 8000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 12', 9000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 13', 11000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 14', 12000, 'img.jpg');
INSERT INTO products (name, price, image_url) VALUES ('상품 15', 13000, 'img.jpg');

-- 옵션 초기 데이터
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 1, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 2, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 3, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 4, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 5, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 6, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 7, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 8, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 9, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 10, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 11, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 12, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 13, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 14, 0);
INSERT INTO options (name, quantity, product_id, version) VALUES ('기본', 100, 15, 0);