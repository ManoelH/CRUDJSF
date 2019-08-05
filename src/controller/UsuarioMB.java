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

import dao.UsuarioDAO;
import model.Endereco;
import model.Usuario;
import util.MensageUtil;
import util.SessionUtil;
import util.ValidaCPFUtil;
import util.ValidaEmailUtil;
import util.ValidaSenhaUtil;

@ManagedBean
@ViewScoped
public class UsuarioMB {

	private Usuario usuario = new Usuario();
	private Usuario usuarioLogado = new Usuario();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private List<Usuario> usuarios = new ArrayList<Usuario>();
	private Usuario usuarioSelecionado = new Usuario();
	private String filtroNome;
	private List<Usuario> imagemInserida = new ArrayList<Usuario>();
	private Boolean existeImagem;
	private Endereco endereco = new Endereco();

	public String login() throws IOException {
		String retorno = "";
		usuarioLogado = usuarioDAO.loginUsuario(usuario);
		if (usuarioLogado != null) {
			usuarioLogado.setTipo("usuario");
			SessionUtil.setSessionAttribute("usuario", usuarioLogado);
			retorno = "/user/principal?faces-redirect=true";
		} else {

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
		if (validarEmail()) {
			if (validarSenha()) {
				if (validarCpf()) {
					if (!cpfJaCadastrado()) {
						if (usuario.getImagem() != null) {
							Boolean cadastrado = usuarioDAO.cadastroUsuario(usuario);
							if (cadastrado) {
								MensageUtil.mensagemInfo("Usuário cadastrado com sucesso", "Sucesso!");
								usuario = new Usuario();
							} else
								MensageUtil.mensagemErro("Erro! ao cadastrar usuário!", "Erro!");
						} else
							MensageUtil.mensagemWarn("Por favor insira a imagem", "Atenção");
					}
				}
			}
		}
	}

	public void listarUsuarios() {
		usuarios = usuarioDAO.listarUsuarios();
		filtroNome = "";
	}

	public void filtrarUsuarios() {
		usuarios = usuarioDAO.filtroUsuarios(filtroNome);
	}

	public void editarUsuario() {
		if (validarEmail()) {
			if (validarSenha()) {
				if (validarCpf()) {
					if (!cpfJaCadastrado()) {
						if (usuario.getImagem() != null) {
							Boolean editado = usuarioDAO.editaUsuario(usuario);
							if (editado) {
								MensageUtil.mensagemInfo("Usuário " + usuario.getNome() + " editado com sucesso",
										"Sucesso!");
								usuario = new Usuario();
								PrimeFaces.current().executeScript("PF('dialog-edit').hide();");
							} else
								MensageUtil.mensagemErro("Erro! ao editar usuário!", "Erro!");
						} else
							MensageUtil.mensagemWarn("Por favor insira a imagem", "Atenção");
					}
				}
			}
		}

	}

	public void excluirUsuario() {
		Boolean excluido = usuarioDAO.excluiUsuario(usuario.getId());
		if (excluido) {
			MensageUtil.mensagemInfo("Usuário " + usuario.getNome() + " excluido com sucesso", "Sucesso!");
			usuario = new Usuario();
			usuarios = usuarioDAO.listarUsuarios();
			PrimeFaces.current().executeScript("PF('dialog-exc').hide();");
			PrimeFaces.current().ajax().update("form-list");
		} else {
			MensageUtil.mensagemErro("Erro! ao excluir usuário!", "Erro!");
			PrimeFaces.current().ajax().update("form-list");
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		if (event.getFile() != null) {
			MensageUtil.mensagemInfo("Upload da imagem " + event.getFile().getFileName(), "Sucesso");
			this.usuario.setImagem(event.getFile().getContents());
			existeImagem = true;
			this.imagemInserida.add(this.usuario);
		}

	}

	public void excluirImagem() {
		usuario.setImagem(null);
		this.imagemInserida.remove(0);
		existeImagem = false;
		PrimeFaces.current().ajax().update("form-editar");
	}

	public void selecionarUsuario(Usuario user) {
		this.imagemInserida = new ArrayList<Usuario>();
		this.usuario = user;
		this.imagemInserida.add(user);
		existeImagem = true;
		user.setEndereco(usuarioDAO.listarEnderecoUsuario(user.getId()));
		PrimeFaces.current().ajax().update("form-editar");
		PrimeFaces.current().executeScript("PF('dialog-edit').show();");
	}

	public void selecionarUsuarioExc(Usuario user) {
		this.usuario = user;
		PrimeFaces.current().ajax().update("form-excluir");
		PrimeFaces.current().executeScript("PF('dialog-exc').show();");
	}

	public void checarCepCompletado(AjaxBehaviorEvent event) {
		this.endereco = usuario.getEndereco();
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
				this.usuario.setEndereco(endereco);

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
		Boolean emailValido = ValidaEmailUtil.isValidEmailAddressRegex(usuario.getEmail());
		if (!emailValido)
			MensageUtil.mensagemWarn("Email inválido", "Atenção");
		return emailValido;
	}

	public Boolean validarSenha() {
		Boolean senhaValida = ValidaSenhaUtil.validarSenha(usuario.getSenha());
		if (!senhaValida)
			MensageUtil.mensagemWarn(
					"Senha inválida, certifique que a senha contém letras, números e que possua pelo menos 8 caracteres",
					"Atenção");
		return senhaValida;
	}

	public Boolean validarCpf() {
		Boolean cpfValido = ValidaCPFUtil.isCPF(usuario.getCpf());
		if (!cpfValido)
			MensageUtil.mensagemWarn("CPF inválido", "Atenção");
		return cpfValido;
	}

	public Boolean cpfJaCadastrado() {
		Boolean cadastrado = usuarioDAO.existeCpfCadastrado(usuario.getCpf(), usuario.getId());
		if (cadastrado) {
			MensageUtil.mensagemWarn("CPF já cadastrado", "Atenção");
			usuarios = usuarioDAO.listarUsuarios();
		}
		return cadastrado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

	public String getFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(String filtroNome) {
		this.filtroNome = filtroNome;
	}

	public List<Usuario> getImagemInserida() {
		return imagemInserida;
	}

	public void setImagemInserida(List<Usuario> imagemInserida) {
		this.imagemInserida = imagemInserida;
	}

	public Boolean getExisteImagem() {
		return existeImagem;
	}

	public void setExisteImagem(Boolean existeImagem) {
		this.existeImagem = existeImagem;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

}
