//package util;
//
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//import model.User;
//
//
//public class SessionUtil {
//
//    public static HttpSession getSession() {
//        return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//    }
//
//    public static HttpServletRequest getRequest() {
//        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//    }
//
//    public static String getNomeUsuario() {
//        return ((User) getSession().getAttribute("usuario")).getNome();
//    }
//
//    public static Long getIdUsuario() {
//        return (long) ((User) getSession().getAttribute("usuario")).getId();
//    }
//
//    public static User getUsuario() {
//        return ((User) getSession().getAttribute("usuario"));
//    }
//
//    public static User getUsuarioServidor() {
//        return ((User) getSession().getAttribute("usuario"));
//    }
//
//    public static User getUsuarioContribuinte() {
//        return ((User) getSession().getAttribute("usuario"));
//    }
//
//    public static void setSessionAttribute(String attrName, Object attrValue) {
//        HttpServletRequest request = getRequest();
//        request.getSession().setAttribute(attrName, attrValue);
//    }
//}
