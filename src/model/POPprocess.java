package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class POPprocess {
	String host;
	int port = 110;
	Socket popSocket;
	String user;
	String passwd;
	BufferedReader reader;
	PrintWriter writer;
	
//	static String sender = "13060013292@163.com";
//	static String password = "ha1234"
	public POPprocess(String server, Socket socket, String user, String passwd, BufferedReader br, PrintWriter pw) {
		this.host = server;
		this.user = user;
		this.popSocket = socket;
		this.passwd = passwd;
		this.reader = br;
		this.writer = pw;
		POPLogin();
	}
	public Vector<String> getMessages(String command){
		Vector<String> messages = new Vector<String>();
		try {
			writer.println(command);
//			System.out.println("Client==> "+command);
			writer.flush();
			String res = reader.readLine();
//			System.out.println("Server==> "+res);
			messages.add(res);
			if(!res.startsWith("+OK")) {
				return messages;
			}
			String buff = null;
			boolean isEnd = false;
			while(!isEnd) {
				buff = reader.readLine();
				messages.add(buff);
//				System.out.println("Server==> "+buff);
				if(buff.equals(".")) {
					isEnd = true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messages;
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
	//POP服务器登陆
	public void POPLogin() {
		String res = null;
		try {
			this.popSocket = new Socket(this.host, this.port);
			this.reader = new BufferedReader(new InputStreamReader(popSocket.getInputStream()));
			this.writer = new PrintWriter(popSocket.getOutputStream(),true);
			res = this.reader.readLine();
//			System.out.println("Server==> "+res);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!res.startsWith("+OK")) {
			try {
				popSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("连接失败!!!!");
			return ;
		}
		getOneMessage("user "+this.user);
		getOneMessage("pass "+this.passwd);
		
	}
	//得到邮件数量
	public int getMailsNumber() {
		Vector<String> messages = new Vector<String>();
		messages = getMessages("list");
		if(!messages.get(0).startsWith("+OK")) {
			return -1;
		}
		else {
			return messages.size()-2;
		}
	}
	
	public Vector<Mail> getAllMails(){
		Vector<Mail> mails = new Vector<Mail>();
		int mailsNum = getMailsNumber();
		for(int i=1; i<=mailsNum; i++) {
			Mail tempMail = new Mail(getMessages("retr "+i));
			mails.add(tempMail);
		}
		return mails;
	}
	//删除邮件
	public boolean deleteMail(int i) {
		String message = getOneMessage("dele "+i);
		if(message.startsWith("+OK")) {
			return true;
		}
		return false;
	}
	
	public boolean quitPopServer() {
		String message = getOneMessage("quit");
		if(!message.startsWith("+OK")) {
			return false; 
		}
		return true;
	}
//	public static void main(String[] args) {
//		String server = "pop.163.com";
//		String sender = "13060013292@163.com"; 
//		String password = "ha1234";
//		BufferedReader br =null;
//		PrintWriter pw=null;
//		Socket socket = null;
//		POPprocess test = new POPprocess(server, socket, sender, password, br, pw);
//		int mailsNumber = test.getMailsNumber();
//		System.out.println(mailsNumber);
//		Vector<Mail> mails = new  Vector<Mail>();
//		mails = test.getAllMails();
//		for(int i=0; i<mailsNumber; i++) {
//			System.out.println(mails.get(i).toString());
//		}

}
