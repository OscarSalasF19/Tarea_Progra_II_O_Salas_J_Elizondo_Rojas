package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.MatchDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MatchController extends Controller implements Initializable {
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
            txfFstTeamScore.setText(String.valueOf(getScore(txfFstTeamScore) + 1));

        } else if (ballBounds.intersects(sndTeamBounds)) {
            System.out.println("Ball dropped on Team 2");
            txfSndTeamScore.setText(String.valueOf(getScore(txfSndTeamScore) + 1));

        } else {
            System.out.println("Ball not dropped on any team");
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startMatch();
    }

    public void setValues(MatchDto match, int SportID) {
        this.match = match;

        imageViewBall.setImage(new Image(InfoManager.GetSportImage(SportID)));
        imageViewFstTeam.setImage(new Image(match.getFstTeam().getTeamImageURL()));
        imageViewSndTeam.setImage(new Image(match.getSndTeam().getTeamImageURL()));
        txfFstTeamScore.setText("0");
        txfSndTeamScore.setText("0");
    }

    private void startMatch() {
        TimerTask task = new TimerTask() { //task to update the timer
            @Override
            public void run() {
                if (seconds > 0) { //while there is time left
                    txfTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                    seconds--;
                } else {
                    endMatch();
                    timer.cancel(); // Stop the timer
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.close();// Close the current window
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000); //countdown every second
    }

    private void endMatch() {
        TournamentController tournamentController = new TournamentController();
        if (getScore(txfFstTeamScore) > getScore(txfSndTeamScore)) {
            match.getFstTeam().sumPoints(3);
            tournamentController.weHaveAWinner(match.getFstTeam());
        } else if (getScore(txfFstTeamScore) < getScore(txfSndTeamScore)) {
            match.getSndTeam().sumPoints(3);
            tournamentController.weHaveAWinner(match.getSndTeam());
        } else {//apenas paara tener algo
            tournamentController.weHaveAWinner(tieBreaker());
        }
        closeMatch();
    }

    private Integer getScore(Label scoreLabel) {
        try {
            return Integer.parseInt(scoreLabel.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private TeamDto tieBreaker() { //apenas para tener algo
        Exception e = new Exception("Tie-breaker logic not implemented");
        match.tieBreaker().sumPoints(2);
        return match.tieBreaker();
    }

    private void closeMatch() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close(); // Close the current window
    }

}
