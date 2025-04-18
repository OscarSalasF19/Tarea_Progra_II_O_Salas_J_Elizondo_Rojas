package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.SportDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TeamsManagerController extends Controller implements Initializable {

    private final StringProperty showTeamPhotoURL = new SimpleStringProperty();
    private ObservableList<TeamDto> filteredTeamsList = FXCollections.observableArrayList();
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imageViewTeamPhoto;
    @FXML
    private TableView<TeamDto> tableViewTeams;
    @FXML
    private TableColumn<TeamDto, String> infoTableColumn;
    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXTextField txfTeamName;
    @FXML
    private MFXTextField txfTeamPoints;
    @FXML
    private MFXTextField txfTeamSport;
    private TeamDto selectedTeam;

    @FXML
    void onActionAddTeam(ActionEvent event) {
        updateTableView();
        changeValues(null);//limpia el campo de la derecha para que se pueda añadir un equipo nuevo
    }

    @FXML
    void onActionArchive(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File chossedImage = fileChooser.showOpenDialog(imageViewTeamPhoto.getScene().getWindow());
        if (chossedImage != null) {
            try {
                String URLtoResources = "src/main/resources/cr/ac/una/tournamentmanager/Resources/Team-Photos/";
                File destinationURL = new File(URLtoResources + chossedImage.getName());

                Files.copy(chossedImage.toPath(), destinationURL.toPath(), StandardCopyOption.REPLACE_EXISTING);

                showTeamPhotoURL.set(destinationURL.toURI().toString());
            } catch (IOException e) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Error al copiar la imagen", getStage(), "No se pudo copiar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    void onActionCamera(ActionEvent event) {
        //abre camara para crear una imagen y despue sla setea
    }

    @FXML
    void onActionDelete(ActionEvent event) {//borra de el array de equipos segun seleccion
        if (selectedTeam != null) {
            ArrayList<TeamDto> fullTeamArrayList = InfoManager.GetTeamList();
            fullTeamArrayList.remove(selectedTeam);
            InfoManager.SetTeamList(fullTeamArrayList);
            updateTableView();
        }
        changeValues(null);
    }

    @FXML
    void onActionSave(ActionEvent event) {
        try {
            if (areFieldsValid()) {
                if (selectedTeam == null) {
                    addNewTeam();
                } else {
                    updateExistingTeam();
                }
                updateTableView();
                changeValues(null);
                System.out.println("Equipo guardado exitosamente.");
            }
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al guardar", getStage(), "Ocurrió un error al guardar el equipo: " + ex.getMessage());
        }
    }

    private boolean areFieldsValid() {
        if (showTeamPhotoURL.get().isBlank()) {
            setDefaultImage(); // Establecer imagen predeterminada si no hay imagen seleccionada
        }
        return isTeamNameValid(txfTeamName.getText().trim()) &&
               isTeamSportValid(txfTeamSport.getText().trim());
    }

    private boolean isTeamNameValid(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Nombre Invalido", getStage(), "Por favor, coloque un nombre.");
            return false; // Invalid team name
        }
        ArrayList<TeamDto> fullTeamArrayList = InfoManager.GetTeamList();
        for (TeamDto team : fullTeamArrayList) {
            if (team.getName().equalsIgnoreCase(teamName) && !team.equals(selectedTeam)) {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Nombre Invalido", getStage(), "Por favor, escoja otro nombre que sea único.");
                return false; // Nombre duplicado
            }
        }
        return true;
    }

    private boolean isTeamSportValid(String teamSport) {
        if (teamSport == null || teamSport.isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Deporte Invalido", getStage(), "Por favor, coloque un deporte.");
            return false; // Invalid team sport
        }
        ArrayList<SportDto> fullSportArrayList = InfoManager.GetSportList();
        for (SportDto sport : fullSportArrayList) {
            if (sport.getName().equalsIgnoreCase(teamSport)) {
                return true; // Team's sport is valid
            }
        }
        new Mensaje().showModal(Alert.AlertType.WARNING, "Deporte Invalido", getStage(), "Por favor, coloque un deporte que exista.");
        return false;
    }

    private void addNewTeam() {
        ArrayList<TeamDto> fullTeamArrayList = InfoManager.GetTeamList();
        TeamDto newTeam = new TeamDto();
        newTeam.setName(txfTeamName.getText().trim());
        newTeam.setSportID(InfoManager.GetSportID(txfTeamSport.getText().trim()));
        newTeam.setTeamImageURL(showTeamPhotoURL.get());
        fullTeamArrayList.add(newTeam);
        InfoManager.SetTeamList(fullTeamArrayList);
    }

    private void updateExistingTeam() {
        selectedTeam.setName(txfTeamName.getText().trim());
        selectedTeam.setSportID(InfoManager.GetSportID(txfTeamSport.getText().trim()));
        selectedTeam.setTeamImageURL(showTeamPhotoURL.get());
    }

    @FXML
    void onMouseClickedTeamsTable(MouseEvent event) {
        TeamDto selectedItem = tableViewTeams.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            changeValues(selectedItem);
        } else {
            System.out.println("No se seleccionó ningún elemento en la tabla.");
        }
    }

    private void updateTableView() {
        System.out.println("Actualizando tabla de equipos.");
        String searchValue = txfSearch.getText();
        txfSearch.setText(searchValue + " ");
        txfSearch.setText(searchValue);
        tableViewTeams.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        changeValues(null);

        ObservableList<TeamDto> fullTeamArrayList = FXCollections.observableArrayList(InfoManager.GetTeamList());
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
                            team.setTeamImageURL("/cr/ac/una/tournamentmanager/Resources/Default-Image.png");
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

        setupSearchListener();

        showTeamPhotoURL.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                imageViewTeamPhoto.setImage(new Image(newValue));
            } else {
                setDefaultImage();
            }
        });
    }

    private void setupSearchListener() {
        txfSearch.setText("");
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredTeamsList.clear();
            String searchText = newValue.toLowerCase().trim();

            if (searchText.isEmpty()) {
                filteredTeamsList = FXCollections.observableArrayList(InfoManager.GetTeamList());
            } else {
                ArrayList<TeamDto> fullTeamArrayList = InfoManager.GetTeamList();
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

    private void changeValues(TeamDto value) {
        if (value != null) {
            System.out.println("Cambiando datos seleccionados a [" + value.getName() + "].");
            txfTeamName.setText(value.getName());
            txfTeamSport.setText(InfoManager.GetSportName(value.getSportID()));
            txfTeamPoints.setText(String.valueOf(value.getPoints()));
            showTeamPhotoURL.set(value.getTeamImageURL());
        } else {
            System.out.println("Volviendo a valores por defecto.");
            txfTeamName.setText("");
            txfTeamSport.setText("");
            txfTeamPoints.setText("");
            showTeamPhotoURL.set("");
        }
        selectedTeam = value;
    }

    private void setDefaultImage() {
        String imagePath = "/cr/ac/una/tournamentmanager/Resources/Grupo-300.png";
        showTeamPhotoURL.set(imagePath); // Imagen predeterminada
    }

}

