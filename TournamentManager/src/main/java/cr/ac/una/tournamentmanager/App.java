package cr.ac.una.tournamentmanager;

import cr.ac.una.tournamentmanager.Util.FlowController;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FlowController.getInstance().InitializeFlow(stage, null);
        scene = new Scene(loadFXML("PrincipalView"), 640, 480);
        MFXThemeManager.addOn(scene,Themes.DEFAULT,Themes.LEGACY);
        stage.setScene(scene);
        stage.setMinHeight(500.0);//experimento
        stage.setMinWidth(720.0);//experimento
        stage.show();
    }

   public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}