package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Alert;
import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.MeetingsDAO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
        String path = "/createMeeting.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        /*Alert errorMessage = new Alert(false, Alert.DANGER, "");
        if(req.getSession().getAttribute("loginResult") == null) {
            errorMessage.hide();
        } else if((boolean)req.getSession().getAttribute("loginResult")) {
            User u = (User) req.getSession().getAttribute("user");
            String target = getServletContext().getContextPath() + "/home";
            resp.sendRedirect(target);
        } else {
            errorMessage.setContent("Invalid credential");
            errorMessage.show();
        }*/
       // ctx.setVariable("errorMessage", errorMessage);
        UserDAO userDAO = new UserDAO(connection);
        ArrayList<User> list;
        try {
            list = userDAO.getAllUsers();
            list.removeIf(user -> user.getId() == ((User)req.getSession().getAttribute("user")).getId());
        } catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); return;
        }
        ctx.setVariable("availableUsers", list);
        templateEngine.process(path, ctx, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
      //  Alert alert = (Alert)req.getSession().getAttribute("meetingAlert");
        MeetingsDAO meetingsDAO = new MeetingsDAO(connection);

        if(!Utility.paramExists(req, resp, new ArrayList<>(Collections.singletonList("invitations")))) return;

        String[] invitations = req.getParameterValues("invitations");
        ArrayList<Integer> userIds;
        try{
            userIds = Arrays.stream(invitations).distinct().map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Meeting meeting = user.getPendingMeeting();
        if (meeting == null || userIds.size() > meeting.getMaxParticipants() || userIds.size() <= 0){
            // segnala errore TODO
            user.setNumTries((short) (user.getNumTries() + 1));
            if (user.getNumTries() >= 3) {
                //risposta diversa TODO
                user.setNumTries((short)0);
                user.setPendingMeeting(null);
            }
        }
        else {
            for (Integer i : userIds) {
                try {
                    UserDAO userDAO = new UserDAO(connection);
                    if (!userDAO.existsUser(i)) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return;
                    }
                } catch (SQLException throwables) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); return;
                }

            }
            meeting.setParticipants(userIds);
            try {
                meetingsDAO.createMeeting(meeting.getTitle(), meeting.getMaxParticipants(), meeting.getDateTime(), meeting.getDuration(), user.getId(), meeting.getParticipants());
            } catch (SQLException throwables) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            user.setPendingMeeting(null);
        }

        //resp.sendRedirect(getServletContext().getContextPath() + "/home/createMeeting");

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
