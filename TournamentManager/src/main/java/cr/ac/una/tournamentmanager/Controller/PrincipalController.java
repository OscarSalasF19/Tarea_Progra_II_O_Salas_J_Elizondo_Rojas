package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

public class PrincipalController extends Controller implements Initializable{

    @FXML
    private BorderPane root;

    @FXML
    void onActionBtnInfoSport(ActionEvent event) {
        
    }

    @FXML
    void onActionBtnInfoTeam(ActionEvent event) {
        FlowController.getInstance().goView("TeamsManagerView");
    }

    @FXML
    void onActionBtnTourney(ActionEvent event) {
        FlowController.getInstance().goView("secondary");
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
