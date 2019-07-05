package controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import dao.UserDAO;
import model.User;

@ManagedBean
@ViewScoped
public class UserMB {
	
	
	private User user = new User();
	private User userLogado = new User();
	private UserDAO userDAO = new UserDAO();
	
	public String login() {
		String retorno = "";
		userLogado = userDAO.loginUsers(user);
		if(userLogado != null) {
			retorno = "/user/cadastro?faces-redirect=true";
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro! email ou senha incorretos!", "Erro!"));
			PrimeFaces.current().ajax().update("form");
		}
		return retorno;
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
	
	
}
