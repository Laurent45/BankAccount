-- Demo seed data; idempotent so the scripts can run on every startup.
INSERT INTO account (id, type, balance, overdraft_limit, deposit_ceiling)
VALUES ('00000000-0000-0000-0000-000000000001', 'BANK_ACCOUNT', 0, 0, NULL)
ON CONFLICT (id) DO NOTHING;
