import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Handler;

public class ClientFrame extends JFrame{
    public ClientFrame(){
        setSize(500,500);
        setTitle("Chat Client");
        addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent We){
                System.exit(0);
            }
        });

        add(new ClientPanel(this), BorderLayout.CENTER);
        setVisible(true);
    }


    public static void main(String[] args){
        new ClientFrame();
    }
}

class ClientPanel extends Panel implements ActionListener, Runnable{
    TextField tf;
    Boolean serverConnected=false;
    ClientFrame container;
    Label unameMessage;
    TextField uname;
    TextArea ta;
    List list;
    Button connect, disconnect;
    Socket socketToServer;
    PrintWriter pw;
    BufferedReader br;
    Thread t;
    String userName;

    public ClientPanel(ClientFrame clientF){
        container=clientF;
        setLayout(new BorderLayout());
        tf = new TextField();
        ta = new TextArea();
        list = new List();
        unameMessage = new Label("Please Enter Username");
        uname= new TextField(10);
        connect = new Button("Connect");
        disconnect = new Button("Disconnect");
        Panel bPanel = new Panel();
        bPanel.add(unameMessage);
        bPanel.add(uname);
        bPanel.add(connect);
        bPanel.add(disconnect);

        uname.addActionListener(this); // for user_connection
        list.addActionListener(this); // for PM
        tf.addActionListener(this); // for writing messages
        ta.setEditable(false); // makes it so message can not be erased
        connect.addActionListener(this); // for connection to server after user_name is entered
        disconnect.addActionListener(this); // to disconnect from server once user wants to leave
        disconnect.setEnabled(false); // makes it so that user must connect before using disconnect.
        add(tf, BorderLayout.NORTH);
        add(ta, BorderLayout.CENTER);
        add(list, BorderLayout.EAST);
        add(bPanel, BorderLayout.SOUTH);


    }


    public void actionPerformed(ActionEvent ae) {

        if(list.getSelectedItem() != null) { // used to send Private message
            String temp = tf.getText();
            tf.setText("");
            pw.println(userName + " : " + "PRIVATE_MESSAGE " + userName + " to " + list.getSelectedItem() + " " + temp);
            list.deselect(list.getSelectedIndex());
        }

        if(ae.getSource() == connect) {

            userName = uname.getText();
            container.setTitle(userName+"'s Chat Window" + "");
            if(userName.length() == 0)
            {
                JOptionPane.showMessageDialog(null, "Username must be filled");
                container.setTitle("Chat Client");


            }
            else{
                try	{
                    socketToServer = new Socket("127.0.0.1", 3000);
                    pw = new PrintWriter(new OutputStreamWriter(socketToServer.getOutputStream()), true);
                    br = new BufferedReader(new InputStreamReader (socketToServer.getInputStream()));
                }catch(UnknownHostException uhe){
                    System.out.println(uhe.getMessage());
                }catch(IOException ioe){
                    System.out.println(ioe.getMessage());
                }
                t = new Thread(this);
                t.start();
                connect.setEnabled(false);
                disconnect.setEnabled(true);
                serverConnected= true;
                pw.println(userName + " : has connected to the chat room.");
                uname.setText("");
            }
        }


        else if (ae.getSource() == disconnect) {
            if(serverConnected)
                try {
                    pw.println(userName + " : has disconnected from the chat room.");
                    socketToServer.close();
                    System.out.println(" Disconnected");
                    ta.append(userName + " Disconnected\n");
                    container.setTitle(userName+"'s Chat Window" + "");
                    list.removeAll();
                    ta.setText("");
                    connect.setEnabled(true);
                    disconnect.setEnabled(false);
                    serverConnected=false;

                }
                catch (UnknownHostException uhe){
                    System.out.println(uhe.getMessage());
                }
                catch (IOException IOE){
                    System.out.println(IOE.getMessage());
                }

        }
        else if(ae.getSource() == tf && list.getSelectedItem() == null)
        {
            String temp = tf.getText();

            if(!serverConnected)
            {
                JOptionPane.showMessageDialog(null, "You must first connected with a username to chat");

            }
            else if (temp.length() > 1)
            {
                pw.println(userName+": "+temp);
                tf.setText("");
            }
        }

    }


    public void run(){
        for(;;){
            try{

                String temp = br.readLine();
                String[] splitted = temp.trim().split("\\s*,\\s*"); //reads message and splits it into a list to add to the userList
                ta.append(temp + "\n");
                if(temp.contains(",")) {
                    list.removeAll();
                }
                System.out.println("list before for loop"+list);
                for (int i=0; i<splitted.length; i++) {
                    if(!splitted[i].contains(":")) {
                        list.add(splitted[i]);
                        System.out.println("list in for loop"+list);
                    }

                }

            }catch(IOException ioe){
                System.out.println(ioe.getMessage());

            }

        }
    }

}