package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionFactory;
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
				userLogado.setEmail(rs.getString("String"));
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
		String sql ="";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.executeUpdate();
			con.commit();
			cadastrado = true;
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
	
	public List<User> listarUsers() {		
		List<User> users = new ArrayList<User>();
		String sql ="";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				User user = new User();
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
	
	public boolean editaUser(User user) {
		Boolean editado = false;
		String sql ="";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.executeUpdate();
			con.commit();
			editado = true;
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
	
	public boolean excluiUser(Long idUser) {
		Boolean excluido = false;
		String sql ="";
		Connection con = ConnectionFactory.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
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
}
