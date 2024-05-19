ALTER TABLE refresh_token ALTER id set data type bigint;
ALTER TABLE users ADD COLUMN IF NOT EXISTS refresh_token_id bigint;