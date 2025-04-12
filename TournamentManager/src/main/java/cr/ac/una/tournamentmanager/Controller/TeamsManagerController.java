package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.EquipoDto;
import cr.ac.una.tournamentmanager.util.AppContext;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class TeamsManagerController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    
    @FXML
    private ImageView imageViewTeamPhoto;
    private StringProperty showTeamPhotoURL = new SimpleStringProperty("");
    
    @FXML
    private TableView<?> tableViewTeams;

    @FXML
    private MFXTextField txfSearch;

    @FXML
    private MFXTextField txfTeamName;

    @FXML
    private MFXTextField txfTeamPoints;

    @FXML
    private MFXTextField txfTeamSport;
    
    private EquipoDto selectedTeam;
    private ObjectProperty<EquipoDto> showTeamProperty = new SimpleObjectProperty<>();
    
    
    @FXML
    void onActionAddTeam(ActionEvent event) {
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
                String URLtoResources = "src/main/resources/cr/ac/una/tournamentmanager/Resources/";
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
        if(selectedTeam != null){
            ArrayList<EquipoDto> fullTeamArrayList = (ArrayList<EquipoDto>) AppContext.getInstance().get("FullTeamArrayList");
            fullTeamArrayList.remove(selectedTeam);
            AppContext.getInstance().set("FullTeamArrayList", fullTeamArrayList);                
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
                changeValues(null);
            } else {
                System.out.println("Presenta Datos Incompletos.");
            }
        } catch (Exception ex) {
            System.out.println("Error al guardar el equipo: " + ex.getMessage());
        }
    }

    private boolean areFieldsValid() {
        return !showTeamProperty.get().getNombre().isBlank() &&
               !showTeamProperty.get().getSport().isBlank() &&
               !showTeamProperty.get().getFoto().isBlank();
    }

    private void addNewTeam() {
        ArrayList<EquipoDto> fullTeamArrayList = (ArrayList<EquipoDto>) AppContext.getInstance().get("FullTeamArrayList");
        EquipoDto teamCopy = new EquipoDto();
        teamCopy.setNombre(showTeamProperty.get().getNombre());
        teamCopy.setFoto(showTeamProperty.get().getFoto());
        teamCopy.setSport(showTeamProperty.get().getSport());
        fullTeamArrayList.add(teamCopy);
        AppContext.getInstance().set("FullTeamArrayList", fullTeamArrayList);
    }

    private void updateExistingTeam() {
        selectedTeam.setNombre(showTeamProperty.get().getNombre());
        selectedTeam.setFoto(showTeamProperty.get().getFoto());
        selectedTeam.setSport(showTeamProperty.get().getSport());
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindShowTeam();
        changeValues(null);
    }
    
    void bindShowTeam() {
        try {
        showTeamProperty.addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                txfTeamName.textProperty().unbindBidirectional(oldVal.getNombreProperty());
                showTeamPhotoURL.unbindBidirectional(oldVal.getFotoProperty());
                txfTeamSport.textProperty().unbindBidirectional(oldVal.getSportProperty());
            }
            if (newVal != null) {
                txfTeamName.textProperty().bindBidirectional(newVal.getNombreProperty());
                showTeamPhotoURL.bindBidirectional(newVal.getFotoProperty());
                txfTeamSport.textProperty().bindBidirectional(newVal.getSportProperty());
            } 
        });
        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(), "Ocurrió un error al realizar el bindeo");
        }
    }

    public void changeValues(EquipoDto value) {
        if (value != null) {
            System.out.println("Cambiando datos a [" + value.getNombre() + "].");
            showTeamProperty.set(value);
        } else {
            System.out.println("Valores por defecto.");
            showTeamProperty.set(new EquipoDto());
            setDefaultImage();
        }
        selectedTeam = value;
    }

    public void setDefaultImage() {
        String imagePath = "/cr/ac/una/tournamentmanager/Resources/Grupo-300.png";
        showTeamPhotoURL.set(imagePath);
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        imageViewTeamPhoto.setImage(image);
    }
}
