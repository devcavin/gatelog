INSERT INTO sites (id, name, location)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'Default Site',
           'Set during onboarding'
       );

INSERT INTO users (id, name, email, password_hash, role_id, site_id)
SELECT
    '00000000-0000-0000-0000-000000000002',
    'Super Admin',
    'admin@gatelog.dev',
    '$2b$10$NG7pE/TCZUCLwvs2RQYpDu1g8LFt1wfnjGqQznMkNSSPYyPZhMfBS',
    r.id,
    '00000000-0000-0000-0000-000000000001'
FROM roles r
WHERE r.name = 'SUPER_ADMIN';