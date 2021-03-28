package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
		Parent root = loader.load();

		Controller controller = loader.getController();

		Scene scene = new Scene(root, 600, 380);
		stage.setTitle("Welcome");
		stage.setScene(scene);
		stage.getIcons().add(new Image("file:DNAIcon.jpg"));
		stage.setResizable(false);
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
