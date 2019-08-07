package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionFactory;
import model.Endereco;
import model.Usuario;
import util.Queries;

public class UsuarioDAO {

	public Usuario loginUsuario(Usuario usuario) {
		Usuario usuarioLogado = new Usuario();

		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.LOGIN_USUARIO);
			ps.setString(1, usuario.getEmail());
			ps.setString(2, usuario.getSenha());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				usuarioLogado.setId(rs.getLong("id"));
				usuarioLogado.setNome(rs.getString("nome"));
				usuarioLogado.setEmail(rs.getString("email"));
				usuarioLogado.setSenha(rs.getString("senha"));
				usuarioLogado.setCpf(rs.getString("cpf"));
				usuarioLogado.setCelular(rs.getString("celular"));
				usuarioLogado.setGenero(rs.getString("genero"));
				usuarioLogado.setExcluido(rs.getBoolean("excluido"));
			}

			else
				usuarioLogado = null;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return usuarioLogado;
	}

	public boolean cadastroUsuario(Usuario usuario) {
		Boolean cadastrado = false;

		Long idUsuario = null;
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.CADASTRO_USUARIO);
			mapearPreparedStatement(usuario, ps);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				idUsuario = rs.getLong("id");

			cadastrado = cadastroEndereco(usuario, idUsuario, con);
			if (cadastrado)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return cadastrado;
	}

	public boolean cadastroEndereco(Usuario usuario, Long idUsuarioCadastrado, Connection con) {
		Boolean cadastrado = false;

		try {
			PreparedStatement ps = con.prepareStatement(Queries.CADASTRO_ENDERECO);
			ps.setLong(1, idUsuarioCadastrado);
			ps.setString(2, usuario.getEndereco().getCep());
			ps.setString(3, usuario.getEndereco().getLocalidade());
			ps.setString(4, usuario.getEndereco().getBairro());
			ps.setString(5, usuario.getEndereco().getLogradouro());
			ps.setInt(6, usuario.getEndereco().getNumero());
			ps.executeUpdate();
			cadastrado = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cadastrado;
	}

	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.LISTA_USUARIOS);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Usuario usuario = new Usuario();
				usuario.setId(rs.getLong("id"));
				usuario.setNome(rs.getString("nome"));
				usuario.setEmail(rs.getString("email"));
				usuario.setCpf(rs.getString("cpf"));
				usuario.setCelular(rs.getString("celular"));
				usuario.setGenero(rs.getString("genero"));
				usuario.setExcluido(rs.getBoolean("excluido"));
				usuario.setSenha(rs.getString("senha"));
				usuario.setImagem(rs.getBytes("imagem"));
				usuarios.add(usuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return usuarios;
	}

	public Endereco listarEnderecoUsuario(Long idUsuario) {
		Endereco endereco = new Endereco();
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.LISTA_ENDERECO_USUARIO);
			ps.setLong(1, idUsuario);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				endereco.setId(rs.getLong("id"));
				endereco.setCep(rs.getString("cep"));
				endereco.setLocalidade(rs.getString("cidade"));
				endereco.setBairro(rs.getString("bairro"));
				endereco.setLogradouro(rs.getString("logradouro"));
				endereco.setNumero(rs.getInt("numero"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return endereco;
	}

	public boolean editaUsuario(Usuario usuario) {
		Boolean editado = false;
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.EDITA_USUARIO);
			mapearPreparedStatement(usuario, ps);
			ps.setLong(8, usuario.getId());
			ps.executeUpdate();
			editado = editaEndereco(usuario, con);
			if (editado)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return editado;
	}

	public void mapearPreparedStatement(Usuario usuario, PreparedStatement ps) {
		try {
			ps.setString(1, usuario.getNome());
			ps.setString(2, usuario.getEmail());
			ps.setString(3, usuario.getSenha());
			ps.setString(4, usuario.getCpf());
			ps.setString(5, usuario.getCelular());
			ps.setString(6, usuario.getGenero());
			ps.setBytes(7, usuario.getImagem());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean editaEndereco(Usuario usuario, Connection con) {
		Boolean editado = false;

		try {
			PreparedStatement ps = con.prepareStatement(Queries.EDITA_ENDERECO_USUARIO);
			ps.setString(1, usuario.getEndereco().getCep());
			ps.setString(2, usuario.getEndereco().getLocalidade());
			ps.setString(3, usuario.getEndereco().getBairro());
			ps.setString(4, usuario.getEndereco().getLogradouro());
			ps.setInt(5, usuario.getEndereco().getNumero());
			ps.setLong(6, usuario.getId());
			ps.executeUpdate();
			editado = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return editado;
	}

	public Boolean existeCpfCadastrado(String cpf, Long idUsuario) {
		Boolean existe = false;
		StringBuilder sql = new StringBuilder();
		sql.append("select exists (select cpf from public.users where cpf = ? ");

		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = null;
			if (idUsuario != null) {
				sql.append(" and id !=?)");
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, cpf);
				ps.setLong(2, idUsuario);
			} else {
				sql.append(")");
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, cpf);
			}

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				existe = rs.getBoolean("exists");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return existe;
	}

	public boolean excluiUsuario(Long idUsuario) {
		Boolean excluido = false;

		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.EXCLUI_USUARIO);
			ps.setLong(1, idUsuario);
			ps.executeUpdate();
			con.commit();
			excluido = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return excluido;
	}

	public List<Usuario> filtroUsuarios(String nome) {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(Queries.FILTRO_USUARIO);
			ps.setString(1, "%" + nome + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Usuario usuario = new Usuario();
				usuario.setId(rs.getLong("id"));
				usuario.setNome(rs.getString("nome"));
				usuario.setEmail(rs.getString("email"));
				usuario.setCpf(rs.getString("cpf"));
				usuario.setCelular(rs.getString("celular"));
				usuario.setGenero(rs.getString("genero"));
				usuario.setExcluido(rs.getBoolean("excluido"));
				usuario.setSenha(rs.getString("senha"));
				usuario.setImagem(rs.getBytes("imagem"));
				usuarios.add(usuario);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return usuarios;
	}
}
