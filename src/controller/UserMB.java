package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import com.google.gson.Gson;

import dao.UserDAO;
import model.Endereco;
import model.User;
import util.MensageUtil;
import util.SessionUtil;
import util.ValidaCPFUtil;
import util.ValidaEmailUtil;
import util.ValidaSenhaUtil;

@ManagedBean
@ViewScoped
public class UserMB {
	
	
	private User user = new User();
	private User userLogado = new User();
	private UserDAO userDAO = new UserDAO();
	private List<User> users = new ArrayList<User>();
	private User userSelecionado = new User();
	private String filtroNome;
	private List<User> imagemInserida = new ArrayList<User>();
	private Boolean existeImg;
	private Endereco endereco = new Endereco();
	
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
		if(validarEmail()) {
			if(validarSenha()) {
				if(validarCpf()) {
					if(!cpfJaCadastrado()) {
						if(user.getImagem() != null) {
							Boolean cadastrado = userDAO.cadUser(user);
							if(cadastrado) {
								MensageUtil.mensagemInfo("Usuário cadastrado com sucesso", "Sucesso!");
								user = new User();
							}
							else 
								MensageUtil.mensagemErro("Erro! ao cadastrar usuário!", "Erro!");
						}
						else
							MensageUtil.mensagemWarn("Por favor insira a imagem", "Atenção");						
					}
				}
			}
		}
	}
	
	public void listarUsers() {
		users = userDAO.listarUsers();
		filtroNome = "";
	}
	
	public void filtrarUsers() {
		users = userDAO.filtroUsers(filtroNome);
	}
	
	public void editarUsuario() {
		if(validarEmail()) {
			if(validarSenha()) {
				if(validarCpf()) {
					if(!cpfJaCadastrado()) {
						if(user.getImagem() != null) {
							Boolean editado = userDAO.editaUser(user);
							if(editado) {
								MensageUtil.mensagemInfo("Usuário " +user.getNome()+ " editado com sucesso", "Sucesso!");
								user = new User();
								PrimeFaces.current().executeScript("PF('dialog-edit').hide();");
							}
							else 
								MensageUtil.mensagemErro("Erro! ao editar usuário!", "Erro!");
						}
						else
							MensageUtil.mensagemWarn("Por favor insira a imagem", "Atenção");						
					}
				}
			}
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
        	this.user.setImagem(event.getFile().getContents());
        	existeImg = true;
        	this.imagemInserida.add(this.user);
    	}

    }
	
    public void excluirImagem() {
    	user.setImagem(null);
    	this.imagemInserida.remove(0);
    	existeImg = false;
    	PrimeFaces.current().ajax().update("form-editar");
    }
    
	public void selecionarUsuario(User user){
		this.imagemInserida = new ArrayList<User>();
		this.user = user;
		this.imagemInserida.add(user);
		existeImg = true;
		user.setEndereco(userDAO.listarEnderecoUsuario(user.getId()));
		PrimeFaces.current().ajax().update("form-editar");
		PrimeFaces.current().executeScript("PF('dialog-edit').show();");
	}
	
	public void selecionarUsuarioExc(User user){
		this.user = user;
		PrimeFaces.current().ajax().update("form-excluir");
		PrimeFaces.current().executeScript("PF('dialog-exc').show();");
	}
	
	public void checarCepCompletado(AjaxBehaviorEvent event) {
		this.endereco = user.getEndereco();
		String cep = this.endereco.getCep();
		String cepFormatado = cep.replaceAll("\\D", "");
		if (cepFormatado.length() == 8)
			this.getEndereco(cepFormatado);
	}
	
	public void getEndereco(String cep) {
		try {
			URLConnection con = getConnection("https://viacep.com.br/ws/" + cep + "/json/");
			Reader reader = new InputStreamReader(con.getInputStream(), "UTF-8");
			Gson gson = new Gson();
			endereco = gson.fromJson(reader, Endereco.class);

			if (endereco.getCep() == null) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Não foi possível localizar o endereço com o CEP digitado, por favor digite seu endereo manualmente",
						null);
				FacesContext ct = FacesContext.getCurrentInstance();
				ct.addMessage(null, msg);
			} else
				this.user.setEndereco(endereco);

		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Não foi possível localizar o endereço com o CEP digitado, por favor digite seu endereo manualmente",
					null);
			FacesContext ct = FacesContext.getCurrentInstance();
			ct.addMessage(null, msg);
			e.printStackTrace();
		}
	}
	
	public URLConnection getConnection(String enderecoWS) {
		URLConnection con = null;
		try {
			URL url = new URL(enderecoWS);
			Optional<String> proxyUrl = Optional.ofNullable(("192.168.100.3"));
			Optional<Integer> proxyPort = Optional.ofNullable((3128));
			boolean isProxySet = Stream.of(proxyUrl, proxyPort).allMatch(Optional::isPresent);
			if (isProxySet) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.get(), proxyPort.get()));
				con = url.openConnection(proxy);
			} else {
				con = url.openConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		con.setConnectTimeout(3000);
		return con;
	}

	public Boolean validarEmail() {
		Boolean emailValido = ValidaEmailUtil.isValidEmailAddressRegex(user.getEmail());
		if(!emailValido)
			MensageUtil.mensagemWarn("Email inválido", "Atenção");		
		return emailValido;
	}
	
	public Boolean validarSenha() {
		Boolean senhaValida = ValidaSenhaUtil.validarSenha(user.getSenha());
		if(!senhaValida)
			MensageUtil.mensagemWarn
			("Senha inválida, certifique que a senha contém letras, números e que possua pelo menos 8 caracteres"
			,"Atenção");	
		return senhaValida;
	}
	
	public Boolean validarCpf() {
		Boolean cpfValido = ValidaCPFUtil.isCPF(user.getCpf());
		if(!cpfValido)
			MensageUtil.mensagemWarn("CPF inválido", "Atenção");		
		return cpfValido;
	}
	
	public Boolean cpfJaCadastrado() {
		Boolean cadastrado = userDAO.existeCpfCadastrado(user.getCpf(), user.getId());
		if(cadastrado) {
			MensageUtil.mensagemWarn("CPF já cadastrado", "Atenção");
			users = userDAO.listarUsers();
		}
		return cadastrado;
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

	public String getFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(String filtroNome) {
		this.filtroNome = filtroNome;
	}

	public List<User> getImagemInserida() {
		return imagemInserida;
	}

	public void setImagemInserida(List<User> imagemInserida) {
		this.imagemInserida = imagemInserida;
	}

	public Boolean getExisteImg() {
		return existeImg;
	}

	public void setExisteImg(Boolean existeImg) {
		this.existeImg = existeImg;
	}
	
}
