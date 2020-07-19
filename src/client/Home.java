package client;

import common.GUI;
import common.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class Home extends GUI {

    private JLabel title;
    private ServerSocket server;
    private final Socket connection;
    private final String connection_info;
    // buttons to get connected users and start talk
    private JButton jb_get_connected, jb_start_talk; 
    // online user list
    private JList jlist; 
    // scroll down conversation if needed
    private JScrollPane scroll; 

    private final ArrayList<String> connected_users;
    private final ArrayList<String> opened_chats;
    private final Map<String, ClientListener> connected_listeners;

    // credentials verification
    public Home(Socket connection, String connection_info) {
        super("Chat | Home");
        title.setText("< User : " + connection_info.split(":")[0] + " >");
        this.connection = connection;
        this.setTitle("Home | " + connection_info.split(":")[0]);
        this.connection_info = connection_info;
        connected_users = new ArrayList<String>();
        opened_chats = new ArrayList<String>();
        connected_listeners = new HashMap<String, ClientListener>();
        startServer(this, Integer.parseInt(connection_info.split(":")[2]));
    }

    @Override
    protected void initComponents() {
        title = new JLabel();
        jb_get_connected = new JButton("Update Users");
        jlist = new JList();
        scroll = new JScrollPane(jlist);
        jb_start_talk = new JButton("Start Chat");
    }

    @Override
    protected void configComponents() {
        this.setLayout(null);
        this.setMinimumSize(new Dimension(480, 480));
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.WHITE);

        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(10, 10, 300, 40);
        title.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        jb_get_connected.setBounds(320, 10, 150, 40);
        // disable focus on button
        jb_get_connected.setFocusable(false); 

        // wraps all component
        jlist.setBorder(BorderFactory.createTitledBorder("Online Users")); 
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scroll.setBounds(10, 60, 460, 320);
        
        jb_start_talk.setBounds(10, 390, 460, 40);
        jb_start_talk.setFocusable(false);
        
        // shows vertical scroll bar when too much text appears
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // controls horizontal scroll
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // no border on scroll to avoid dispute with "JList" border
        scroll.setBorder(null); 
    }

    // insert components inside frame
    @Override
    protected void insertComponents() {
        this.add(title);
        this.add(jb_get_connected);
        this.add(scroll);
        this.add(jb_start_talk);
    }

    @Override
    protected void insertActions() {
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Closed connection...");
                Utils.sendMessage(connection, "quit");
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        jb_get_connected.addActionListener(event -> getConnectedUsers());
        jb_start_talk.addActionListener(event -> openChat());
    }

    @Override
    protected void start() {
        this.pack();
        this.setVisible(true);
    }

    private void getConnectedUsers() {
        Utils.sendMessage(connection, "GET_CONNECTED_USERS");
        String response = Utils.receiveMessage(connection);
        jlist.removeAll();
        connected_users.clear();
        for (String user : response.split(";")) {
            if (!user.equals(connection_info)) {
                connected_users.add(user);
            }

        }
        jlist.setListData(connected_users.toArray());
    }

    private void openChat() {
        int index = jlist.getSelectedIndex();
        if (index != -1) {
            String value = jlist.getSelectedValue().toString();
            String[] splited = value.split(":");
            if (!opened_chats.contains(value)) {
                try {
                    Socket socket = new Socket(splited[1], Integer.parseInt(splited[2]));
                    // send message to other side of chat and open windows
                    Utils.sendMessage(socket, "OPEN_CHAT;" + connection_info); 
                    ClientListener cl = new ClientListener(this, socket);
                    cl.setChat(new Chat(this, socket, value, this.connection_info.split(":")[0]));
                    cl.setChatOpen(true);
                    connected_listeners.put(value, cl);
                    opened_chats.add(value);
                    new Thread(cl).start();

                } catch (IOException ex) {
                }
            }

        }
    }

    private void startServer(Home home, int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    server = new ServerSocket(port);
                    System.out.println("Client server started on port " + port + " ...");
                    while (true) {
                        Socket client = server.accept();
                        ClientListener cl = new ClientListener(home, client);
                        new Thread(cl).start();
                    }
                } catch (IOException ex) {
                    System.err.println("[ERROR:startServer] -> " + ex.getMessage());
                }
            }
        }.start();
    }
    
    

    public ArrayList<String> getOpened_chats() {
        return opened_chats;
    }

    public String getConnection_info() {
        return connection_info;
    }

    public Map<String, ClientListener> getConnected_listeners() {
        return connected_listeners;
    }

}
