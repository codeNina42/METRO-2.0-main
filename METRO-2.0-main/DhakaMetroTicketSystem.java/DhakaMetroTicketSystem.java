import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// ================= MAIN APP =================
public class DhakaMetroTicketSystem extends JFrame {

    // Hard-coded admin credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "metro123";

    // Data
    private java.util.List<Station> stations = new ArrayList<>();
    private java.util.List<Ticket> tickets = new ArrayList<>();

    // Ticket UI fields
    private JComboBox<Station> comboFrom;
    private JComboBox<Station> comboTo;
    private JComboBox<String> comboTicketType;
    private JTextField txtPassengerName;
    private JLabel lblFare;
    private DefaultTableModel ticketTableModel;

    // Card layout: LOGIN  -> MAIN
    private CardLayout cardLayout;
    private JPanel rootPanel;

    public DhakaMetroTicketSystem() {
        setTitle("Dhaka Metro Rail Ticket Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initStations();

        // -------- Background image (CHANGE the file name if needed) --------
        Image bgImage = new ImageIcon("metro_bg.jpg").getImage();
        BackgroundPanel bgPanel = new BackgroundPanel(bgImage);
        bgPanel.setLayout(new BorderLayout());
        setContentPane(bgPanel);
        // -------------------------------------------------------------------

        // Root panel with cards (login + main)
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);
        rootPanel.setOpaque(false);  // let background show through

        JPanel loginPanel = createLoginPanel();
        JPanel mainPanel = createMainPanel();   // tabs

        rootPanel.add(loginPanel, "LOGIN");
        rootPanel.add(mainPanel, "MAIN");

        bgPanel.add(rootPanel, BorderLayout.CENTER);

        // Show login first
        cardLayout.show(rootPanel, "LOGIN");
    }

    // ---------------- STATION DATA ----------------
    private void initStations() {
        String[] names = {
                "Uttara North",
                "Uttara Center",
                "Uttara South",
                "Pallabi",
                "Mirpur-11",
                "Mirpur-10",
                "Kazipara",
                "Shewrapara",
                "Agargaon",
                "Bijoy Sarani",
                "Farmgate",
                "Karwan Bazar",
                "Shahbag",
                "Dhaka University",
                "Bangladesh Secretariat",
                "Motijheel",
                "Kamalapur"
        };
        for (int i = 0; i < names.length; i++) {
            stations.add(new Station(i + 1, names[i], i));
        }
    }

    // ---------------- LOGIN PANEL ----------------
    private JPanel createLoginPanel() {
        // Semi-transparent white panel
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Stronger transparency (alpha 140 → more background visible)
                g.setColor(new Color(255, 255, 255, 140));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Dhaka Metro Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(title, gbc);

        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogin, gbc);

