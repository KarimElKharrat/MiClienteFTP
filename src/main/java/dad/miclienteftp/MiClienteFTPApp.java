package dad.miclienteftp;

import dad.miclienteftp.controllers.RootController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MiClienteFTPApp extends Application {

	private RootController rootController;
	
	public static Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

		MiClienteFTPApp.primaryStage = primaryStage;
		
		rootController = new RootController();
		
		Scene scene = new Scene(rootController.getView());

		primaryStage.setTitle("Mi cliente FTP");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
