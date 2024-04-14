INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('f3eefd39-b93c-4a40-9b93-8be34837ba0f', 'картон', NOW(), 'CATEGORY', NULL, NULL);

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('e1cf72cd-8a99-4fc8-94eb-6e3e7b115ce1', 'коробки', NOW(), 'CATEGORY', NULL, 'f3eefd39-b93c-4a40-9b93-8be34837ba0f');

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('310e42d3-7340-41ad-9e7d-dc11debb131b', 'подарочная коробка', NOW(), 'OFFER', 10, 'e1cf72cd-8a99-4fc8-94eb-6e3e7b115ce1');

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('4bdc9d6c-68c6-4319-a440-72e225b370fb', 'от холодильника коробка', NOW(), 'OFFER', 3, 'e1cf72cd-8a99-4fc8-94eb-6e3e7b115ce1');

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('a7012059-40bf-469d-9d3e-eda96b5851e8', 'дом из картона', NOW(), 'OFFER', 10, 'f3eefd39-b93c-4a40-9b93-8be34837ba0f');

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('3632921d-735e-467b-b6b6-f796279f617f', 'десерты', NOW(), 'CATEGORY', NULL, NULL);

INSERT INTO shop_unit (id, name, date, type, price, parent_id)
VALUES ('1fa20131-543b-4905-b3bf-af41cc728214', 'чебурашка', NOW(), 'OFFER', 9999, NULL);
