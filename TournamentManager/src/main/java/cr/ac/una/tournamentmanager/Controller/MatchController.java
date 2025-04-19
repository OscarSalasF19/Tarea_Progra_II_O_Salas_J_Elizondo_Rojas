package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.MatchDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MatchController extends Controller implements Initializable {
    @FXML
    private ImageView imageViewBall;

    @FXML
    private Pane ballPane;

    @FXML
    private ImageView imageViewFstTeam;

    @FXML
    private ImageView imageViewSndTeam;

    @FXML
    private AnchorPane root;

    @FXML
    private Label txfFstTeamScore;

    @FXML
    private Label txfSndTeamScore;

    @FXML
    private Label txfTimer;

    private MatchDto match;
    private int seconds;
    private Timer timer;

    private Double initalBallX;
    private Double initalBallY;

    @FXML
    void onActionFinishMatch(ActionEvent event) {
        seconds = 0;
    }

    @FXML
    void onMousePressedBallImage(MouseEvent event) {
        initalBallX = event.getSceneX() - imageViewBall.getLayoutX();
        initalBallY = event.getSceneY() - imageViewBall.getLayoutY();
    }

    @FXML
    void onMouseDraggedBallImage(MouseEvent event) {
        imageViewBall.toFront();
        imageViewBall.setLayoutX(event.getSceneX() - initalBallX);
        imageViewBall.setLayoutY(event.getSceneY() - initalBallY);
    }

    @FXML
    void onMouseReleasedBallImage(MouseEvent event) {
        Bounds ballBounds = imageViewBall.localToScene(imageViewBall.getBoundsInLocal());
        Bounds fstTeamBounds = imageViewFstTeam.localToScene(imageViewFstTeam.getBoundsInLocal());
        Bounds sndTeamBounds = imageViewSndTeam.localToScene(imageViewSndTeam.getBoundsInLocal());

        if (ballBounds.intersects(fstTeamBounds)) {
            txfFstTeamScore.setText(String.valueOf(getScore(txfFstTeamScore) + 1));
            System.out.println("Equipo1 :" + txfFstTeamScore.getText());
        } else if (ballBounds.intersects(sndTeamBounds)) {
            txfSndTeamScore.setText(String.valueOf(getScore(txfSndTeamScore) + 1));
            System.out.println("Equipo2 :" + txfSndTeamScore.getText());
        }
        imageViewBall.toFront();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setValues(MatchDto match, int sportID, int seconds) {
        this.match = match;
        this.seconds = seconds;

        imageViewBall.setImage(new Image(InfoManager.GetSportImage(sportID)));

        // Asegurarse de cargar correctamente las imágenes de los equipos
        try {
            imageViewFstTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Team-Photos/" + match.getFstTeam().getTeamImageURL()).toExternalForm()));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del primer equipo: " + e.getMessage());
            imageViewFstTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Default-Image.png").toExternalForm()));
        }

        try {
            imageViewSndTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Team-Photos/" + match.getSndTeam().getTeamImageURL()).toExternalForm()));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del segundo equipo: " + e.getMessage());
            imageViewSndTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Default-Image.png").toExternalForm()));
        }

        txfFstTeamScore.setText("0");
        txfSndTeamScore.setText("0");

        // Centrar la bola al iniciar (esperar al render para obtener ancho/alto)
        Platform.runLater(() -> {
            double centerX = ballPane.getWidth() / 2 - imageViewBall.getFitWidth() / 2;
            double centerY = ballPane.getHeight() / 2 - imageViewBall.getFitHeight() / 2;
            imageViewBall.setLayoutX(centerX);
            imageViewBall.setLayoutY(centerY);
            imageViewBall.toFront();
        });

        startMatch();
    }

    private void startMatch() {
        if (timer != null) {
            timer.cancel();
        }

        Platform.runLater(() -> {
            txfTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
        });

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (seconds > 0) {
                        txfTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                        seconds--;
                    } else {
                        endMatch();
                        timer.cancel();
                        closeMatch();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private void endMatch() {
        TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");
        int fstScore = getScore(txfFstTeamScore);
        int sndScore = getScore(txfSndTeamScore);

        if (fstScore > sndScore) {
            match.getFstTeam().sumPoints(3);
            tournamentController.weHaveAWinner(match.getFstTeam());
        } else if (fstScore < sndScore) {
            match.getSndTeam().sumPoints(3);
            tournamentController.weHaveAWinner(match.getSndTeam());
        } else {
            TeamDto winner = tieBreaker();
            winner.sumPoints(2);
            tournamentController.weHaveAWinner(winner);
        }
    }

    private Integer getScore(Label scoreLabel) {
        try {
            return Integer.parseInt(scoreLabel.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private TeamDto tieBreaker() {
        // Aquí puedes poner tu lógica real, esto es un placeholder
        return match.tieBreaker();
    }

    private void closeMatch() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
