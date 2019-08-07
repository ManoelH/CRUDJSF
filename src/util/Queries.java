package util;

public class Queries {

	public static final String LOGIN_USUARIO = "SELECT id, nome, email, senha, cpf, celular, genero, excluido "
			+ "FROM public.users where email = ? and senha = ? and excluido is false";
	
	public static final String CADASTRO_USUARIO = "INSERT INTO public.users( nome, email, senha, cpf, celular, genero, imagem) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
	
	public static final String CADASTRO_ENDERECO = "INSERT INTO public.endereco( id_usuario, cep, cidade, bairro, logradouro, numero) "
			+ "VALUES (?, ?, ?, ?, ?, ?);";
	
	public static final String LISTA_USUARIOS = "SELECT id, nome, email, senha, cpf, celular, genero, excluido, senha, imagem "
			+ "FROM public.users WHERE excluido is false ORDER BY nome;";
	
	public static final String LISTA_ENDERECO_USUARIO = "SELECT id, cep, cidade, bairro, logradouro, numero FROM public.endereco WHERE id_usuario = ?";
	public static final String EDITA_USUARIO = "UPDATE public.users SET nome = ?, email = ?, senha = ?, cpf = ?, celular = ?, genero = ?, imagem = ? "
			+ "WHERE id = ?";
	
	public static final String EDITA_ENDERECO_USUARIO = "UPDATE public.endereco SET cep = ?, cidade = ?, bairro = ?, logradouro = ?, numero = ? "
			+ "WHERE id_usuario = ?";
	
	public static final String EXCLUI_USUARIO = "UPDATE public.users SET excluido = true WHERE id = ?";
	public static final String FILTRO_USUARIO = "SELECT id, nome, email, senha, cpf, celular, genero, excluido, senha, imagem "
			+ "FROM public.users WHERE excluido is false and nome ilike ? ORDER BY nome;";
}
