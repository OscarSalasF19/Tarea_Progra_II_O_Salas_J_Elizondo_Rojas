package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.SportDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.model.TourneyDto;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;


public class TournamentFormController extends Controller implements Initializable {

    private final ObservableList<TeamDto> observableSeletedTeams = FXCollections.observableArrayList();
    private final ObservableList<TeamDto> filteredTeamsList = FXCollections.observableArrayList();
    @FXML
    private AnchorPane root;
    @FXML
    private MFXSlider sliderTeamsAmount;
    @FXML
    private TableView<TeamDto> tableViewTeams;
    @FXML
    private TableColumn<TeamDto, String> infoTableColumn;
    @FXML
    private TableView<TeamDto> tableViewSelectedTeams;
    @FXML
    private TableColumn<TeamDto, String> infoSelectedTeamsTableColumn;
    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXTextField txfMinutes;
    @FXML
    private MFXTextField txfSeconds;
    @FXML
    private Label txfTeamsAmount;

    @FXML
    void onActionStartTourney(ActionEvent event) {

        int seconds = 0;

        if (txfMinutes.getText().trim().isEmpty()) txfMinutes.setText("0");
        if (txfSeconds.getText().trim().isEmpty()) txfSeconds.setText("0");


        seconds = parseInt(txfMinutes.getText()) * 60;
        seconds += parseInt(txfSeconds.getText());
        if (seconds < 5) seconds = 15;

        System.out.println("\nTime: " + seconds / 60 + " : " + seconds % 60);

        txfMinutes.setText("");
        txfSeconds.setText("");


        int sportID = 0;

        if (InfoManager.GetSport(txfSearch.getText()) != null)
            sportID = InfoManager.GetSport(txfSearch.getText()).getID();
        if (sportID == 0) {
            if (observableSeletedTeams.size() == 0) {
                System.out.println("No se ha seleccionado un deporte.");
                return;
            }

        }

        TourneyDto tourney = new TourneyDto(
                sportID,
                (int) sliderTeamsAmount.getValue(),
                seconds,
                new ArrayList<TeamDto>(observableSeletedTeams));

        TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");
        tournamentController.setValues(tourney);
        FlowController.getInstance().goView("TournamentView");
    }

    @FXML
    void onMouseClickedTeamsTable(MouseEvent event) {
        TeamDto selectedTeam = tableViewTeams.getSelectionModel().getSelectedItem();
        if (selectedTeam != null && !observableSeletedTeams.contains(selectedTeam)) {
            observableSeletedTeams.add(selectedTeam);
            System.out.println("Equipo añadido al torneo: " + selectedTeam.getName());
        }
        updateTableView();
    }

    @FXML
    void onMouseClickedSelectedTeamsTable(MouseEvent event) {
        TeamDto selectedTeam = tableViewSelectedTeams.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            observableSeletedTeams.remove(selectedTeam);
            System.out.println("Equipo eliminado del torneo: " + selectedTeam.getName());
        }
        updateTableView();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<TeamDto> fullTeamArrayList = FXCollections.observableArrayList(InfoManager.GetTeamList());
        tableViewTeams.setItems(fullTeamArrayList);
        infoTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        configureColumn(infoTableColumn);

        tableViewSelectedTeams.setItems(observableSeletedTeams);
        infoSelectedTeamsTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        configureColumn(infoSelectedTeamsTableColumn);

        setupSlider();
        setupSearchListener();
        setupTimer();
    }

    private void configureColumn(TableColumn<TeamDto, String> column) {
        column.setCellFactory(col -> {
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
                            imageView.setImage(InfoManager.loadImage(team.getTeamImageURL()));
                        } catch (Exception e) {
                            System.out.println("Error al cargar la imagen: " + team.getName() + " | " + team.getTeamImageURL());
                            team.setTeamImageURL("/Default-Image.png");
                            imageView.setImage(InfoManager.loadImage(team.getTeamImageURL()));
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
    }

    private void setupSearchListener() {
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredTeamsList.clear();
            String searchText = newValue.toLowerCase().trim();

            filteredTeamsList.addAll(InfoManager.GetTeamList());
            filteredTeamsList.removeIf(team -> team.getID() <= 0);

            SportDto sport = InfoManager.GetSport(searchText);
            if (sport != null) filteredTeamsList.removeIf(team -> team.getSportID() != sport.getID());
            else filteredTeamsList.clear();

            tableViewTeams.setItems(filteredTeamsList);
            tableViewTeams.refresh();
        });
    }

    private void setupSlider() {
        sliderTeamsAmount.setMin(2);
        sliderTeamsAmount.setMax(64);
        sliderTeamsAmount.setValue(2);
        sliderTeamsAmount.setUnitIncrement(1);
        txfTeamsAmount.setText("0" + (int) sliderTeamsAmount.getValue());

        sliderTeamsAmount.setStyle("-fx-accent: #009999;");

        sliderTeamsAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            txfTeamsAmount.setText(String.valueOf((int) newValue.doubleValue()));
            if ((int) newValue.doubleValue() < 10) {
                txfTeamsAmount.setText("0" + newValue.intValue());
            }
        });
    }

    private void setupTimer() {
        setupNumericFilter(txfMinutes, 2);
        setupNumericFilter(txfSeconds, 2);
    }

    private void setupNumericFilter(MFXTextField textField, int maxLength) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String cleaned = newValue.replaceAll("\\D", "");

            if (cleaned.length() > maxLength) cleaned = cleaned.substring(0, maxLength);

            if (!cleaned.isEmpty()) {
                try {
                    int value = Integer.parseInt(cleaned);
                    if (value >= 60) {
                        cleaned = "59";
                    }
                } catch (NumberFormatException e) {
                    cleaned = "0";
                }
            }

            if (!newValue.equals(cleaned)) {
                final String finalCleaned = cleaned;
                Platform.runLater(() -> textField.setText(finalCleaned));
            }
        });
    }

    public void updateTableView() {
        System.out.println("Actualizando tablas.");
        tableViewTeams.getSelectionModel().clearSelection();
        String search = txfSearch.getText();
        txfSearch.setText(search + " ");
        txfSearch.setText(search);
        tableViewSelectedTeams.refresh();
    }
}

