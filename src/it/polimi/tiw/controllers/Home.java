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
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

@WebServlet("/home")
public class Home extends HttpServlet {
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

        String path = "/home.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        User user = (User) req.getSession().getAttribute("user");
        Alert meetingAlert;
        if(req.getSession().getAttribute("meetingAlert")==null){
            meetingAlert = new Alert(false, Alert.DANGER, "");
            req.getSession().setAttribute("meetingAlert", meetingAlert);
        } else {
            meetingAlert = (Alert) req.getSession().getAttribute("meetingAlert");
        }
        MeetingsDAO meetingsDAO = new MeetingsDAO(connection);
        List<Meeting> meetings, invitedMeetings;
        try{
            meetings = meetingsDAO.getCreatedMeetings(user.getId());
            invitedMeetings = meetingsDAO.getInvitedMeetings(user.getId());
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in reading data");
            return;
        }
        ctx.setVariable("user", user);
        ctx.setVariable("userMadeMeetings", meetings);
        ctx.setVariable("userAvailableMeetings", invitedMeetings);
        ctx.setVariable("meetingAlert", meetingAlert);
        templateEngine.process(path, ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Alert alert = (Alert)req.getSession().getAttribute("meetingAlert");

        if(!Utility.paramExists(req, resp, new ArrayList<>(Arrays.asList("meetingName", "meetingDate","meetingTime","meetingDuration","meetingMaxParticipants")))) return;

        String name = req.getParameter("meetingName");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int duration, maxParticipants;
        try{
            duration = Integer.parseInt(req.getParameter("meetingDuration"));
            maxParticipants = Integer.parseInt(req.getParameter("meetingMaxParticipants"));
            if (duration <= 5 || duration >= (24 * 60) || maxParticipants < 2 || maxParticipants > 500) throw new IllegalArgumentException();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Date date, currentTime = new Date();
        LocalTime time;
        Calendar calendar = Calendar.getInstance();
        try{
            date = simpleDateFormat.parse(req.getParameter("meetingDate"));
            time = LocalTime.parse(req.getParameter("meetingTime"));
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, time.getHour());
            calendar.add(Calendar.MINUTE, time.getMinute());
            calendar.add(Calendar.SECOND, time.getSecond());
            long milliseconds = (calendar.getTime().getTime() - currentTime.getTime());
            if (milliseconds <= 0) throw new IllegalArgumentException();
        } catch (Exception e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Meeting meeting = new Meeting();
        meeting.setDateTime(calendar.getTime());
        meeting.setDuration(duration);
        meeting.setTitle(name);
        meeting.setMaxParticipants(maxParticipants);
        user.setPendingMeeting(meeting);
        resp.sendRedirect(getServletContext().getContextPath() + "/home/createMeeting");

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
