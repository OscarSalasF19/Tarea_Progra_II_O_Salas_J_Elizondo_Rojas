package cr.ac.una.tournamentmanager.Controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import cr.ac.una.tournamentmanager.Util.Mensaje;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.SportDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TeamsManagerController extends Controller implements Initializable {

    private final StringProperty showTeamPhotoURL = new SimpleStringProperty();
    private volatile boolean capturing = true;
    private Webcam webcam;
    private BufferedImage lastCapturedShot;
    private JFrame cameraWindow;
    private final ObservableList<TeamDto> filteredTeamsList = FXCollections.observableArrayList();
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
    private MFXTextField txfTeamSport;

    private TeamDto selectedTeam;
    @FXML
    private MFXButton pictureBtn;
    @FXML
    private Label labelTeamScores;
    @FXML
    private Label labelTeamTies;
    @FXML
    private Label labelTeamWins;
    @FXML
    private Label labelTeamLosses;
    @FXML
    private Label labelTeamPoints;
    @FXML
    private TextArea textAreaTournamentSummary;

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
                String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/tournamentmanager/Resources/Team-Photos/";
                File destinationURL = new File(resourcesPath + chossedImage.getName());

                Files.copy(chossedImage.toPath(), destinationURL.toPath(), StandardCopyOption.REPLACE_EXISTING);

                showTeamPhotoURL.set("Team-Photos/" + chossedImage.getName());
            } catch (IOException e) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Error al copiar la imagen", getStage(), "No se pudo copiar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    void onActionCamera(ActionEvent event) {
        openCameraWindow();
    }

    private void openCameraWindow() {

        pictureBtn.setVisible(true);
        pictureBtn.setDisable(false);
        pictureBtn.setManaged(true);
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFillArea(true);
        panel.setMirrored(true);

        cameraWindow = new JFrame("Camera Overview");

        cameraWindow.add(panel);
        cameraWindow.setResizable(true);
        cameraWindow.pack();
        cameraWindow.setVisible(true);

        capturing = true;

        cameraWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        new Thread(() -> {
            while (capturing) {
                BufferedImage frame = webcam.getImage();
                if (frame != null) {
                    lastCapturedShot = frame;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }

                if (!cameraWindow.isActive()) {
                    stopCamera();
                    pictureBtn.setVisible(false);
                    pictureBtn.setDisable(true);
                    pictureBtn.setManaged(false);

                }
            }
        }).start();
    }

    public void stopCamera() {
        capturing = false;
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    @FXML
    void onActionDelete(ActionEvent event) {// Deletes the selected team
        if (selectedTeam != null) {
            File file = new File(selectedTeam.getTeamImageURL().trim());
            file.deleteOnExit();

            InfoManager.deleteTeam(selectedTeam.getID());

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
            setDefaultImage();
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
        newTeam.setSportID(InfoManager.GetSport(txfTeamSport.getText().trim()).getID());
        newTeam.setTeamImageURL(showTeamPhotoURL.getValue());
        fullTeamArrayList.add(newTeam);

        InfoManager.SetTeamList(fullTeamArrayList);
    }

    private void updateExistingTeam() {
        selectedTeam.setName(txfTeamName.getText().trim());
        selectedTeam.setSportID(InfoManager.GetSport(txfTeamSport.getText().trim()).getID());
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

    public void updateTableView() {
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

        setupSearchListener();

        showTeamPhotoURL.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                imageViewTeamPhoto.setImage(InfoManager.loadImage(newValue));
                System.out.println("cargue la imagen");
            } else {
                setDefaultImage();
                System.out.println("cargue la imagen default");

            }
        });
    }

    private void setupSearchListener() {
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredTeamsList.clear();
            String searchText = newValue.toLowerCase().trim();

            filteredTeamsList.addAll(InfoManager.GetTeamList());
            filteredTeamsList.removeIf(team -> team.getID() <= 0);
            filteredTeamsList.removeIf(team -> !team.getName().toLowerCase().contains(searchText));

            for (int i = 0; i < filteredTeamsList.size(); i++) {
                for (int j = 0; j < filteredTeamsList.size() - 1; j++) {
                    TeamDto current = filteredTeamsList.get(j);
                    TeamDto next = filteredTeamsList.get(j + 1);

                    if (current.getPoints() < next.getPoints()) {
                        filteredTeamsList.set(j, next);
                        filteredTeamsList.set(j + 1, current);
                    } else if (current.getPoints() == next.getPoints()) {
                        if (current.getName().compareToIgnoreCase(next.getName()) > 0) {
                            filteredTeamsList.set(j, next);
                            filteredTeamsList.set(j + 1, current);
                        }
                    }
                }
            }

            tableViewTeams.setItems(filteredTeamsList);
            tableViewTeams.refresh();
        });
        txfSearch.setText(" ");
        txfSearch.setText("");
    }

    public void changeValues(TeamDto value) {
        if (value != null) {
            System.out.println("Cambiando datos seleccionados a [" + value.getName() + "].");
            txfTeamName.setText(value.getName());
            txfTeamSport.setText(InfoManager.GetSport(value.getSportID()).getName());
            labelTeamPoints.setText("# " + value.getPoints());
            labelTeamScores.setText("# " + value.getScores());
            labelTeamWins.setText("# " + value.getWins());
            labelTeamTies.setText("# " + value.getTies());
            labelTeamLosses.setText("# " + value.getLosses());
            textAreaTournamentSummary.setText(InfoManager.getFullTorneysData(value.getID()));
            showTeamPhotoURL.set(value.getTeamImageURL());
        } else {
            System.out.println("Volviendo a valores por defecto.");
            txfTeamName.setText("");
            txfTeamSport.setText("");
            labelTeamPoints.setText("#_");
            labelTeamScores.setText("#_");
            labelTeamWins.setText("#_");
            labelTeamTies.setText("#_");
            labelTeamLosses.setText("#_");
            textAreaTournamentSummary.setText("");
            setDefaultImage();
        }
        selectedTeam = value;
    }

    private void setDefaultImage() {
        String imagePath = "Grupo-300.png";
        showTeamPhotoURL.set(imagePath);
    }

    @FXML
    private void onActionTakeShot(ActionEvent event) throws IOException {
        capturing = false;

        if (lastCapturedShot != null) {
            String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/tournamentmanager/Resources/Team-Photos/";
            Image image1 = SwingFXUtils.toFXImage(lastCapturedShot, null);
            File file = new File(resourcesPath + txfTeamName.getText().trim() + ".png");
            ImageIO.write(lastCapturedShot, "PNG", file);
            showTeamPhotoURL.set("Team-Photos/" + txfTeamName.getText().trim() + ".png");

        }

        if (webcam != null && webcam.isOpen()) {
            webcam.close();

        }

        if (cameraWindow != null) {
            cameraWindow.dispose();
            cameraWindow = null;
        }
        pictureBtn.setVisible(false);
        pictureBtn.setDisable(true);
        pictureBtn.setManaged(false);

    }

}



