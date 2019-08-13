package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SMTPProcess {
	String host;
	int port = 25;
	String sender;
	String password;
	Socket smtpSocket;
	BufferedReader reader;
	PrintWriter writer;
	public SMTPProcess(String host, String sender, String password, Socket socket, BufferedReader br, PrintWriter pw) {
		this.host  = host;
		this.sender = sender;
		this.password = password;
		this.smtpSocket = socket;
		this.reader = br;
		this.writer = pw;
		SMTPLogin();
	}
    public boolean SMTPLogin() {
		// TODO Auto-generated method stub
		try {
			smtpSocket = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
			writer = new PrintWriter(smtpSocket.getOutputStream(), true);
			String res = reader.readLine();
//			System.out.println("Server==> "+res);
			if(!res.startsWith("220")) {
				System.out.println("连接失败！！！");
				return false;
			}
			getOneMessage("helo "+sender);
			getOneMessage("auth login");
			String user = Base64.getEncoder().encodeToString(sender.substring(0, sender.indexOf("@")).getBytes()); 
			String pass = Base64.getEncoder().encodeToString(password.getBytes());
			String userRes = getOneMessage(user);
			if(!userRes.startsWith("334")) {
				return false;
			}
			String passRes = getOneMessage(pass);
			if(!passRes.startsWith("235")) {
				return false;
			}
			else {
				return true;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("无法解析服务器名称！！！");
			alert.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
    public void quitSMTP() {
    	getOneMessage("quit");
    	try {
			smtpSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean sendMail(Mail mail) {
    	getOneMessage("mail from:<"+mail.from+">");
    	getOneMessage("rcpt to:<"+mail.to+">");
    	String res = getOneMessage("data");
    	if(res.charAt(0) != '3') {
    		return false;
    	} 
        writer.println("From: <" + mail.from+">");
        writer.println("To: <"+mail.to+">");
        writer.println("Subject: " + mail.subject);
        writer.println("Content-Type: text/plain;");
        writer.println();
        writer.println(mail.content);
    	String res1 = getOneMessage(".");
    	if(!res1.startsWith("250")) {
    		return false;
    	}
    	return true;
    }
    public String getOneMessage(String command) {
		
		String res = null;
		try {
			writer.println(command);
//			System.out.println("Client==> "+command);
			res = reader.readLine();
//			System.out.println("Server==> "+res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}