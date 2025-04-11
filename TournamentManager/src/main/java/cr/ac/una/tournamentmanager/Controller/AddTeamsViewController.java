/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.EquipoDto;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class AddTeamsViewController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txfTeamName;
    @FXML
    private MFXTextField txfTeamSport;
    @FXML
    private ImageView imageTeamPhoto;
    
    private EquipoDto team;
    
    private ObjectProperty<EquipoDto> teamProperty = new SimpleObjectProperty<>();
    
    @FXML
    void onActionArchiveBtn(ActionEvent event) {
        //abrir explorador de archivos para escoger una foto y setear la ruta de
    }

    @FXML
    void onActionCamBtn(ActionEvent event) {
        //abre la camara, guarda una foto como archivo y setea la ruta de la foto al equipo
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bindTeam();
    }    

    @Override
    public void initialize() {

    }
    
    void bindTeam() {
        try{
            teamProperty.addListener((obs, oldVal, newVal) -> {
            if (oldVal != null){
                txfTeamName.textProperty().unbindBidirectional(oldVal.getNombreProperty());
                //desbindeo del url de la imagen
                txfTeamSport.textProperty().unbindBidirectional(oldVal.getSportProperty());
            }
            if (newVal != null){
                txfTeamName.textProperty().bindBidirectional(newVal.getNombreProperty());
                //bindeo de la url de la imagen
                txfTeamSport.textProperty().bindBidirectional(newVal.getSportProperty());
            }
        });    
            
        }catch (Exception ex){
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el biendeo", getStage(),"Ocurrio un error al realizar el bindeo");
        }
    }
}
