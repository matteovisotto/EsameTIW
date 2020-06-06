package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Alert;
import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingsDAO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

@WebServlet("/home/createMeeting")
public class CreateMeeting extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");

        try{
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Alert alert = (Alert)req.getSession().getAttribute("meetingAlert");
        MeetingsDAO meetingsDAO = new MeetingsDAO(connection);

        if(!Utility.paramExists(req, resp, new ArrayList<>(Arrays.asList("meetingName", "meetingDate","meetingDuration")))) return;

        String name = req.getParameter("meetingName");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int duration;
        try{
            duration = Integer.parseInt(req.getParameter("meetingDuration"));
            if (duration <= 5 || duration >= (24 * 60)) throw new IllegalArgumentException();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Date date, currentTime = new Date();
        try{
            date = simpleDateFormat.parse(req.getParameter("meetingDate"));
            long milliseconds = (currentTime.getTime()-date.getTime());
            if (milliseconds <= 0) throw new IllegalArgumentException();
        } catch (Exception e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

   /*     try{
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String path = getServletContext().getContextPath() + "/manager/campaign?id="+id;
        resp.sendRedirect(path);*/
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
        }
    }
}
