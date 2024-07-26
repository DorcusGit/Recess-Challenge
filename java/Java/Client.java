
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        scanner = new Scanner(System.in);
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        scanner.close();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Welcome to the International Education System Mathematics Competition System");
                System.out.println("Enter function to continue or enter function>>help to view details");
                System.out.print(">>> ");

                String command = scanner.nextLine().trim();
                if (command.equalsIgnoreCase("exit")) {
                    break;
                }
                processCommand(command);
            }
        } catch (IOException e) {
            System.out.println("Error in client: " + e.getMessage());
        } finally {
            try {
                stopConnection();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void processCommand(String command) throws IOException {
        String[] parts = command.split(" ");
        String action = parts[0].toLowerCase();

        switch (action) {
            case "register":
                if (parts.length != 8) {
                    System.out.println("Invalid registration format. Use: Register username firstname lastname emailAddress date_of_birth school_registration_number image_file.png");
                } else {
                    register(parts);
                }
                break;
            case "login":
                if (parts.length == 3) {
                    login(parts[1], parts[2]);
                } else if (parts.length == 2 && parts[1].contains("@")) {
                    loginSchoolRepresentative(parts[1]);
                } else {
                    System.out.println("Invalid login format. Use: login username password (for regular users) or login email@school.com (for school representatives)");
                }
                break;
            case "logout":
                logout();
                break;
            case "viewchallenges":
                viewChallenges();
                break;
            case "attemptchallenge":
                if (parts.length != 2) {
                    System.out.println("Invalid format. Use: attemptChallenge challengeNumber");
                } else {
                    attemptChallenge(parts[1]);
                }
                break;
            case "viewapplicants":
                viewApplicants();
                break;
            case "confirm":
                if (parts.length != 3 || (!parts[1].equals("yes") && !parts[1].equals("no"))) {
                    System.out.println("Invalid format. Use: confirm yes/no username");
                } else {
                    confirmApplicant(parts[1], parts[2]);
                }
                break;
            case "function>>help":
                displayHelp();
                break;
            default:
                System.out.println("Invalid choice. Enter one of the functions or enter function>>help for help.");
        }
    }

    private void login(String username, String password) throws IOException {
        String response = sendMessage("LOGIN " + username + " " + password);
        System.out.println(response);
    }
    
    private void loginSchoolRepresentative(String email) throws IOException {
        String response = sendMessage("LOGIN " + email);
        System.out.println(response);
        if (response.contains("password has been generated")) {
            System.out.print("Please enter the password sent to your email: ");
            String password = scanner.nextLine();
            response = sendMessage("LOGIN " + email + " " + password);
            System.out.println(response);
        }
    }

    private void logout() throws IOException {
        String response = sendMessage("LOGOUT");
        System.out.println(response);
    }

    private void register(String[] args) throws IOException {
        String message = String.join(" ", args);
        String response = sendMessage("REGISTER " + message);
        System.out.println(response);
    }

    private void viewChallenges() throws IOException {
        String response = sendMessage("VIEW_CHALLENGES");
        if (response.startsWith("No challenges") || response.startsWith("Error")) {
            System.out.println(response);
        } else {
            System.out.println("Available Challenges:");
            System.out.println(response);
        }
    }

    private void attemptChallenge(String challengeNumber) throws IOException {
        try {
            String response = sendMessage("ATTEMPT_CHALLENGE " + challengeNumber);
            System.out.println(response);
            String startPrompt = in.readLine();
            System.out.println(startPrompt);
    
            System.out.println("Press Enter to start the challenge...");
            scanner.nextLine(); // Wait for user to press Enter
            out.println("start");
            out.flush();
    
            while (true) {
                String line = in.readLine();
                if (line == null || line.equals("END_OF_CHALLENGE")) {
                    break;
                }
                System.out.println(line);
    
                if (line.startsWith("Enter your answer")) {
                    System.out.print("Your answer: ");
                    String answer = scanner.nextLine();
                    out.println(answer);
                    out.flush();
                }
    
                if (line.equals("Time's up!")) {
                    System.out.println("Challenge ended due to time limit.");
                    break;
                }
            }
    
            String finalResult = in.readLine();
            System.out.println(finalResult);
        } catch (IOException e) {
            System.err.println("Error during challenge attempt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewApplicants() throws IOException {
        String response = sendMessage("VIEW_APPLICANTS");
        System.out.println(response);
    }

    private void confirmApplicant(String decision, String username) throws IOException {
        String response = sendMessage("CONFIRM_APPLICANT " + decision + " " + username);
        System.out.println(response);
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("1. Login: login username password");
        System.out.println("2. Register: Register username firstname lastname emailAddress date_of_birth school_registration_number image_file.png");
        System.out.println("3. Logout: logout");
        System.out.println("4. View Challenges: ViewChallenges");
        System.out.println("5. Attempt Challenge (for participants only): attemptChallenge challengeNumber");
        System.out.println("6. View Applicants (for school representatives only): viewApplicants");
        System.out.println("7. Confirm Applicant (for school representatives only): confirm yes/no username");
        System.out.println("8. Exit: exit");
    }
    

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.startConnection("localhost", 5000); 
            client.run();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}