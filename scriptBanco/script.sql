--TABLES
create table users 
(id serial primary key, nome varchar (50), email varchar, senha varchar, cpf varchar (14) unique, 
 celular varchar, genero char(1) check (genero = 'M' or genero = 'F'), excluido bool default false, imagem bytea)

create table endereco (id serial primary key, id_usuario integer, foreign key (id_usuario) references users (id),
cep varchar (9), cidade varchar (20), bairro varchar (20), logradouro varchar (30), numero integer)
					  
--LOGIN USER
 INSERT INTO public.users(
	 nome, email, senha, cpf, celular, genero, excluido, imagem)
	VALUES ('Manoel', 'teste@teste.com', '1234567h', '123.456.789-09', '(82)99999-9999', 'M', false, null);
--ENDEREÇO
INSERT INTO public.endereco(
	id_usuario, cep, cidade, bairro, logradouro, numero)
	VALUES (1, '57050-000', 'Maceió', 'Farol', 'Avenida Fernandes Lima', 7);
