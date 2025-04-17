package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.TeamDto;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TournamentFormController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private MFXSlider sliderTeamsAmount;

    @FXML
    private TableView<TeamDto> tableViewTeams;

    @FXML
    private TableColumn<TeamDto, String> infoTableColumn;

    @FXML
    private MFXTextField txfMinutes;

    @FXML
    private MFXTextField txfSearch;

    @FXML
    private MFXTextField txfSeconds;

    @FXML
    private Label txfTeamsAmount;


    @FXML
    void onActionStartTourney(ActionEvent event) {

    }

    @FXML
    void textChangedSearch(InputMethodEvent event) {

    }


    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
