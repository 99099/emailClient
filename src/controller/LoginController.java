package controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AppData;
import model.SMTPProcess;

public class LoginController {
	@FXML
	TextField userID;
	@FXML
	PasswordField password;
	@FXML
	Button login;
//	boolean flag = true;
	
	@FXML
	public boolean login() {
		AppData.user = this.userID.getText();
		AppData.pass = this.password.getText();
		BufferedReader br = null;
		PrintWriter pw = null;
		Socket socket = null;
		
		int index1 = AppData.user.indexOf("@");
		int index2 = AppData.user.indexOf(".");
		if(index1==-1 || index2 == -1) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setContentText("邮箱格式错误！！");
			alert.showAndWait();
			return false;
		}
		String str = AppData.user.substring(index1 + 1);
		AppData.popServer = "pop." + str;
		AppData.smtpServer = "smtp." + str;
		SMTPProcess smtp = new SMTPProcess(AppData.smtpServer, AppData.user, AppData.pass, socket, br, pw);
		if (!smtp.SMTPLogin()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setContentText("用户名或密码错误");
			alert.showAndWait();
			return false;
		}
		else {
			//切换页面
			Runnable newThread = new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					MailController mailController = new MailController();
					try {
						mailController.start(new Stage());			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			newThread.run();
		}
		return true;
	}
}
