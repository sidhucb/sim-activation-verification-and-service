CREATE EXTENSION IF NOT EXISTS pgcrypto;

Create or REPLACE FUNCTION generate_uid(size INT) RETURNS TEXT AS $$
DECLARE
  characters TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  bytes BYTEA := gen_random_bytes(size);
  l INT := length(characters);
  i INT := 0;
  output TEXT := '';
BEGIN
  WHILE i < size LOOP
    output := output || substr(characters, get_byte(bytes, i) % l + 1, 1);
    i := i + 1;
  END LOOP;
  RETURN output;
END;
$$ LANGUAGE plpgsql VOLATILE;


create table if not exists sim_status(id serial primary key,username varchar(100),email varchar(100),simstatus varchar(100),request_id varchar(100)unique default generate_uid(7));

select * from sim_status;
insert into sim_status(username,email,simstatus) values ('aishwarya','aishwarya@gmail.com','pending'),('aishwarya','aishwarya@gmail.com','active');
select * from sim_status;