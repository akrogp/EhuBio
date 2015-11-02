package es.ehubio.panalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class PAnalyzerGui extends Application {
	@Override
	public void start(Stage stage) {
		try {
			MainModel model = new MainModel();
			MainController controller = new MainController(model,stage);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
			loader.setController(controller);
			VBox root = (VBox)loader.load();
			Scene scene = new Scene(root,640,480);
			//setUserAgentStylesheet(STYLESHEET_CASPIAN);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());			
			stage.setTitle(String.format("%s - UPV/EHU", MainModel.SIGNATURE));
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
