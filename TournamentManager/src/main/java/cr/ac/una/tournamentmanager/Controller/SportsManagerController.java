package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.SportDto;
import cr.ac.una.tournamentmanager.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
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
    private TableView<?> tableViewSports;
    
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
        File chossedImage = fileChooser.showOpenDialog(imageViewSportPhoto.getScene().getWindow());
        if (chossedImage != null) {
            try {
                String URLtoResources = "src/main/resources/cr/ac/una/tournamentmanager/Resources/";
                File destinationURL = new File(URLtoResources + chossedImage.getName());

                Files.copy(chossedImage.toPath(), destinationURL.toPath(), StandardCopyOption.REPLACE_EXISTING);// Copiar el archivo seleccionado en Resources

                showSportPhotoURL.set(destinationURL.toURI().toString());
                imageViewSportPhoto.setImage(new Image(showSportPhotoURL.get()));
            } catch (IOException e) {
                System.out.println("Error al copiar la imagen: " + e.getMessage());
            }
        } 
    }

    @FXML
    void onActionCamera(ActionEvent event) {
    }

    @FXML
    void onActionDelete(ActionEvent event) {
        if(selectedSport != null){
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
            System.out.println("Cambiando datos a [" + value.getName() + "].");
            showSportProperty.set(value);
        } else {
            System.out.println("Valores por defecto.");
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
