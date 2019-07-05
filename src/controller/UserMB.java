package controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

import dao.UserDAO;
import model.User;
import util.MensageUtil;

@ManagedBean
@ViewScoped
public class UserMB {
	
	
	private User user = new User();
	private User userLogado = new User();
	private UserDAO userDAO = new UserDAO();
	private List<User> users = new ArrayList<User>();
	
	public String login() {
		String retorno = "";
		userLogado = userDAO.loginUsers(user);
		if(userLogado != null) {
			retorno = "/user/principal?faces-redirect=true";
		}
		else {
			MensageUtil.mensagemErro("Erro! email ou senha incorretos!", "Erro!");
			PrimeFaces.current().ajax().update("form");
		}
		return retorno;
	}
	
	public void cadastrarUsuario() {
		Boolean cadastrado = userDAO.cadUser(user);
		if(cadastrado) {
			MensageUtil.mensagemInfo("Usuário cadastrado com sucesso", "Sucesso!");
			user = new User();
			PrimeFaces.current().ajax().update("form-cad");
		}
		else {
			MensageUtil.mensagemErro("Erro! ao cadastrar usuário!", "Erro!");
			PrimeFaces.current().ajax().update("form-cad");
		}
	}
	
	public void listarUsers() {
		users = userDAO.listarUsers();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUserLogado() {
		return userLogado;
	}

	public void setUserLogado(User userLogado) {
		this.userLogado = userLogado;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
