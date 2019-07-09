package util;

public class ValidaSenhaUtil {
	
	public static Boolean validarSenha(String senha) {
		
		Boolean senhaValida = false;
		String caracteres [];
		Boolean haNumeros = false;
		Boolean haLetras = false;
		if(senha.length() >= 8) {
			caracteres = senha.split("");
			 for(int i = 0; i < caracteres.length; i++) {
				 if(caracteres[i].matches("^[0-9]*$"))
					 haNumeros = true;
				 if(caracteres[i].matches("^[a-zA-ZÁÂÃÀÇÉÊÍÓÔÕÚÜáâãàçéêíóôõúü]*$"))
					 haLetras = true;
			 }
			 if(haNumeros && haLetras)
				 senhaValida = true;
		}
		
		return senhaValida;
	}
	
}
