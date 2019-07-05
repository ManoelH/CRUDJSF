--TABLES
create table users 
(id serial primary key, nome varchar, email varchar, senha varchar, cpf varchar, 
 celular varchar, genero char(1), excluido bool default false)
 
--LOGIN USER
 INSERT INTO public.users(
	 nome, email, senha, cpf, celular, genero)
	VALUES ('Manoel', 'teste@t.com', '123456', '111.111.111-11', '(82)99999-9999', 'M');
--CAD USER
 
--LIST USER
 
--UPDATE USER
 
--DELETE USER