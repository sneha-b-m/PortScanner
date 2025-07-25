import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class PortScanner {

    private static final Map<Integer, String> serviceNames = Map.ofEntries(
        Map.entry(20, "FTP-Data"),
        Map.entry(21, "FTP"),
        Map.entry(22, "SSH"),
        Map.entry(23, "Telnet"),
        Map.entry(25, "SMTP"),
        Map.entry(53, "DNS"),
        Map.entry(80, "HTTP"),
        Map.entry(110, "POP3"),
        Map.entry(143, "IMAP"),
        Map.entry(443, "HTTPS"),
        Map.entry(3306, "MySQL"),
        Map.entry(8080, "HTTP-Alt")
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter target IP or hostname(eg: scanme.nmap.org): ");
        String host = scanner.nextLine();
        System.out.print("Enter start port: ");
        int startPort = scanner.nextInt();
        System.out.print("Enter end port: ");
        int endPort = scanner.nextInt();

        System.out.println("\nScanning " + host + " from port " + startPort + " to " + endPort + "...\n");

        List<Integer> openPorts = new ArrayList<>();

        try (PrintWriter writer = new PrintWriter(new FileWriter("scan_results.txt"))) {
            for (int port = startPort; port <= endPort; port++) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(host, port), 200);
                    String service = serviceNames.getOrDefault(port, "Unknown Service");
                    System.out.println("Port " + port + " is open (" + service + ")");
                    writer.println("Port " + port + " is open (" + service + ")");
                    openPorts.add(port);
                } catch (IOException ignored) {
                    // Port is closed or unreachable — do nothing
                }
            }

            writer.println("\n--- Scan Summary ---");
            if (openPorts.isEmpty()) {
                writer.println("No open ports found in the range " + startPort + " to " + endPort);
            } else {
                writer.println("Total open ports found: " + openPorts.size());
                for (int port : openPorts) {
                    String service = serviceNames.getOrDefault(port, "Unknown Service");
                    writer.println("- Port " + port + " (" + service + ")");
                }
            }

        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("\nScan complete.");
        if (openPorts.isEmpty()) {
            System.out.println("No open ports found in the given range.");
        } else {
            System.out.println(openPorts.size() + " open port(s) found. See scan_results.txt for details.");
        }
    }
}
