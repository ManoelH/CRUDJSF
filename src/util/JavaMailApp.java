package util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import br.gov.al.maceio.inscricoesonline.model.Usuario;

public class JavaMailApp {

    public Boolean enviarEmail(String emailRemetente, String emailDestinatario, String body, String assunto) throws UnsupportedEncodingException {
        
        final String username = "naoresponder@dti.maceio.al.gov.br";
        final String password = "nr17semge";
        
        Boolean enviou = false;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "192.168.10.42");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "*");
        //props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailRemetente, "Prefeitura Municipal de Maceió"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinatario));
            message.setSubject(assunto);
            message.setText(body);
            message.setContent(body, "text/html");

            Transport.send(message);

            enviou = true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return enviou;
    }
    
    public boolean enviarEmailConvocado(Usuario user, String nomeCurso) {
		String nomeDestinatario = user.getNome();
		String emailRemetente = "naoresponder@dti.maceio.al.gov.br"; // SERVIDOR LOCAL
		String nomeRemetente = "MPDOM";
		Boolean retorno = true;

		String url = "http://localhost:8080/inscricoes-online/faces/pages/principal.xhtml";
		String protocolo = "smtp";
		String servidor = "192.168.10.42"; // SERVIDOR LOCAL

		Properties props = new Properties();

		// EMAIL
		String assunto = "Convocação";
		String solicitante = "Olá "+user.getNome() +",\n"
				+ "Você foi convocado da lista de espera para realizar o curso " +nomeCurso+"!"
				+"\nAcesse: "+url+" faça o login e confirme a inscrição em até 24 horas para não perder o prazo de confirmação da matrícula";
		
		
		
		// SERVIDOR LOCAL
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "192.168.10.42");
		props.put("mail.smtp.debug", "false");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.starttls.enable", "true");
		final String username = "naoresponder@dti.maceio.al.gov.br";
		final String senha = "nr17semge";

		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(false);

		try {
			InternetAddress iaFrom = new InternetAddress(emailRemetente, nomeRemetente);
			InternetAddress[] iaTo = new InternetAddress[1];
			InternetAddress[] iaReplyTo = new InternetAddress[1];

			iaReplyTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);
			iaTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);

			Message msg = new MimeMessage(session);

			if (iaReplyTo != null) {
				msg.setReplyTo(iaReplyTo);
			}
			if (iaFrom != null) {
				msg.setFrom(iaFrom);
			}
			if (iaTo.length > 0) {
				msg.setRecipients(Message.RecipientType.TO, iaTo);
			}

			msg.setFrom(new InternetAddress(emailRemetente));
			InternetAddress[] address = { new InternetAddress(user.getEmail()) };
			msg.setRecipients(Message.RecipientType.TO, address);

			// CRIA O CONTEUDO DO EMAIL
			Multipart multipart = new MimeMultipart();

			msg.setSubject(assunto);
			msg.setSentDate(new Date());
			BodyPart solicitanteTextPart = new MimeBodyPart();

			solicitanteTextPart.setText(solicitante);
			multipart.addBodyPart(solicitanteTextPart);

			// ADICIONA O CONTEUDO AO EMAIL
			msg.setContent(multipart);

			Transport tr = session.getTransport(protocolo);
			tr.connect(servidor, username, senha);

			msg.saveChanges();

			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();

		} catch (UnsupportedEncodingException e) {
			retorno = false;
			e.printStackTrace();
		} catch (MessagingException e) {
			retorno = false;
			e.printStackTrace();
		}

		return retorno;
	}
    
    public boolean enviaEmailConfirmacaoInscricao(Usuario user, String nomeCurso) {
		String nomeDestinatario = user.getNome();
		String emailRemetente = "naoresponder@dti.maceio.al.gov.br"; // SERVIDOR LOCAL
		String nomeRemetente = "MPDOM";
		Boolean retorno = true;

		String url = "http://localhost:8080/inscricoes-online/faces/pages/principal.xhtml";
		String protocolo = "smtp";
		String servidor = "192.168.10.42"; // SERVIDOR LOCAL

		Properties props = new Properties();

		// EMAIL
		String assunto = "Convocação";
		String mensagem = "<html><head><meta charset=\"UTF-8\"</head><body><center>"
	            + "<img src=\"http://www.maceio.al.gov.br/wp-content/uploads/lucasragucci/png/2014/07/prefeitura-de-maceio-logo-horizontal.png\"/>"
	            + "<br />Olá " + user.getNome() +"!\n"
	            + "<br />Sua matrícula no curso: <b><h3> " + nomeCurso + "</b></h3> foi realizada com sucesso!</center></body></html>";
		

		
		
		
		// SERVIDOR LOCAL
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "192.168.10.42");
		props.put("mail.smtp.debug", "false");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.starttls.enable", "true");
		final String username = "naoresponder@dti.maceio.al.gov.br";
		final String senha = "nr17semge";

		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(false);

		try {
			InternetAddress iaFrom = new InternetAddress(emailRemetente, nomeRemetente);
			InternetAddress[] iaTo = new InternetAddress[1];
			InternetAddress[] iaReplyTo = new InternetAddress[1];

			iaReplyTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);
			iaTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);

			Message msg = new MimeMessage(session);

			if (iaReplyTo != null) {
				msg.setReplyTo(iaReplyTo);
			}
			if (iaFrom != null) {
				msg.setFrom(iaFrom);
			}
			if (iaTo.length > 0) {
				msg.setRecipients(Message.RecipientType.TO, iaTo);
			}

			msg.setFrom(new InternetAddress(emailRemetente));
			InternetAddress[] address = { new InternetAddress(user.getEmail()) };
			msg.setRecipients(Message.RecipientType.TO, address);

			// CRIA O CONTEUDO DO EMAIL
			Multipart multipart = new MimeMultipart();

			msg.setSubject(assunto);
			msg.setSentDate(new Date());
			BodyPart solicitanteTextPart = new MimeBodyPart();

			solicitanteTextPart.setText(mensagem);
			multipart.addBodyPart(solicitanteTextPart);

			// ADICIONA O CONTEUDO AO EMAIL
			msg.setContent(multipart);

			Transport tr = session.getTransport(protocolo);
			tr.connect(servidor, username, senha);

			msg.saveChanges();

			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();

		} catch (UnsupportedEncodingException e) {
			retorno = false;
			e.printStackTrace();
		} catch (MessagingException e) {
			retorno = false;
			e.printStackTrace();
		}

		return retorno;
	}
        
    
    public boolean enviarEmailExpiracao(Usuario user) {
    	String nomeDestinatario = user.getNome();
    	String emailRemetente = "naoresponder@dti.maceio.al.gov.br"; // Servidor Local
    	String nomeRemetente = "MPDOM";
    	Boolean retorno = true;

    	String protocolo = "smtp";

    	String servidor = "192.168.10.42"; // SERVIDOR LOCAL

    	Properties props = new Properties();

    	// EMAIL
    	String assunto = "Convocação";
    	String solicitante = "Olá "+user.getNome() +",\n"
    			+ "Sua Convocacao expirou, pois o prazo de 24 horas acabou.";
    	
    	// SERVIDOR LOCAL
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.host", "192.168.10.42");
    	props.put("mail.smtp.debug", "false");
    	props.put("mail.smtp.port", "587");
    	props.put("mail.smtp.ssl.trust", "*");
    	props.put("mail.smtp.starttls.enable", "true");
    	final String username = "naoresponder@dti.maceio.al.gov.br";
    	final String senha = "nr17semge";

    	Session session = Session.getDefaultInstance(props, null);
    	session.setDebug(false);

    	try {
    		InternetAddress iaFrom = new InternetAddress(emailRemetente, nomeRemetente);
    		InternetAddress[] iaTo = new InternetAddress[1];
    		InternetAddress[] iaReplyTo = new InternetAddress[1];

    		iaReplyTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);
    		iaTo[0] = new InternetAddress(user.getEmail(), nomeDestinatario);

    		Message msg = new MimeMessage(session);

    		if (iaReplyTo != null) {
    			msg.setReplyTo(iaReplyTo);
    		}
    		if (iaFrom != null) {
    			msg.setFrom(iaFrom);
    		}
    		if (iaTo.length > 0) {
    			msg.setRecipients(Message.RecipientType.TO, iaTo);
    		}

    		msg.setFrom(new InternetAddress(emailRemetente));
    		InternetAddress[] address = { new InternetAddress(user.getEmail()) };
    		msg.setRecipients(Message.RecipientType.TO, address);

    		// CRIA O CONTEUDO DO EMAIL
    		Multipart multipart = new MimeMultipart();

    		msg.setSubject(assunto);
    		msg.setSentDate(new Date());
    		BodyPart solicitanteTextPart = new MimeBodyPart();

    		solicitanteTextPart.setText(solicitante);
    		multipart.addBodyPart(solicitanteTextPart);

    		// ADICIONA O CONTEUDO AO EMAIL
    		msg.setContent(multipart);

    		Transport tr = session.getTransport(protocolo);
    		tr.connect(servidor, username, senha);

    		msg.saveChanges();

    		tr.sendMessage(msg, msg.getAllRecipients());
    		tr.close();

    	} catch (UnsupportedEncodingException e) {
    		retorno = false;
    		e.printStackTrace();
    	} catch (MessagingException e) {
    		retorno = false;
    		e.printStackTrace();
    	}

    	return retorno;
    }
}