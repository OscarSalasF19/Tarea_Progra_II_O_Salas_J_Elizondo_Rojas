package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
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

    private ArrayList<TeamDto> tournamentTeams;

    //private ObservableList<TeamDto> tournamentTeams = FXCollections.observableArrayList();

    private ObservableList<TeamDto> filteredTeamsList = FXCollections.observableArrayList();

    @FXML
    void onActionStartTourney(ActionEvent event) {

    }

    @FXML
    void onMouseClickedTeamsTable(MouseEvent event) {
        TeamDto selectedTeam = tableViewTeams.getSelectionModel().getSelectedItem();
        if (selectedTeam != null && !tournamentTeams.contains(selectedTeam)) {
            tournamentTeams.add(selectedTeam);
            System.out.println("Equipo añadido al torneo: " + selectedTeam.getName());
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tournamentTeams = new ArrayList<>();

        ObservableList<TeamDto> fullTeamArrayList = FXCollections.observableArrayList((ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList"));
        tableViewTeams.setItems(fullTeamArrayList);

        infoTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        infoTableColumn.setCellFactory(column -> {
            return new TableCell<TeamDto, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty || name == null) {
                        setGraphic(null);
                    } else {
                        TeamDto team = getTableView().getItems().get(getIndex());
                        try {
                            imageView.setImage(new Image(team.getTeamImageURL()));
                        } catch (Exception e) {
                            System.out.println("Error al cargar la imagen: " + team.getName() + " | " + team.getTeamImageURL());
                            team.getTeamImageURLProperty().set("/cr/ac/una/tournamentmanager/Resources/Default-Image.png");
                            imageView.setImage(new Image(team.getTeamImageURL()));
                        }
                        imageView.setFitHeight(30);
                        imageView.setFitWidth(30);

                        HBox hbox = new HBox(imageView, new Label(name));
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setSpacing(10);
                        setGraphic(hbox);
                    }
                }
            };
        });

        setupSlider();
        setupSearchListener();
    }

    private void setupSearchListener() {
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredTeamsList.clear();
            String searchText = newValue.toLowerCase().trim();

            if (searchText.isEmpty()) {
                filteredTeamsList = FXCollections.observableArrayList((ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList"));
            } else {
                ArrayList<TeamDto> fullTeamArrayList = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
                for (TeamDto team : fullTeamArrayList) {
                    if (team.getName().trim().toLowerCase().contains(searchText)) {
                        filteredTeamsList.add(team);
                    }
                }
            }
            tableViewTeams.setItems(filteredTeamsList);
            tableViewTeams.refresh();
        });
    }

    private void setupSlider() {
        sliderTeamsAmount.setMin(2);
        sliderTeamsAmount.setMax(64);
        sliderTeamsAmount.setValue(2);
        txfTeamsAmount.setText(String.valueOf((int) sliderTeamsAmount.getValue()));

        // Cambiar el color del slider//no sirve
        sliderTeamsAmount.setStyle("-fx-accent: #009999;");

        sliderTeamsAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            txfTeamsAmount.setText(String.valueOf((int) newValue.doubleValue()));
        });
    }

    private void updateTableView() {
        System.out.println("Actualizando tabla de equipos.");
        String searchValue = txfSearch.getText();
        txfSearch.setText(searchValue + " ");
        txfSearch.setText(searchValue);
        tableViewTeams.getSelectionModel().clearSelection();
    }
}