        // Action for login button and Enter key in password field
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword());

                if (ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass)) {
                    JOptionPane.showMessageDialog(DhakaMetroTicketSystem.this,
                            "Login successful. Welcome, admin!");
                    cardLayout.show(rootPanel, "MAIN");
                } else {
                    JOptionPane.showMessageDialog(DhakaMetroTicketSystem.this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        btnLogin.addActionListener(loginAction);
        txtPass.addActionListener(loginAction);

        return panel;
    }

    // ---------------- MAIN PANEL (TABS) ----------------
    private JPanel createMainPanel() {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Very light white with alpha 120 across the whole tab area
                g.setColor(new Color(255, 255, 255, 120));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        outer.setOpaque(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.setBackground(new Color(0, 0, 0, 0));

        tabs.addTab("Issue Ticket", createIssueTicketPanel());
        tabs.addTab("Tickets List", createTicketsListPanel());

        outer.add(tabs, BorderLayout.CENTER);
        return outer;
    }

    // ---------------- ISSUE TICKET PANEL ----------------
    private JPanel createIssueTicketPanel() {
        // Transparent container, with inner “glass” form panel
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Glass effect: white with alpha 160
                g.setColor(new Color(255, 255, 255, 160));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder("Issue New Ticket"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboFrom = new JComboBox<>(stations.toArray(new Station[0]));
        comboTo = new JComboBox<>(stations.toArray(new Station[0]));
        comboTicketType = new JComboBox<>(new String[]{"Regular", "Student (50% Off)"});
        txtPassengerName = new JTextField(20);
        lblFare = new JLabel("Fare: - BDT");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassengerName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("From Station:"), gbc);
        gbc.gridx = 1;
        form.add(comboFrom, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("To Station:"), gbc);
        gbc.gridx = 1;
        form.add(comboTo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Ticket Type:"), gbc);
        gbc.gridx = 1;
        form.add(comboTicketType, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Calculated Fare:"), gbc);
        gbc.gridx = 1;
        form.add(lblFare, gbc);

        row++;
        JButton btnCalc = new JButton("Calculate Fare");
        JButton btnIssue = new JButton("Issue Ticket");
        JButton btnClear = new JButton("Clear");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnCalc);
        btnPanel.add(btnIssue);
        btnPanel.add(btnClear);

        gbc.gridx = 1; gbc.gridy = row;
        form.add(btnPanel, gbc);

        btnCalc.addActionListener(e -> calculateFareAndShow());
        btnIssue.addActionListener(e -> issueTicket());
        btnClear.addActionListener(e -> clearForm());

        JLabel info = new JLabel(
                "Fare rule: Base 20 BDT + 5 BDT per extra station. Student: 50% discount."
        );
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        container.add(form, BorderLayout.NORTH);
        container.add(info, BorderLayout.SOUTH);
        return container;
    }

    // ---------------- TICKETS LIST PANEL ----------------
    private JPanel createTicketsListPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Slight tint so table floats above background
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);

        ticketTableModel = new DefaultTableModel(
                new String[]{"ID", "Passenger", "Type", "From", "To", "Stops", "Fare (BDT)", "Time"},
                0
        );
        JTable table = new JTable(ticketTableModel);
        table.setOpaque(false);
        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createTitledBorder("Issued Tickets"));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ---------------- LOGIC: FARE & TICKETS ----------------
    private void calculateFareAndShow() {
        Station from = (Station) comboFrom.getSelectedItem();
        Station to = (Station) comboTo.getSelectedItem();
        String type = (String) comboTicketType.getSelectedItem();

        if (from == null || to == null) return;

        if (from.getIndex() == to.getIndex()) {
            JOptionPane.showMessageDialog(this,
                    "From and To station cannot be the same.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE);
            lblFare.setText("Fare: - BDT");
            return;
        }

        int stops = Math.abs(from.getIndex() - to.getIndex());
        double fare = calculateFare(stops, type);

        lblFare.setText(String.format("Fare: %.2f BDT (%d stops)", fare, stops));
    }

    private double calculateFare(int stops, String ticketType) {
        if (stops <= 0) stops = 1;

        double base = 20.0;
        double perStop = 5.0;
        double fare = base + (stops - 1) * perStop;

        if (ticketType != null && ticketType.startsWith("Student")) {
            fare = fare * 0.5;
        }
        return fare;
    }

    private void issueTicket() {
        String passenger = txtPassengerName.getText().trim();
        Station from = (Station) comboFrom.getSelectedItem();
        Station to = (Station) comboTo.getSelectedItem();
        String type = (String) comboTicketType.getSelectedItem();

        if (passenger.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Passenger name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both From and To stations.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (from.getIndex() == to.getIndex()) {
            JOptionPane.showMessageDialog(this,
                    "From and To station cannot be the same.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stops = Math.abs(from.getIndex() - to.getIndex());
        double fare = calculateFare(stops, type);

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Ticket ticket = new Ticket(passenger, type, from, to, stops, fare, time);
        tickets.add(ticket);

        ticketTableModel.addRow(new Object[]{
                ticket.getId(),
                ticket.getPassengerName(),
                ticket.getTicketType(),
                ticket.getFrom().getName(),
                ticket.getTo().getName(),
                ticket.getStops(),
                String.format("%.2f", ticket.getFare()),
                ticket.getIssueTime()
        });

        JOptionPane.showMessageDialog(this,
                "Ticket issued successfully!\nFare: " + String.format("%.2f BDT", fare),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        clearForm();
    }

    private void clearForm() {
        txtPassengerName.setText("");
        comboFrom.setSelectedIndex(0);
        comboTo.setSelectedIndex(stations.size() - 1);
        comboTicketType.setSelectedIndex(0);
        lblFare.setText("Fare: - BDT");
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DhakaMetroTicketSystem app = new DhakaMetroTicketSystem();
            app.setVisible(true);
        });
    }
}

// ================= BACKGROUND PANEL =================
class BackgroundPanel extends JPanel {
    private final Image bg;

    public BackgroundPanel(Image bg) {
        this.bg = bg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bg != null) {
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

// ================= MODEL CLASSES =================
class Station {
    private int id;
    private String name;
    private int index;

    public Station(int id, String name, int index) {
        this.id = id;
        this.name = name;
        this.index = index;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getIndex() { return index; }

    @Override
    public String toString() {
        return name;
    }
}

class Ticket {
    private static int counter = 1;

    private int id;
    private String passengerName;
    private String ticketType;
    private Station from;
    private Station to;
    private int stops;
    private double fare;
    private String issueTime;

    public Ticket(String passengerName, String ticketType,
                  Station from, Station to, int stops, double fare, String issueTime) {
        this.id = counter++;
        this.passengerName = passengerName;
        this.ticketType = ticketType;
        this.from = from;
        this.to = to;
        this.stops = stops;
        this.fare = fare;
        this.issueTime = issueTime;
    }

    public int getId() { return id; }
    public String getPassengerName() { return passengerName; }
    public String getTicketType() { return ticketType; }
    public Station getFrom() { return from; }
    public Station getTo() { return to; }
    public int getStops() { return stops; }
    public double getFare() { return fare; }
    public String getIssueTime() { return issueTime; }
}
