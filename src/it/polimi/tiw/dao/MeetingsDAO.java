package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MeetingsDAO {
    private Connection con;
    public MeetingsDAO(Connection connection) {
        this.con = connection;
    }

    public ArrayList<Meeting> getCreatedMeetings(int id) throws SQLException, NoSuchElementException {
        String query = "SELECT * FROM meetings WHERE id = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                ArrayList<Meeting> meetingList = new ArrayList<>();
                while(result.next()) {
                    Meeting meeting = new Meeting();
                    meeting.setTitle(StringEscapeUtils.unescapeJava(result.getString("title")));
                    meeting.setMaxParticipants(result.getInt("maxParticipants"));
                    meeting.setDateTime(result.getTimestamp("datetime"));
                    meeting.setDuration(result.getInt("duration"));
                    meetingList.add(meeting);
                }
                return meetingList;
            }

        }
    }

    public ArrayList<Meeting> getInvitedMeetings(int id) throws SQLException, NoSuchElementException {
        String query = "SELECT * FROM meetings JOIN invitations ON meetings.id = invitations.meetingid WHERE userid = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery();) {
                ArrayList<Meeting> meetingList = new ArrayList<>();
                while(result.next()) {
                    Meeting meeting = new Meeting();
                    meeting.setTitle(StringEscapeUtils.unescapeJava(result.getString("title")));
                    meeting.setMaxParticipants(result.getInt("maxParticipants"));
                    meeting.setDateTime(result.getTimestamp("datetime"));
                    meeting.setDuration(result.getInt("duration"));
                    meetingList.add(meeting);
                }
                return meetingList;
            }

        }
    }


}
