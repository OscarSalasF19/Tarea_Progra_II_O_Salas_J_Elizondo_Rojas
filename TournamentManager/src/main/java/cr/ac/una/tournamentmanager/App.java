package cr.ac.una.tournamentmanager;

import com.github.sarxos.webcam.Webcam;
import cr.ac.una.tournamentmanager.Controller.TeamsManagerController;
import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.model.InfoManager;
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
        scene = new Scene(loadFXML("PrincipalView"), 800, 610);
        MFXThemeManager.addOn(scene,Themes.DEFAULT,Themes.LEGACY);
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(630.0);
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
        InfoManager manager = new InfoManager();
        manager.LoadInfo();
        launch();
        manager.saveInfo();
        Webcam camera = Webcam.getDefault();
        if(camera.isOpen() && camera != null){
            camera.close();
        }
    }

}