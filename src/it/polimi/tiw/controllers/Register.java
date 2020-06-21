package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Alert;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utility.Utility;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/register")
public class Register extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        try{
            String driver = servletContext.getInitParameter("dbDriver");
            String url = servletContext.getInitParameter("dbUrl");
            String user = servletContext.getInitParameter("dbUser");
            String password = servletContext.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = "/register.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
        Alert alert;
        if(req.getSession().getAttribute("registerResult")==null){
            alert = new Alert(false, Alert.DANGER, "");
        } else {
            alert = (Alert) req.getSession().getAttribute("registerResult");
        }

        req.getSession().setAttribute("registerResult", alert);
        webContext.setVariable("errorMessage", req.getSession().getAttribute("registerResult"));
        templateEngine.process(path, webContext, resp.getWriter());
        if(alert.isDismissible()) alert.hide();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> paramList = new ArrayList<>(Arrays.asList("username", "password","checkPassword"));
        if(!Utility.paramExists(req, resp, paramList) || Utility.paramIsEmpty(req, resp, paramList)) return;
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String checkPassword = req.getParameter("checkPassword");
        if(!password.equals(checkPassword)){
            setAlert(req,resp, Alert.DANGER,"Password and Confirm Password are different");
            return;
        }
        UserDAO userDAO = new UserDAO(connection);
        try {
            if(!userDAO.isUsernameFree(username)){
                setAlert(req,resp, Alert.DANGER,"This username is already in use.");
                return;
            }

            userDAO.addUser(username, password);

        } catch (SQLException e){
            setAlert(req,resp, Alert.DANGER,"Database or SQL error" + e.getMessage());
            return;
        }

        // Send "Account created"
        setAlert(req,resp, Alert.SUCCESS,"Account created successfully. Please login <a href=\"login\">here</a>");
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    void setAlert(HttpServletRequest req, HttpServletResponse resp, String alertType, String alertContent) throws IOException {
        Alert alert = (Alert) req.getSession().getAttribute("registerResult");
        if (alert == null) {
            alert = new Alert(false, Alert.DANGER, alertContent);
            req.getSession().setAttribute("registerResult", alert);
        }
        else {
            alert.setType(alertType);
            alert.setContent(alertContent);
        }
        alert.show();
        alert.dismiss();
        resp.sendRedirect(getServletContext().getContextPath()+ "/register");
    }

}
