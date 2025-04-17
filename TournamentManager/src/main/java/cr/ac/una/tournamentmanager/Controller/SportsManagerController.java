package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.SportDto;
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
import javafx.scene.layout.VBox;
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
    private final ObjectProperty<SportDto> showSportProperty = new SimpleObjectProperty<>();
    @FXML
    private AnchorPane root;
    @FXML
    private VBox imageBox;
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
        changeValues(null); // Limpia los campos para añadir un nuevo deporte
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
                String resourcesPath = "src/main/resources/cr/ac/una/tournamentmanager/Resources/Sport-Photos/";
                File destinationPath = new File(resourcesPath + chosenImage.getName());

                // Copy the selected file to the Resources folder
                Files.copy(chosenImage.toPath(), destinationPath.toPath(), StandardCopyOption.REPLACE_EXISTING);

                showSportPhotoURL.set(destinationPath.toURI().toString());
                imageViewSportPhoto.setImage(new Image(showSportPhotoURL.get()));
            } catch (IOException e) {
                System.out.println("Error al copiar la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    void onActionCamera(ActionEvent event) {
        // Camera functionality not implemented
    }

    @FXML
    void onActionDelete(ActionEvent event) {
        if (selectedSport != null) {
            ArrayList<SportDto> fullSportArrayList = (ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList");
            fullSportArrayList.remove(selectedSport);
            AppContext.getInstance().set("FullSportArrayList", fullSportArrayList);
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
                updateTableView();
                changeValues(null);
                System.out.println("Deporte guardado exitosamente.");
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
        return isSportNameValid(txfSportName.getText().trim()) &&
                !showSportPhotoURL.get().isBlank();
    }

    private boolean isSportNameValid(String sportName) {
        if (sportName == null || sportName.isBlank()) {
            return false; // Invalid name
        }
        ArrayList<SportDto> fullSportArrayList = (ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList");
        for (SportDto sport : fullSportArrayList) {
            if (sport.getName().equalsIgnoreCase(sportName) && !sport.equals(selectedSport)) {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Nombre Invalido", getStage(), "Por favor, escoja otro nombre que sea único.");
                return false; // Sport's name already exists
            }
        }
        return true; // Valid name for a new sport
    }

    private void addNewSport() {
        ArrayList<SportDto> fullSportArrayList = (ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList");
        SportDto newSport = new SportDto();
        newSport.setName(txfSportName.getText().trim());
        newSport.setBallImageURL(showSportPhotoURL.get());
        fullSportArrayList.add(newSport);
        AppContext.getInstance().set("FullSportArrayList", fullSportArrayList);
    }

    private void updateExistingSport() {
        selectedSport.setName(txfSportName.getText().trim());
        selectedSport.setBallImageURL(showSportPhotoURL.get());
    }

    private void updateTableView() {
        ArrayList<SportDto> fullSportArrayList = (ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList");
        ObservableList<SportDto> filteredList = FXCollections.observableArrayList();

        String searchText = txfSearch.getText().toLowerCase().trim();
        for (SportDto sport : fullSportArrayList) {
            if (sport.getName().toLowerCase().contains(searchText)) {
                filteredList.add(sport);
            }
        }

        tableViewSports.setItems(filteredList);
        tableViewSports.refresh();
        tableViewSports.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindShowSport();
        changeValues(null);

        ArrayList<SportDto> tempArrayList = new ArrayList<SportDto>();
        SportDto tempTeam = new SportDto();
        tempTeam.setName("VolleyBall");
        tempArrayList.add(tempTeam);
        AppContext.getInstance().set("FullSportArrayList", tempArrayList);

        ObservableList<SportDto> observableSportList = FXCollections.observableArrayList((ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList"));
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
                            imageView.setImage(new Image(sport.getBallImageURL()));
                        } catch (Exception e) {
                            System.out.println("Error al cargar la imagen: " + sport.getName() + " | " + sport.getBallImageURL());
                            sport.getBallImageURLProperty().set("/cr/ac/una/tournamentmanager/Resources/Default-Image.png");
                            imageView.setImage(new Image(sport.getBallImageURL()));
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

        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> { //to make the search work
            updateTableView();
            System.out.println("Texto cambiado de: " + oldValue + " a: " + newValue);
        });
    }

    private void bindShowSport() {
        try {
            showSportProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txfSportName.textProperty().unbindBidirectional(oldVal.getNameProperty());
                    showSportPhotoURL.unbindBidirectional(oldVal.getBallImageURLProperty());
                }
                if (newVal != null) {
                    txfSportName.textProperty().bindBidirectional(newVal.getNameProperty());
                    showSportPhotoURL.bindBidirectional(newVal.getBallImageURLProperty());
                }
            });
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(), "Ocurrió un error al realizar el bindeo");
        }
    }

    private void changeValues(SportDto value) {
        if (value != null) {
            System.out.println("Cambiando datos seleccionados a [" + value.getName() + "].");
            showSportProperty.set(value);
            imageViewSportPhoto.setImage(new Image(value.getBallImageURL()));
        } else {
            System.out.println("Volviendo a valores por defecto.");
            showSportProperty.set(new SportDto());
            setDefaultImage();
        }
        selectedSport = value;
    }

    private void setDefaultImage() {
        String imagePath = "/cr/ac/una/tournamentmanager/Resources/Bola-300.png";
        showSportPhotoURL.set(imagePath);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        imageViewSportPhoto.setImage(image);
    }
}
