package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalController extends Controller implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    void onActionBtnInfoSport(ActionEvent event) {
        FlowController.getInstance().goView("SportsManagerView");
    }

    @FXML
    void onActionBtnInfoTeam(ActionEvent event) {
        FlowController.getInstance().goView("TeamsManagerView");
    }

    @FXML
    void onActionBtnTourney(ActionEvent event) {
        TournamentFormController controller = (TournamentFormController) FlowController.getInstance().getController("TournamentFormView");
        controller.updateTableView();
        FlowController.getInstance().goView("TournamentFormView");
    }

    @FXML
    void onActionBtnExit(ActionEvent event) {
        FlowController.getInstance().salir();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
