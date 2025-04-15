package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.SportDto;
import cr.ac.una.tournamentmanager.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SportsManagerController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private VBox imageBox;

    @FXML
    private ImageView imageViewSportPhoto;
    private StringProperty showSportPhotoURL = new SimpleStringProperty();

    @FXML
    private TableView<SportDto> tableViewSports;

    @FXML
    private TableColumn<SportDto, String> tColumnSports;

    @FXML
    private MFXTextField txfSearch;

    @FXML
    private MFXTextField txfSportName;

    private SportDto selectedSport;
    private ObjectProperty<SportDto> showSportProperty = new SimpleObjectProperty<>();

    @FXML
    void onActionAddSport(ActionEvent event) {
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
                String resourcesPath = "src/main/resources/cr/ac/una/tournamentmanager/Resources/";
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
                changeValues(null);
            } else {
                System.out.println("Presenta Datos Incompletos.");
            }
        } catch (Exception ex) {
            System.out.println("Error al guardar el deporte: " + ex.getMessage());
        }
    }

    private boolean areFieldsValid() {
        return !showSportProperty.get().getName().isBlank() &&
               !showSportProperty.get().getBallImageURL().isBlank();
    }

    private void addNewSport() {
        ArrayList<SportDto> fullSportArrayList = (ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList");
        SportDto sportCopy = new SportDto();
        sportCopy.setName(showSportProperty.get().getName());
        sportCopy.setBallImageURL(showSportProperty.get().getBallImageURL());
        fullSportArrayList.add(sportCopy);
        AppContext.getInstance().set("FullSportArrayList", fullSportArrayList);
    }

    private void updateExistingSport() {
        selectedSport.setName(showSportProperty.get().getName());
        selectedSport.setBallImageURL(showSportProperty.get().getBallImageURL());
    }

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindShowSport();
        changeValues(null);


        ObservableList<SportDto> observableSportList = FXCollections.observableArrayList((ArrayList<SportDto>) AppContext.getInstance().get("FullSportArrayList"));
        observableSportList.add(new SportDto("Futbol", "/cr/ac/una/tournamentmanager/Resources/Bola-300.png"));
        tableViewSports.setItems(observableSportList);

        tColumnSports.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());

        tColumnSports.setCellFactory(column -> {
            return new TableCell<SportDto, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty || name == null) {
                        setGraphic(null);
                    } else {
                        SportDto sport = getTableView().getItems().get(getIndex());
                        imageView.setImage(new Image(sport.getBallImageURLProperty().get()));
                        imageView.setFitHeight(20);
                        imageView.setFitWidth(20);
                        setGraphic(new HBox(imageView, new Label(name)));
                    }
                }
            };
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
        if (value == null) {
            System.out.println("Valores por defecto.");
            showSportProperty.set(new SportDto());
            setDefaultImage();
        } else {
            System.out.println("Cambiando datos a [" + value.getName() + "].");
            showSportProperty.set(value);
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
