package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionFactory;
import model.Endereco;
import model.User;

public class UserDAO {
	
	public User loginUsers(User user) {		
		User userLogado = new User();
		String sql ="SELECT id, nome, email, senha, cpf, celular, genero, excluido \n" + 
				"FROM public.users where email = ? and senha = ? and excluido is false;";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getSenha());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				userLogado.setId(rs.getLong("id"));
				userLogado.setNome(rs.getString("nome"));
				userLogado.setEmail(rs.getString("email"));
				userLogado.setSenha(rs.getString("senha"));
				userLogado.setCpf(rs.getString("cpf"));
				userLogado.setCelular(rs.getString("celular"));
				userLogado.setGenero(rs.getString("genero"));
				userLogado.setExcluido(rs.getBoolean("excluido"));
			}
			
			else 
				userLogado = null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return userLogado;
	}
	
	public boolean cadUser(User user) {
		Boolean cadastrado = false;
		String sql ="INSERT INTO public.users(\n" + 
				"nome, email, senha, cpf, celular, genero, imagem)\n" + 
				"VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
		Long idUser = null;
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getNome());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getSenha());
			ps.setString(4, user.getCpf());
			ps.setString(5, user.getCelular());
			ps.setString(6, user.getGenero());
			ps.setBytes(7, user.getImagem());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				idUser = rs.getLong("id");
			}
			
			cadastrado = cadEndereco(user, idUser, con);
			if(cadastrado)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return cadastrado;
	}
	
	public boolean cadEndereco(User user, Long idUserCadastrado, Connection con) {
		Boolean cadastrado = false;
		String sql ="INSERT INTO public.endereco(\n" + 
				"id_usuario, cep, cidade, bairro, logradouro, numero)\n" + 
				"VALUES (?, ?, ?, ?, ?, ?);";
		
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, idUserCadastrado);
			ps.setString(2, user.getEndereco().getCep());
			ps.setString(3, user.getEndereco().getLocalidade());
			ps.setString(4, user.getEndereco().getBairro());
			ps.setString(5, user.getEndereco().getLogradouro());
			ps.setInt(6, user.getEndereco().getNumero());
			ps.executeUpdate();
			cadastrado = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cadastrado;
	}
	
	public List<User> listarUsers() {		
		List<User> users = new ArrayList<User>();
		String sql ="SELECT id, nome, email, senha, cpf, celular, genero, excluido, senha, imagem\n" + 
				"FROM public.users WHERE excluido is false ORDER BY nome;";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setNome(rs.getString("nome"));
				user.setEmail(rs.getString("email"));
				user.setCpf(rs.getString("cpf"));
				user.setCelular(rs.getString("celular"));
				user.setGenero(rs.getString("genero"));
				user.setExcluido(rs.getBoolean("excluido"));
				user.setSenha(rs.getString("senha"));
				user.setImagem(rs.getBytes("imagem"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return users;
	}
	
	public Endereco listarEnderecoUsuario(Long idUser) {		
		Endereco endereco = new Endereco();
		String sql ="SELECT id, cep, cidade, bairro, logradouro, numero\n" + 
				" FROM public.endereco WHERE id_usuario = ?";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, idUser);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				endereco.setId(rs.getLong("id"));
				endereco.setCep(rs.getString("cep"));
				endereco.setLocalidade(rs.getString("cidade"));
				endereco.setBairro(rs.getString("bairro"));
				endereco.setLogradouro(rs.getString("logradouro"));
				endereco.setNumero(rs.getInt("numero"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return endereco;
	}
	
	public boolean editaUser(User user) {
		Boolean editado = false;
		String sql ="UPDATE public.users \n" + 
				"SET nome = ?, email = ?, senha = ?, cpf = ?, celular = ?, genero = ?, imagem = ? \n" + 
				"WHERE id = ?";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getNome());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getSenha());
			ps.setString(4, user.getCpf());
			ps.setString(5, user.getCelular());
			ps.setString(6, user.getGenero());
			ps.setBytes(7, user.getImagem());
			ps.setLong(8, user.getId());
			ps.executeUpdate();
			editado = editEndereco(user, con);
			if(editado)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return editado;
	}
	
	public boolean editEndereco(User user, Connection con) {
		Boolean editado = false;
		String sql ="UPDATE public.endereco\n" + 
				"	SET cep = ?, cidade = ?, bairro = ?, logradouro = ?, numero = ?\n" + 
				"	WHERE id_usuario = ?";
		
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getEndereco().getCep());
			ps.setString(2, user.getEndereco().getLocalidade());
			ps.setString(3, user.getEndereco().getBairro());
			ps.setString(4, user.getEndereco().getLogradouro());
			ps.setInt(5, user.getEndereco().getNumero());
			ps.setLong(6, user.getId());
			ps.executeUpdate();
			editado = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return editado;
	}
	
	public boolean excluiUser(Long idUser) {
		Boolean excluido = false;
		String sql ="UPDATE public.users \n" + 
				"SET excluido = true \n" + 
				"WHERE id = ?";;
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setLong(1, idUser);
			ps.executeUpdate();
			con.commit();
			excluido = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return excluido;
	}
	
	public List<User> filtroUsers(String nome) {		
		List<User> users = new ArrayList<User>();
		String sql ="SELECT id, nome, email, senha, cpf, celular, genero, excluido, senha, imagem\n" + 
				" FROM public.users WHERE excluido is false and nome ilike ? ORDER BY nome;";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "%"+nome+"%");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setNome(rs.getString("nome"));
				user.setEmail(rs.getString("email"));
				user.setCpf(rs.getString("cpf"));
				user.setCelular(rs.getString("celular"));
				user.setGenero(rs.getString("genero"));
				user.setExcluido(rs.getBoolean("excluido"));
				user.setSenha(rs.getString("senha"));
				user.setImagem(rs.getBytes("imagem"));
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return users;
	}
}
