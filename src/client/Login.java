package client;

import common.GUI;
import common.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import server.Server;

public class Login extends GUI {

    private JButton jb_login;
    private JLabel jl_user, jl_port, jl_title;
    private JTextField jt_user, jt_port;

    public Login() {
        super("Login");
    }

    @Override
    protected void initComponents() {
        //for icon usage
        jl_title = new JLabel();
        jb_login = new JButton("Login");
        jl_user = new JLabel("Username", SwingConstants.CENTER);
        jl_port = new JLabel("4 Digit Port", SwingConstants.CENTER);
        jt_user = new JTextField();
        jt_port = new JTextField();
    }

    @Override
    protected void configComponents() {
        //disables JFrame default layout manager (FlowLayout)
        this.setLayout(null); 
        this.setMinimumSize(new Dimension(400, 300));
        //set window to center
        this.setLocationRelativeTo(null); 
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);
        
        // "Title" size
        jl_title.setBounds(50, 10, 300, 100);
        ImageIcon icon = new ImageIcon("logo.png");
        jl_title.setIcon(new ImageIcon(icon.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH)));

        // "Enter" button size
        jb_login.setBounds(10, 210, 380, 40);

        // User label position
        jl_user.setBounds(10, 110, 100, 40);
        jl_user.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Port label position
        jl_port.setBounds(10, 160, 100, 40);
        jl_port.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // User input text position
        jt_user.setBounds(120, 110, 270, 40);
        jt_user.setBorder(BorderFactory.createCompoundBorder
            (BorderFactory.createLineBorder(Color.GRAY), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
           
        // Port input text position
        jt_port.setBounds(120, 160, 270, 40);
        jt_port.setBorder(BorderFactory.createCompoundBorder
            (BorderFactory.createLineBorder(Color.GRAY), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    // insert components inside frame
    @Override
    protected void insertComponents() {
        this.add(jl_title);
        this.add(jb_login);
        this.add(jl_port);
        this.add(jl_user);
        this.add(jt_port);
        this.add(jt_user);
    }

    @Override
    protected void insertActions() {
        jb_login.addActionListener(event -> {
            Socket connection;
            try {
                String username = jt_user.getText();
                int port = Integer.parseInt(jt_port.getText());
                //clean text
                jt_user.setText("");
                jt_port.setText("");
                connection = new Socket(Server.HOST, Server.PORT);
                String request = username + ":" + connection.getLocalAddress().getHostAddress() + ":" + port;
                Utils.sendMessage(connection, request);
                if (Utils.receiveMessage(connection).toLowerCase().equals("sucess")) {
                    new Home(connection, request);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "A user is already connected with this username, or in this port. Try again!");
                }
            } catch (IOException ex) {
                System.err.println("[ERROR:login] -> " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error connecting. Verify if server is running!");
            }

        });
    }

    @Override
    protected void start() {
        this.pack();
        this.setVisible(true);
    }

    // run interface
    public static void main(String[] args) {
        Login login = new Login();
    }

}
