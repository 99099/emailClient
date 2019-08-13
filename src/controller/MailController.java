package controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.AppData;
import model.Mail;
import model.POPprocess;
import model.SMTPProcess;

public class MailController extends Application{
	static BufferedReader br = null;
	static PrintWriter pw = null;
	static Socket popSocket = null;
	static Socket smtpSocket = null;
	@FXML TextField receiver;
	@FXML TextField send_subject;
	@FXML TextArea send_content;
	@FXML Tab wirter_letters;
	@FXML Tab mails_box;
	@FXML Tab tab1;
	@FXML TextField sender;
	@FXML TextField date;
	@FXML TextField receive_subject;
	@FXML TextArea receive_content;
	@FXML Button sendMail;
	@FXML Button delete;
	@FXML TabPane mailBox;
//	@FXML Button deleteButton;
	
	@FXML
	public void sendMail() {
		SMTPProcess smtp = new SMTPProcess(AppData.smtpServer, AppData.user, 
				AppData.pass, smtpSocket, br, pw);
		Date now = new Date();
		String date = now.toString();
		String subject = send_subject.getText();
		String content = send_content.getText();
		String to = receiver.getText();
		boolean isMailedSuccessfully = false;
		if(to.contains(";")) {
			StringTokenizer st = new StringTokenizer(to, ";");
			while(st.hasMoreTokens()) {
				Mail newMail = new Mail(AppData.user, st.nextToken(), date, subject, content);
				isMailedSuccessfully = smtp.sendMail(newMail);
			}
		}
		else {
			Mail newMail = new Mail(AppData.user, to, date, subject, content);
			isMailedSuccessfully = smtp.sendMail(newMail);
		}
		
		if(!isMailedSuccessfully) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setContentText("发送失败！！");
			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("发送成功！！");
			alert.showAndWait();
		}
	}
	
	
	@FXML
	public void getMails() {
		mailBox.getTabs().clear();
		final POPprocess pop = new POPprocess(AppData.popServer, popSocket, AppData.user, 
				AppData.pass, br, pw);
		Vector<Mail> myMails = pop.getAllMails();
		for(int i=0; i<myMails.size(); i++) {
			Mail tempMail = myMails.get(i);
			Tab temp = new Tab("mail"+(i+1));
			temp.setId("mail"+i);
			Label senderLabel = new Label("来自");
			senderLabel.setFont(Font.font(24));
			TextField senderText = new TextField("unknow");
			senderText.setText(tempMail.from);
			
			Label dateLabel = new Label("日期");
			dateLabel.setFont(Font.font(24));
			TextField dateText = new TextField(tempMail.date);
			
			Label subjectLabel = new Label("主题");
			subjectLabel.setFont(Font.font(24));
			TextField subjectText = new TextField(tempMail.subject);
			
			Label contentLabel = new Label("内容");
			contentLabel.setFont(Font.font(24));
			TextArea contentArea = new TextArea(tempMail.content);
			
			GridPane tempGridPane = new GridPane();
			tempGridPane.setLayoutY(-1);
			tempGridPane.setPrefHeight(260);
			tempGridPane.setPrefWidth(582);
			
			ColumnConstraints column1 = new ColumnConstraints();
			column1.setHgrow(Priority.SOMETIMES);
			column1.setMaxWidth(294.4);
			column1.setMinWidth(100.0);
			column1.setPrefWidth(130);
			
			ColumnConstraints column2 = new ColumnConstraints();
			column2.setHgrow(Priority.SOMETIMES);
			column2.setMaxWidth(540.8);
			column2.setMinWidth(100.0);
			column2.setPrefWidth(450);
			
			RowConstraints row1 = new RowConstraints();
			row1.setVgrow(Priority.SOMETIMES);
			row1.setMaxHeight(75.6);
			row1.setMinHeight(10.0);
			row1.setPrefHeight(30.0);
			RowConstraints row2 = new RowConstraints();
			row2.setVgrow(Priority.SOMETIMES);
			row2.setMaxHeight(75.6);
			row2.setMinHeight(10.0);
			row2.setPrefHeight(30.0);
			RowConstraints row3 = new RowConstraints();
			row3.setVgrow(Priority.SOMETIMES);
			row3.setMaxHeight(149.2);
			row3.setMinHeight(10.0);
			row3.setPrefHeight(30.0);
			RowConstraints row4 = new RowConstraints();
			row4.setVgrow(Priority.SOMETIMES);
			row4.setMaxHeight(185.6);
			row4.setMinHeight(10.0);
			row4.setPrefHeight(180.0);
			
			GridPane.setHalignment(senderLabel, HPos.CENTER);
			tempGridPane.add(senderLabel, 0, 0);
			
			tempGridPane.add(senderText, 1, 0);
			GridPane.setHalignment(senderText, HPos.CENTER);
			tempGridPane.add(dateLabel, 0, 1);
			GridPane.setHalignment(dateLabel, HPos.CENTER);
			tempGridPane.add(dateText, 1, 1);
			GridPane.setHalignment(dateText, HPos.CENTER);
			tempGridPane.add(subjectLabel, 0, 2);
			GridPane.setHalignment(subjectLabel, HPos.CENTER);
			tempGridPane.add(subjectText, 1, 2);
			GridPane.setHalignment(subjectText, HPos.CENTER);
			tempGridPane.add(contentLabel, 0, 3);
			GridPane.setHalignment(contentLabel, HPos.CENTER);
			tempGridPane.add(contentArea, 1, 3);
			GridPane.setHalignment(contentArea, HPos.CENTER);
			
			final Button deleteButton = new Button("删除");
			deleteButton.setId("delete"+i);
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					int length = deleteButton.getId().length();
					String str = deleteButton.getId().substring(6, length);
					boolean isSucessfullyDelete = pop.deleteMail(Integer.parseInt(str)+1);
					if(isSucessfullyDelete) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText("删除成功，延迟后刷新即删除！");
						alert.showAndWait();
					}else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setContentText("删除失败！");
						alert.showAndWait();
					}
					
				}	
			});
			AnchorPane wholePane = new AnchorPane(tempGridPane, deleteButton);
			wholePane.setMinWidth(0);
			wholePane.setMinHeight(0);
			wholePane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			wholePane.setPrefHeight(180);
			wholePane.setPrefWidth(200);
			
			AnchorPane.setBottomAnchor(deleteButton, (double) 100);
			deleteButton.setLayoutY(291);
			deleteButton.setLayoutX(522);
			deleteButton.setMnemonicParsing(false);
			deleteButton.setFont(Font.font(18));
			
			temp.setContent(wholePane);
			
			mailBox.getTabs().add(temp);

		}
	}
	
	@FXML
	public void getMail() {

		return ;
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(getClass().getResource("/view/MailClient.fxml"));
		Scene scene = new Scene(root, 600, 400);
	     primaryStage.setTitle("FXML Welcome");
	     primaryStage.setScene(scene);
	     primaryStage.show();
	}
}
