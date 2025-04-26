package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.SportDto;
import io.github.palexdev.materialfx.controls.MFXTextField;
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

public class SportsManagerController extends Controller implements Initializable {

    private final StringProperty showSportPhotoURL = new SimpleStringProperty();
    private ObservableList<SportDto> filteredSportsList = FXCollections.observableArrayList();
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imageViewSportPhoto;
    @FXML
    private TableView<SportDto> tableViewSports;
    @FXML
    private TableColumn<SportDto, String> infoTableColumn;
    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXTextField txfSportName;
    private SportDto selectedSport;

    @FXML
    void onActionAddSport(ActionEvent event) {
        updateTableView();
        changeValues(null);
    }

    @FXML
    void onActionArchive(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File chosenImage = fileChooser.showOpenDialog(imageViewSportPhoto.getScene().getWindow());
        if (chosenImage != null) {
            try {
                String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/tournamentmanager/Resources/Sports-Photos/";
                File destinationPath = new File(resourcesPath + chosenImage.getName());

                // Copy the selected file to the Resources folder
                Files.copy(chosenImage.toPath(), destinationPath.toPath(), StandardCopyOption.REPLACE_EXISTING);

                showSportPhotoURL.set("Sports-Photos/" + chosenImage.getName());
            } catch (IOException e) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Error al copiar la imagen", getStage(), "No se pudo copiar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    void onActionDelete(ActionEvent event) {
        if (selectedSport != null) {
            InfoManager.deleteSport(selectedSport.getID());
            updateTableView();
        }
        changeValues(null);
    }

    @FXML
    void onActionSave(ActionEvent event) {
        try {
            if (areFieldsValid()) {
                if (selectedSport == null) {
                    addNewSport();
                } else {
                    updateExistingSport();
                }
                System.out.println("Deporte guardado exitosamente.");
                updateTableView();
                changeValues(null);
            }
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al guardar", getStage(), "Ocurrió un error al guardar el deporte: " + ex.getMessage());
        }
    }

    @FXML
    void onMouseClickedSportsTable(MouseEvent event) {
        SportDto selectedItem = tableViewSports.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            changeValues(selectedItem);
        } else {
            System.out.println("No se seleccionó ningún elemento en la tabla.");
        }
    }

    private boolean areFieldsValid() {
        if (showSportPhotoURL.get().isBlank()) {
            setDefaultImage();
        }
        return isSportNameValid(txfSportName.getText().trim());
    }

    private boolean isSportNameValid(String sportName) {
        if (sportName == null || sportName.isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Nombre Invalido", getStage(), "Por favor, ingrese un nombre.");
            return false; // Invalid name
        }
        ArrayList<SportDto> fullSportArrayList = InfoManager.GetSportList();
        for (SportDto sport : fullSportArrayList) {
            if (sport.getName().equalsIgnoreCase(sportName) && !sport.equals(selectedSport)) {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Nombre Invalido", getStage(), "Por favor, escoja otro nombre que sea único.");
                return false; // Sport's name already exists
            }
        }
        return true; // Valid name for a new sport
    }

    private void addNewSport() {
        ArrayList<SportDto> fullSportArrayList = InfoManager.GetSportList();
        SportDto newSport = new SportDto();
        newSport.setName(txfSportName.getText().trim());
        newSport.setBallImageURL(showSportPhotoURL.get());
        fullSportArrayList.add(newSport);
        InfoManager.SetSportList(fullSportArrayList);
    }

    private void updateExistingSport() {
        selectedSport.setName(txfSportName.getText().trim());
        selectedSport.setBallImageURL(showSportPhotoURL.get());
    }

    public void updateTableView() {
        System.out.println("Actualizando tabla de deportes.");
        String searchValue = txfSearch.getText();
        txfSearch.setText(searchValue + " ");
        txfSearch.setText(searchValue);
        tableViewSports.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        changeValues(null);

        ObservableList<SportDto> observableSportList = FXCollections.observableArrayList(InfoManager.GetSportList());
        tableViewSports.setItems(observableSportList);

        infoTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        infoTableColumn.setCellFactory(column -> {
            return new TableCell<SportDto, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty || name == null) {
                        setGraphic(null);
                    } else {
                        SportDto sport = getTableView().getItems().get(getIndex());
                        try {
                            imageView.setImage(InfoManager.loadImage(sport.getBallImageURL()));
                        } catch (Exception e) {
                            System.out.println("Error al cargar la imagen: " + sport.getName() + " | " + sport.getBallImageURL());
                            sport.setBallImageURL("/Default-Image.png");
                            imageView.setImage(InfoManager.loadImage(sport.getBallImageURL()));
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

        showSportPhotoURL.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                imageViewSportPhoto.setImage(InfoManager.loadImage(newValue));
            } else {
                setDefaultImage();
            }
        });
    }

    private void setupSearchListener() {
        txfSearch.setText("");
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredSportsList.clear();
            String searchText = newValue.toLowerCase().trim();

            filteredSportsList.addAll(InfoManager.GetSportList());
            filteredSportsList.removeIf(sport -> sport.getID() <= 0);
            filteredSportsList.removeIf(sport -> !sport.getName().toLowerCase().contains(searchText));

            tableViewSports.setItems(filteredSportsList);
            tableViewSports.refresh();
        });
        txfSearch.setText(" ");
        txfSearch.setText("");
    }

    public void changeValues(SportDto value) {
        if (value != null) {
            System.out.println("Cambiando datos seleccionados a [" + value.getName() + "].");
            txfSportName.setText(value.getName());
            showSportPhotoURL.set(value.getBallImageURL());
        } else {
            System.out.println("Volviendo a valores por defecto.");
            txfSportName.setText("");
            showSportPhotoURL.set("");
        }
        selectedSport = value;
    }

    private void setDefaultImage() {
        String imagePath = "Bola-300.png";
        showSportPhotoURL.set(imagePath);
    }
}
