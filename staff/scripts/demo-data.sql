BEGIN;

INSERT INTO item (inventory_number, name, condition_description, issue_allowed)
VALUES
    ('STF-001', 'Ноутбук', 'Исправен, зарядное устройство в комплекте', TRUE),
    ('STF-002', 'Проектор', 'Исправен, кабель питания в комплекте', TRUE),
    ('STF-003', 'Фотоаппарат', 'Требуется проверка аккумулятора', FALSE)
ON CONFLICT (inventory_number) DO NOTHING;

COMMIT;
