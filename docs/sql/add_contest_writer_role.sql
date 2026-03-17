-- 공모전 게시 권한용 역할 추가
-- SecurityConfig: /contest/write, /contest/edit, /contest/delete 는 이 역할이 있어야 접근 가능
-- roles 테이블 구조: role_id (PK), name 등 (프로젝트에 맞게 컬럼 확인 후 실행)

-- 1) 역할 추가 (이미 있으면 중복 오류 무시하거나, 한 번만 실행)
INSERT INTO roles (name) VALUES ('ROLE_CONTEST_WRITER');
-- 중복 시: name에 UNIQUE가 있으면 INSERT IGNORE 또는 ON DUPLICATE KEY UPDATE 사용

-- 2) 특정 사용자에게 권한 부여 (예: user_id = 1 인 사용자)
-- INSERT INTO user_roles (id, role_id)
-- SELECT 1, role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER'
-- WHERE NOT EXISTS (SELECT 1 FROM user_roles WHERE id = 1 AND role_id = (SELECT role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER'));

-- 3) 관리자(ROLE_ADMIN)에게 공모전 게시 권한도 부여 (관리자는 공모전도 게시 가능하도록)
-- INSERT INTO user_roles (id, role_id)
-- SELECT ur.id, (SELECT role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER')
-- FROM user_roles ur
-- JOIN roles r ON r.role_id = ur.role_id AND r.name = 'ROLE_ADMIN'
-- WHERE NOT EXISTS (
--   SELECT 1 FROM user_roles ur2
--   WHERE ur2.id = ur.id AND ur2.role_id = (SELECT role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER')
-- );

-- 4) 모든 일반 회원(ROLE_USER)에게 공모전 게시 권한 부여하려면 (필요 시 주석 해제 후 실행)
-- INSERT INTO user_roles (id, role_id)
-- SELECT ur.id, (SELECT role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER')
-- FROM user_roles ur
-- JOIN roles r ON r.role_id = ur.role_id AND r.name = 'ROLE_USER'
-- WHERE NOT EXISTS (
--   SELECT 1 FROM user_roles ur2
--   WHERE ur2.id = ur.id AND ur2.role_id = (SELECT role_id FROM roles WHERE name = 'ROLE_CONTEST_WRITER')
-- );
