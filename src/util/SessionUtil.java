package util;
  
  import javax.faces.context.FacesContext; 
  import javax.servlet.http.HttpServletRequest; 
  import javax.servlet.http.HttpSession;
  
  import model.Usuario;
  
  
  public class SessionUtil {
  
  public static HttpSession getSession() { return (HttpSession)
  FacesContext.getCurrentInstance().getExternalContext().getSession(false); }
  
  public static HttpServletRequest getRequest() { return (HttpServletRequest)
  FacesContext.getCurrentInstance().getExternalContext().getRequest(); }
  
  public static String getNomeUsuario() { return ((Usuario)
  getSession().getAttribute("usuario")).getNome(); }
  
  public static Long getIdUsuario() { return (long) ((Usuario)
  getSession().getAttribute("usuario")).getId(); }
  
  public static Usuario getUsuario() { return ((Usuario)
  getSession().getAttribute("usuario")); }
  
  public static Usuario getUsuarioServidor() { return ((Usuario)
  getSession().getAttribute("usuario")); }
  
  public static Usuario getUsuarioContribuinte() { return ((Usuario)
  getSession().getAttribute("usuario")); }
  
  public static void setSessionAttribute(String attrName, Object attrValue) {
  HttpServletRequest request = getRequest();
  request.getSession().setAttribute(attrName, attrValue); } }
 