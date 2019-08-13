package model;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
		 Scene scene = new Scene(root, 600, 400);
	     stage.setTitle("FXML Welcome");
	     stage.setScene(scene);
	     stage.show();
	     
	}
	public static void main(String [] args) {
		launch(args);
	}
}
