package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.MatchDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MatchController extends  Controller implements Initializable {
    @FXML
    private MFXButton btnFinishMatch;

    @FXML
    private ImageView imageViewBall;

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
    private int seconds = 10;
    private Timer timer = new Timer();

    @FXML
    void onActionFinishMatch(ActionEvent event) {
        seconds = 0;
    }

    @FXML
    void onMouseDraggedBallImage(MouseEvent event) {
        imageViewBall.setLayoutX(event.getSceneX() - imageViewBall.getFitWidth() / 2);
        imageViewBall.setLayoutY(event.getSceneY() - imageViewBall.getFitHeight() / 2);
    }

    @FXML
    void onMouseReleasedBallImage(MouseEvent event) {
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        System.out.println("Mouse Released at: X=" + mouseX + ", Y=" + mouseY);

        Bounds ballBounds = imageViewBall.localToScene(imageViewBall.getBoundsInLocal());
        Bounds fstTeamBounds = imageViewFstTeam.localToScene(imageViewFstTeam.getBoundsInLocal());
        Bounds sndTeamBounds = imageViewSndTeam.localToScene(imageViewSndTeam.getBoundsInLocal());

        if (ballBounds.intersects(fstTeamBounds)) {
            System.out.println("Ball dropped on Team 1");
            txfFstTeamScore.setText( String.valueOf(getScore(txfFstTeamScore) + 1));

        } else if (ballBounds.intersects(sndTeamBounds)) {
            System.out.println("Ball dropped on Team 2");
            txfSndTeamScore.setText( String.valueOf(getScore(txfSndTeamScore) + 1));

        } else {
            System.out.println("Ball not dropped on any team");
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (seconds > 0) {
                    txfTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                    seconds--;
                } else {
                    endMatch();
                    timer.cancel();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.close();// Close the current window
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void endMatch() {
        if (getScore(txfFstTeamScore) > getScore(txfSndTeamScore)) {
            match.getFstTeam().sumPoints(3);
        } else if (getScore(txfFstTeamScore) < getScore(txfSndTeamScore)) {
            match.getSndTeam().sumPoints(3);
        } else {
            tieBreaker();
        }
    }

    private Integer getScore(Label scoreLabel) {
        try {
            return Integer.parseInt(scoreLabel.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void tieBreaker() {
        Exception e = new Exception("Tie-breaker logic not implemented");
        // Implement tie-breaker logic here
        // For example, you can create a new match between the tied teams
        // and update their scores accordingly.
    }


}
