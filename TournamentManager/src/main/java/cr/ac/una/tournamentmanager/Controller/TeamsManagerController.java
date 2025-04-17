package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
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
import javafx.scene.input.InputMethodEvent;
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

    @FXML
    private AnchorPane root;

    @FXML
    private ImageView imageViewTeamPhoto;
    private final StringProperty showTeamPhotoURL = new SimpleStringProperty("");

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
    private final ObjectProperty<TeamDto> showTeamProperty = new SimpleObjectProperty<>();


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

                Files.copy(chossedImage.toPath(), destinationURL.toPath(), StandardCopyOption.REPLACE_EXISTING);// Copiar el archivo seleccionado en Resources

                showTeamPhotoURL.set(destinationURL.toURI().toString());
                imageViewTeamPhoto.setImage(new Image(showTeamPhotoURL.get()));
            } catch (IOException e) {
                System.out.println("Error al copiar la imagen: " + e.getMessage());
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
            ArrayList<TeamDto> fullTeamArrayList = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
            fullTeamArrayList.remove(selectedTeam);
            AppContext.getInstance().set("FullTeamArrayList", fullTeamArrayList);
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
            } else {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Datos incompletos", getStage(), "Por favor, complete todos los campos obligatorios.");
            }
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al guardar", getStage(), "Ocurrió un error al guardar el equipo: " + ex.getMessage());
        }
    }

    private boolean areFieldsValid() {
        return !txfTeamName.getText().isBlank() &&
               !txfTeamSport.getText().isBlank() &&
               !showTeamPhotoURL.get().isBlank();
    }

    private void addNewTeam() {
        ArrayList<TeamDto> fullTeamArrayList = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
        TeamDto newTeam = new TeamDto();
        newTeam.setName(txfTeamName.getText().trim());
        newTeam.setSportName(txfTeamSport.getText().trim());
        newTeam.setTeamImageURL(showTeamPhotoURL.get());
        fullTeamArrayList.add(newTeam);
        AppContext.getInstance().set("FullTeamArrayList", fullTeamArrayList);
    }

    private void updateExistingTeam() {
        selectedTeam.setName(txfTeamName.getText().trim());
        selectedTeam.setSportName(txfTeamSport.getText().trim());
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
        ArrayList<TeamDto> fullTeamArrayList = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
        ObservableList<TeamDto> filteredList = FXCollections.observableArrayList();

        String searchText = txfSearch.getText().toLowerCase().trim();
        for (TeamDto team : fullTeamArrayList) {
            if (team.getName().toLowerCase().contains(searchText)) {
                filteredList.add(team);
            }
        }

        tableViewTeams.setItems(filteredList);
        tableViewTeams.refresh();
        tableViewTeams.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindShowTeam();
        changeValues(null);

        //eliminar
        AppContext.getInstance().set("FullTeamArrayList", new ArrayList<TeamDto>());

        ObservableList<TeamDto> observableTeamList = FXCollections.observableArrayList((ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList"));
        tableViewTeams.setItems(observableTeamList);

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
    }

    private void bindShowTeam() {
        try {
            showTeamProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txfTeamName.textProperty().unbindBidirectional(oldVal.getNameProperty());
                    showTeamPhotoURL.unbindBidirectional(oldVal.getTeamImageURLProperty());
                    txfTeamSport.textProperty().unbindBidirectional(oldVal.getSportNameProperty());
                    txfTeamPoints.textProperty().unbind();
                }
                if (newVal != null) {
                    txfTeamName.textProperty().bindBidirectional(newVal.getNameProperty());
                    showTeamPhotoURL.bindBidirectional(newVal.getTeamImageURLProperty());
                    txfTeamSport.textProperty().bindBidirectional(newVal.getSportNameProperty());
                    txfTeamPoints.textProperty().bind(new SimpleStringProperty("Puntuación: " + newVal.getPoints() + " pts.")); // Vincular correctamente
                }
            });
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(), "Ocurrió un error al realizar el bindeo");
        }

        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTableView();
            System.out.println("Texto cambiado de: " + oldValue + " a: " + newValue);
        });
    }

    private void changeValues(TeamDto value) {
        if (value != null) {
            System.out.println("Cambiando datos seleccionados a [" + value.getName() + "].");
            showTeamProperty.set(value);
            imageViewTeamPhoto.setImage(new Image(value.getTeamImageURL()));
        } else {
            System.out.println("Volviendo a valores por defecto.");
            showTeamProperty.set(new TeamDto());
            setDefaultImage();
        }
        selectedTeam = value;
    }

    private void setDefaultImage() {
        String imagePath = "/cr/ac/una/tournamentmanager/Resources/Grupo-300.png";
        showTeamPhotoURL.set(imagePath);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        imageViewTeamPhoto.setImage(image);
    }

}
