package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class PrincipalController extends Controller implements Initializable{

    @FXML
    private MFXButton btnAnadirDeporte;

    @FXML
    private MFXButton btnAnadirEquipo;

    @FXML
    private MFXButton btnInformacion;

    @FXML
    private MFXButton btnTorneo;

    @FXML
    private BorderPane root;

    @FXML
    void onActionBtnAnadirDeporte(ActionEvent event) {
        
    }

    @FXML
    void onActionBtnAnadirEquipo(ActionEvent event) {
        FlowController.getInstance().goView("AddTeamsView");
    }

    @FXML
    void onActionBtnInformacion(ActionEvent event) {

    }

    @FXML
    void onActionBtnTorneo(ActionEvent event) {
        FlowController.getInstance().goView("secondary");
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
