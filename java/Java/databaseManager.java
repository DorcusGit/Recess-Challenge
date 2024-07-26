

import java.sql.*;

public class databaseManager {
    private Connection connection;

    public databaseManager(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public void registerParticipant(String username, String firstName, String lastName, String email, String dob, int schoolId) throws SQLException {
        String sql = "INSERT INTO Participants (username, firstName, lastName, email, dob, schoolId) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setDate(5, Date.valueOf(dob));
            pstmt.setInt(6, schoolId);
            pstmt.executeUpdate();
        }
    }

    // Add other database operations here (e.g., getChallenges, submitResponse, etc.)

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
