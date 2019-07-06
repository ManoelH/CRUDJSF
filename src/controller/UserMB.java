package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import dao.UserDAO;
import model.User;
import util.MensageUtil;
import util.SessionUtil;

@ManagedBean
@ViewScoped
public class UserMB {
	
	
	private User user = new User();
	private User userLogado = new User();
	private UserDAO userDAO = new UserDAO();
	private List<User> users = new ArrayList<User>();
	private User userSelecionado = new User();
	
	public String login() throws IOException {
		String retorno = "";
		userLogado = userDAO.loginUsers(user);
		if(userLogado != null) {
			userLogado.setTipo("usuario");
			SessionUtil.setSessionAttribute("usuario", userLogado);
			retorno = "/user/principal?faces-redirect=true";
		}
		else {
			
			MensageUtil.mensagemErro("Erro! email ou senha incorretos!", "Erro!");
			PrimeFaces.current().ajax().update("form");
		}
		return retorno;
	}
	
    public void logout() throws IOException {
        SessionUtil.getSession().invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect("../index.xhtml");
    }
	
	public void cadastrarUsuario() {
		Boolean cadastrado = userDAO.cadUser(user);
		if(cadastrado) {
			MensageUtil.mensagemInfo("Usuário cadastrado com sucesso", "Sucesso!");
			user = new User();
		}
		else 
			MensageUtil.mensagemErro("Erro! ao cadastrar usuário!", "Erro!");
	}
	
	public void listarUsers() {
		users = userDAO.listarUsers();
	}
	
	public void editarUsuario() {
		Boolean editado = userDAO.editaUser(user);
		if(editado) {
			MensageUtil.mensagemInfo("Usuário " +user.getNome()+ " editado com sucesso", "Sucesso!");
			user = new User();
			PrimeFaces.current().executeScript("PF('dialog-edit').hide();");
			PrimeFaces.current().ajax().update("form-list");
		}
		else {
			MensageUtil.mensagemErro("Erro! ao editar usuário!", "Erro!");
			PrimeFaces.current().ajax().update("form-list");
		}
	}
	
	public void excluirUsuario() {
		Boolean excluido = userDAO.excluiUser(user.getId());
		if(excluido) {
			MensageUtil.mensagemInfo("Usuário " +user.getNome()+ " excluido com sucesso", "Sucesso!");
			user = new User();
			users = userDAO.listarUsers();
			PrimeFaces.current().executeScript("PF('dialog-exc').hide();");
			PrimeFaces.current().ajax().update("form-list");
		}
		else {
			MensageUtil.mensagemErro("Erro! ao excluir usuário!", "Erro!");
			PrimeFaces.current().ajax().update("form-list");
		}
	}	
	
    public void handleFileUpload(FileUploadEvent event) {
    	if(event.getFile() != null) {
        	MensageUtil.mensagemInfo("Upload da imagem " + event.getFile().getFileName(), "Sucesso");
        	PrimeFaces.current().ajax().update("form-cad");
        	this.user.setImagem(event.getFile().getContents());    		
    	}

    }
	
	public void selecionarUsuario(User user){
		this.user = user;
		PrimeFaces.current().ajax().update("form-editar");
		PrimeFaces.current().executeScript("PF('dialog-edit').show();");
	}
	
	public void selecionarUsuarioExc(User user){
		this.user = user;
		PrimeFaces.current().ajax().update("form-excluir");
		PrimeFaces.current().executeScript("PF('dialog-exc').show();");
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

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public User getUserSelecionado() {
		return userSelecionado;
	}

	public void setUserSelecionado(User userSelecionado) {
		this.userSelecionado = userSelecionado;
	}
	
}
