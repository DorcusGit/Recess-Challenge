

import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {
    private final ServerSocket serverSocket;
    private final Connection dbConnection;

    public Server(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);
        dbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mathematicschallenge", "root", "root");
    }

    public void start() {
        while (true) {
            try {
                new ClientHandler(serverSocket.accept(), dbConnection).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final Connection dbConnection;
        private PrintWriter out;
        private BufferedReader in;
        private int participantID;
        private boolean isSchoolRepresentative;
        private int schoolRegNo;
        private String currentSchoolRepEmail;
        private String currentSchoolRepPassword;
        
        

        public ClientHandler(Socket socket, Connection dbConnection) {
            this.clientSocket = socket;
            this.dbConnection = dbConnection;
            
        }

        @Override
        public void run() {
        try {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String response = processRequest(inputLine);
            out.println(response);
            out.flush();
            clearInputBuffer();  // Clear the input buffer after each response
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        closeResources();
    }
}
private void clearInputBuffer() throws IOException {
    while (in.ready()) {
        in.readLine();
    }
}


        private void closeResources() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String processRequest(String request) {
            
            String[] parts = request.split(" ");
            String action = parts[0].toUpperCase();

            switch (action) {
                case "REGISTER":
                    return registerUser(parts);
                case "LOGIN":
                    if (parts.length == 3) {
                        return loginUser(parts[1], parts[2]);
                    } else if (parts.length == 2 && parts[1].contains("@")) {
                        return generateSchoolRepresentativePassword(parts[1]);
                    } else {
                        return "Invalid login format.";
                    }
                case "LOGOUT":
                    return logoutUser();
                case "VIEW_CHALLENGES":
                    return viewChallenges();
                case "ATTEMPT_CHALLENGE":
                    return attemptChallenge(parts[1]);
                case "VIEW_APPLICANTS":
                    return viewApplicants();
                case "CONFIRM_APPLICANT":
                    if (parts.length != 3) {
                        return "Invalid command format. Use: CONFIRM_APPLICANT yes/no username";
                    }
                    return confirmApplicant(parts[1], parts[2]);
                default:
                    return "Invalid request";
            }
        }

        private String generateSchoolRepresentativePassword(String email) {
            try {
                // Check if the email exists in the School table
                String checkEmailSql = "SELECT * FROM Schools WHERE emailAddress = ?";
                try (PreparedStatement checkEmailStmt = dbConnection.prepareStatement(checkEmailSql)) {
                    checkEmailStmt.setString(1, email);
                    ResultSet emailRs = checkEmailStmt.executeQuery();
                    if (!emailRs.next()) {
                        return "Error: Invalid email address.";
                    }
                }
        
                // Generate a random 5-digit password
                String password = String.format("%05d", new Random().nextInt(100000));
                System.out.println(password);
        
                // Store the email and password for this session
                this.currentSchoolRepEmail = email;
                this.currentSchoolRepPassword = password;
        
                // TODO: Implement email sending functionality here
                // For now, we'll just return the password in the response
                return "A new password has been generated and sent to your email: " + password + 
                       "\nPlease use this password to log in.";
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error generating password: " + e.getMessage();
            }
        }

        private String registerUser(String[] parts) {
            if (parts.length != 9) {
                return "Invalid registration format";
            }
            String username = parts[2];
            String firstName = parts[3];
            String lastName = parts[4];
            String email = parts[5];
            String dateOfBirth = parts[6];
            int schoolRegNo;
            try {
                schoolRegNo = Integer.parseInt(parts[7]);
            } catch (NumberFormatException e) {
                return "Invalid school registration number";
            }
            String imagePath = parts[8];
            
            if (isRejectedApplicant(email, schoolRegNo)) {
                return "You have been previously rejected and cannot register under this school.";
            }
            
            String password = generateRandomPassword();
            String sql = "INSERT INTO Applicants (schoolRegNo, emailAddress, userName, imagePath, firstName, lastName, password, dateOfBirth) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setInt(1, schoolRegNo);
                pstmt.setString(2, email);
                pstmt.setString(3, username);
                pstmt.setString(4, imagePath);
                pstmt.setString(5, firstName);
                pstmt.setString(6, lastName);
                pstmt.setString(7, password);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = sdf.parse(dateOfBirth);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                pstmt.setDate(8, sqlDate);
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    return "User registered successfully. Your password is: " + password;
                } else {
                    return "Failed to register user";
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                return "Error registering user: " + e.getMessage();
            }
        }

        private String generateRandomPassword() {
            return UUID.randomUUID().toString().substring(0, 8);
        }

        private String loginUser(String emailOrUsername, String password) {
            if (emailOrUsername.contains("@")) {
                return loginSchoolRepresentative(emailOrUsername, password);
            } else {
                return loginParticipant(emailOrUsername, password);
            }
        }

        private String loginParticipant(String username, String password) {
            try {
                String sql = "SELECT participantID FROM Participants WHERE userName = ? AND password = ?";
                try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        this.participantID = rs.getInt("participantID");
                        this.isSchoolRepresentative = false;
                        return "Login successful. Welcome, " + username + "!";
                    } else {
                        return "Login failed. Invalid username or password.";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error during login: " + e.getMessage();
            }
        }

        private String loginSchoolRepresentative(String email, String password) {
            try {
                String sql = "SELECT schoolRegNo, schoolRepID FROM Schools WHERE emailAddress = ?";
                try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                    pstmt.setString(1, email);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        this.schoolRegNo = rs.getInt("schoolRegNo");
                        int schoolRepID = rs.getInt("schoolRepID");
                        
                        // Check if the provided email and password match the current session
                        if (email.equals(this.currentSchoolRepEmail) && password.equals(this.currentSchoolRepPassword)) {
                            this.isSchoolRepresentative = true;
                            return "Login successful. Welcome, School Representative!";
                        } else {
                            return "Login failed. Invalid email or password.";
                        }
                    } else {
                        return "Login failed. Invalid email address.";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error during login: " + e.getMessage();
            }
        }

        private String logoutUser() {
            if (isAuthenticated()) {
                participantID = 0;
                isSchoolRepresentative = false;
                schoolRegNo = 0;
                currentSchoolRepEmail = null;
                currentSchoolRepPassword = null;
                return "Logged out successfully.";
            } else {
                return "No user is currently logged in.";
            }
        }

        private boolean isAuthenticated() {
            return participantID != 0 || (isSchoolRepresentative && schoolRegNo != 0);
        }

        private String viewChallenges() {
            if (!isAuthenticated()) {
                return "You must be logged in to view challenges.";
            }

            String sql = "SELECT * FROM Challenges ORDER BY challengeNo";
            StringBuilder result = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            result.append(String.format("%-5s | %-20s | %-15s | %-15s | %-12s | %-10s | %-10s\n",
                    "No.", "Challenge Name", "Duration", "Questions", "Overall Mark", "Open Date", "Close Date"));
            result.append("-".repeat(100)).append("\n");
        
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
        
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    int challengeNo = rs.getInt("challengeNo");
                    String challengeName = rs.getString("challengeName");
                    Time attemptDuration = rs.getTime("attemptDuration");
                    int noOfQuestions = rs.getInt("noOfQuestions");
                    int overallMark = rs.getInt("overallMark");
                    Date openDate = rs.getDate("openDate");
                    Date closeDate = rs.getDate("closeDate");
        
                    result.append(String.format("%-5d | %-20s | %-15s | %-15d | %-12d | %-10s | %-10s\n",
                            challengeNo,
                            challengeName,
                            attemptDuration.toString(),
                            noOfQuestions,
                            overallMark,
                            dateFormat.format(openDate),
                            dateFormat.format(closeDate)));
                }
        
                return hasResults ? result.toString() : "No challenges found in the database.";
        
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error retrieving challenges: " + e.getMessage();
            }
        }

        private String attemptChallenge(String challengeNumber) {
            if (!isAuthenticated() || isSchoolRepresentative) {
                return "You must be logged in as a participant to attempt a challenge.";
            }
            try {
                int challengeNo = Integer.parseInt(challengeNumber);
                
                // Fetch challenge details
                String checkOpenSql = "SELECT * FROM Challenges WHERE challengeNo = ? AND openDate <= CURDATE() AND closeDate >= CURDATE()";
                try (PreparedStatement pstmt = dbConnection.prepareStatement(checkOpenSql)) {
                    pstmt.setInt(1, challengeNo);
                    ResultSet rs = pstmt.executeQuery();
                    if (!rs.next()) {
                        return "Challenge is not open or does not exist.";
                    }
                    
                    String challengeName = rs.getString("challengeName");
                    String attemptDurationStr = rs.getString("attemptDuration");
                    int totalQuestions = rs.getInt("noOfQuestions");
                    
                    LocalTime attemptDuration = LocalTime.parse(attemptDurationStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
                    long durationInSeconds = attemptDuration.toSecondOfDay();
        
                    // Check number of attempts
                    if (hasExceededAttempts(challengeNo)) {
                        return "You have already attempted this challenge 3 times.";
                    }
                    
                    List<Map<String, Object>> questions = fetchRandomQuestions(challengeNo);
                    
                    String description = String.format("Challenge: %s\nDuration: %s",
                            challengeName, attemptDuration.toString());
                    
                    out.println(description);
                    out.flush();
                    
                    String startResponse = in.readLine();
                    if (!startResponse.equalsIgnoreCase("start")) {
                        return "Challenge cancelled.";
                    }
                    
                    int attemptID = storeAttempt(challengeNo);
                    return conductChallenge(questions, durationInSeconds, attemptID);
                }
            } catch (SQLException | IOException e) {
                System.err.println("Error during challenge attempt: " + e.getMessage());
                e.printStackTrace();
                return "Error during challenge attempt: " + e.getMessage();
            }
        }
        
        private String conductChallenge(List<Map<String, Object>> questions, long durationInSeconds, int attemptID) throws IOException, SQLException {
            int totalScore = 0;
            int totalMarks = 0;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (durationInSeconds * 1000);
        
            for (int i = 0; i < questions.size(); i++) {
                Map<String, Object> question = questions.get(i);
                long currentTime = System.currentTimeMillis();
                if (currentTime >= endTime) {
                    out.println("Time's up!");
                    out.flush();
                    break;
                }
        
                long remainingTime = endTime - currentTime;
                out.println(String.format("Question %d/%d", i + 1, questions.size()));
                out.println(question.get("question"));
                out.println(String.format("Remaining time: %s", formatDuration(remainingTime)));
                out.println("Enter your answer or '-' to skip:");
                out.flush();
        
                String userAnswer = readLineWithTimeout(remainingTime);
                if (userAnswer == null) {
                    out.println("Time's up for this question!");
                    out.flush();
                    userAnswer = "-";
                }
        
                int questionNo = (int) question.get("questionNo");
                int score = evaluateAnswer(questionNo, userAnswer);
                storeAttemptQuestion(attemptID, questionNo, score, userAnswer);
                totalScore += score;
                totalMarks += (int) question.get("marks");
        
                out.println("Answer recorded. Moving to next question...");
                out.flush();
            }
        
            out.println("END_OF_CHALLENGE");
            out.flush();
        
            double percentageMark;
if (totalMarks == 0) {
    percentageMark = 0.0; // or some other default value
} else {
    percentageMark = (double) totalScore / totalMarks * 100;
}
saveAttemptResult(attemptID, startTime, totalScore, percentageMark);
        
return String.format("Challenge completed. Your score: %d (%.2f%%)", totalScore, percentageMark);
        }
        
        private String formatDuration(long remainingTime) {
            long seconds = remainingTime / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            seconds %= 60;
            minutes %= 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        private String readLineWithTimeout(long timeoutMillis) throws IOException {
            long startTime = System.currentTimeMillis();
            StringBuilder input = new StringBuilder();
            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                if (in.ready()) {
                    int c = in.read();
                    if (c == -1 || c == '\n') {
                        break;
                    }
                    input.append((char) c);
                }
                try {
                    Thread.sleep(100); // Small delay to prevent busy-waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return input.length() > 0 ? input.toString() : null;
        }

        private boolean hasExceededAttempts(int challengeNo) throws SQLException {
            String checkAttemptsSql = "SELECT COUNT(*) as attempts FROM Attempts WHERE challengeNo = ? AND participantID = ?";
            try (PreparedStatement attemptStmt = dbConnection.prepareStatement(checkAttemptsSql)) {
                attemptStmt.setInt(1, challengeNo);
                attemptStmt.setInt(2, participantID);
                ResultSet attemptRs = attemptStmt.executeQuery();
                return attemptRs.next() && attemptRs.getInt("attempts") >= 3;
            }
        }

        private List<Map<String, Object>> fetchRandomQuestions(int challengeNo) throws SQLException {
            String questionSql = "SELECT q.questionNo, q.question, a.answer, a.marksAwarded FROM Questions q JOIN Answers a ON q.questionNo = a.questionNo WHERE q.questionBankID = (SELECT questionBankID FROM question_banks WHERE challengeNo = ?) ORDER BY RAND() LIMIT 10";
            List<Map<String, Object>> questions = new ArrayList<>();
            try (PreparedStatement questionStmt = dbConnection.prepareStatement(questionSql)) {
                questionStmt.setInt(1, challengeNo);
                ResultSet questionRs = questionStmt.executeQuery();


                while (questionRs.next()) {
                    Map<String, Object> question = new HashMap<>();
                    question.put("questionNo", questionRs.getInt("questionNo"));
                    question.put("question", questionRs.getString("question"));
                    question.put("answer", questionRs.getString("answer"));
                    question.put("marks", questionRs.getInt("marksAwarded"));
                    questions.add(question);
                }
            }
            return questions;
        }

        private int storeAttempt(int challengeNo) throws SQLException {
            String insertAttemptSql = "INSERT INTO Attempts (startTime, participantID, challengeNo, endTime, score, percentageMark) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(insertAttemptSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(2, participantID);
                pstmt.setInt(3, challengeNo);
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(5, 1); // Set score parameter to actual score value

                pstmt.setDouble(6, 1); // Set percentageMark parameter
                pstmt.executeUpdate();
        
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int attemptID = generatedKeys.getInt(1);
        
                        // ... (code to complete the attempt)
        
                        String updateAttemptSql = "UPDATE Attempts SET endTime = ? WHERE attemptID = ?";
                        try (PreparedStatement updatePstmt = dbConnection.prepareStatement(updateAttemptSql)) {
                            updatePstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                            updatePstmt.setInt(2, attemptID);
                            updatePstmt.executeUpdate();
                        }
        
                        return attemptID;
                    } else {
                        throw new SQLException("Creating attempt failed, no ID obtained.");
                    }
                }
            }
        }
        private int evaluateAnswer(int questionNo, String userAnswer) throws SQLException {
            String sql = "SELECT answer, marksAwarded FROM Answers WHERE questionNo = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setInt(1, questionNo);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String correctAnswer = rs.getString("answer");
                    int marks = rs.getInt("marksAwarded");
                    
                    if (userAnswer.equals("-")) {
                        return 0;
                    } else if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        return marks;
                    } else {
                        return -3;
                    }
                } else {
                    throw new SQLException("No answer found for question " + questionNo);
                }
            }
        }

        private void storeAttemptQuestion(int attemptID, int questionNo, int score, String givenAnswer) throws SQLException {
            String insertSql = "INSERT INTO Attemptqns (attemptID, questionNo, score, givenAnswer) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(insertSql)) {
                pstmt.setInt(1, attemptID);
                pstmt.setInt(2, questionNo);
                pstmt.setInt(3, score);
                pstmt.setString(4, givenAnswer);
                pstmt.executeUpdate();
            }
        }

        private void saveAttemptResult(int attemptID, long startTime, int totalScore, double percentageMark) throws SQLException {
            String saveAttemptSql = "UPDATE Attempts SET endTime = ?, score = ?, percentageMark = ? WHERE attemptID = ?";
            try (PreparedStatement saveStmt = dbConnection.prepareStatement(saveAttemptSql)) {
                saveStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                saveStmt.setInt(2, totalScore);
                saveStmt.setDouble(3, percentageMark);
                saveStmt.setInt(4, attemptID);
                saveStmt.executeUpdate();
            }
        }
        
        private String viewApplicants() {
            if (!isSchoolRepresentative) {
                return "You don't have permission to view applicants.";
            }
        
            String sql = "SELECT applicantID, userName, firstName, lastName, emailAddress, dateOfBirth FROM Applicants WHERE schoolRegNo = ?";
            StringBuilder result = new StringBuilder();
            result.append("List of Pending Applicants:\n");
            result.append(String.format("%-5s | %-20s | %-20s | %-20s | %-30s | %-15s\n",
                    "ID", "Username", "First Name", "Last Name", "Email", "Date of Birth"));
            result.append("-".repeat(120)).append("\n");
        
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setInt(1, schoolRegNo);
                ResultSet rs = pstmt.executeQuery();
        
                while (rs.next()) {
                    int applicantID = rs.getInt("applicantID");
                    String username = rs.getString("userName");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String email = rs.getString("emailAddress");
                    Date dob = rs.getDate("dateOfBirth");
        
                    result.append(String.format("%-5d | %-20s | %-20s | %-20s | %-30s | %-15s\n",
                            applicantID, username, firstName, lastName, email, dob.toString()));
                }
        
                return result.toString();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error retrieving applicants: " + e.getMessage();
            }
        }

        private String confirmApplicant(String decision, String username) {
            if (!isSchoolRepresentative) {
                return "You don't have permission to confirm applicants.";
            }
        
            boolean isApproved = decision.equalsIgnoreCase("yes");
            String targetTable = isApproved ? "Participant" : "Rejected";
        
            try {
                dbConnection.setAutoCommit(false);
        
                // Get applicant details
                String selectSql = "SELECT * FROM Applicants WHERE userName = ? AND schoolRegNo = ?";
                try (PreparedStatement selectStmt = dbConnection.prepareStatement(selectSql)) {
                    selectStmt.setString(1, username);
                    selectStmt.setInt(2, schoolRegNo);
                    ResultSet rs = selectStmt.executeQuery();
        
                    if (!rs.next()) {
                        dbConnection.rollback();
                        return "No applicant found with username: " + username;
                    }
        
                    int applicantID = rs.getInt("applicantID");
        
                    // Insert into target table
                    String insertSql;
                    if (isApproved) {
                        insertSql = "INSERT INTO Participants (applicantID, firstName, lastName, emailAddress, dateOfBirth, schoolRegNo, userName, imagePath, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    } else {
                        insertSql = "INSERT INTO Rejecteds (schoolRegNo, emailAddress, applicantID, userName, imagePath, firstName, lastName, password, dateOfBirth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    }
        
                    try (PreparedStatement insertStmt = dbConnection.prepareStatement(insertSql)) {
                        if (isApproved) {
                            insertStmt.setInt(1, applicantID);
                            insertStmt.setString(2, rs.getString("firstName"));
                            insertStmt.setString(3, rs.getString("lastName"));
                            insertStmt.setString(4, rs.getString("emailAddress"));
                            insertStmt.setDate(5, rs.getDate("dateOfBirth"));
                            insertStmt.setInt(6, rs.getInt("schoolRegNo"));
                            insertStmt.setString(7, rs.getString("userName"));
                            insertStmt.setString(8, rs.getString("imagePath"));
                            insertStmt.setString(9, rs.getString("password"));
                        } else {
                            insertStmt.setInt(1, rs.getInt("schoolRegNo"));
                            insertStmt.setString(2, rs.getString("emailAddress"));
                            insertStmt.setInt(3, applicantID);
                            insertStmt.setString(4, rs.getString("userName"));
                            insertStmt.setString(5, rs.getString("imagePath"));
                            insertStmt.setString(6, rs.getString("firstName"));
                            insertStmt.setString(7, rs.getString("lastName"));
                            insertStmt.setString(8, rs.getString("password"));
                            insertStmt.setDate(9, rs.getDate("dateOfBirth"));
                        }
        
                        insertStmt.executeUpdate();
                    }
        
                    // Delete from Applicant table
                    String deleteSql = "DELETE FROM Applicants WHERE applicantID = ?";
                    try (PreparedStatement deleteStmt = dbConnection.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, applicantID);
                        deleteStmt.executeUpdate();
                    }
        
                    dbConnection.commit();
        
                    // Send email notification (implement this method separately)
                    sendEmailNotification(rs.getString("emailAddress"), isApproved);
        
                    return "Applicant " + username + " has been " + (isApproved ? "accepted" : "rejected") + ".";
                }
            } catch (SQLException e) {
                try {
                    dbConnection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
                return "Error confirming applicant: " + e.getMessage();
            } finally {
                try {
                    dbConnection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void sendEmailNotification(String email, boolean isApproved) {
            // Implement email sending logic here
            System.out.println("Sending " + (isApproved ? "acceptance" : "rejection") + " email to: " + email);
        }


        private boolean isRejectedApplicant(String email, int schoolRegNo) {
            String sql = "SELECT * FROM Rejecteds WHERE emailAddress = ? AND schoolRegNo = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setInt(2, schoolRegNo);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server(5000);
            System.out.println("Server started on port 5000");
            server.start();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}