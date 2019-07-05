package util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MensageUtil {
	
	public static void mensagemErro(String mensagem, String erro) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, erro));
	}
	
	public static void mensagemInfo(String mensagem, String info) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, info));
	}
	
	public static void mensagemWarn(String mensagem, String warn) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, warn));
	}
}
