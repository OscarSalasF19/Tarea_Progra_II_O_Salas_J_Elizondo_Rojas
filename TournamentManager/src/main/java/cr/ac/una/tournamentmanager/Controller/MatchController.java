package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.model.InfoManager;
import cr.ac.una.tournamentmanager.model.MatchDto;
import cr.ac.una.tournamentmanager.model.TeamDto;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

    @FXML
    private BorderPane tieBreakerMenu;



    private MatchDto match;
    private int seconds;
    private Timer timer;

    private Double initalBallX;
    private Double initalBallY;
    private boolean isTieBreaker = false;
    private TeamDto winner = null;

    @FXML
    void onActionFinishMatch(ActionEvent event) {
        seconds = 0;
        if (winner != null) {
            TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");

            TeamDto loser = (winner.equals(match.getFstTeam())) ? match.getSndTeam() : match.getFstTeam();
            int fstScore = getScore(txfFstTeamScore);
            int sndScore = getScore(txfSndTeamScore);

            match.getFstTeam().setScores(match.getFstTeam().getScores() + fstScore);
            match.getSndTeam().setScores(match.getSndTeam().getScores() + sndScore);

            if (isTieBreaker) {
                match.getFstTeam().setTies(match.getFstTeam().getTies() + 1);
                match.getSndTeam().setTies(match.getSndTeam().getTies() + 1);

                match.getFstTeam().sumPoints(2);
                match.getSndTeam().sumPoints(2);
            } else {
                winner.setWins(winner.getWins() + 1);
                winner.sumPoints(3);

                loser.setLosses(loser.getLosses() + 1);
            }

            tournamentController.weHaveAWinner(winner);
            closeMatch();
        }
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
        // Check if the ball intersects with either team's goal area
        Bounds ballBounds = imageViewBall.localToScene(imageViewBall.getBoundsInLocal());
        Bounds fstTeamBounds = imageViewFstTeam.localToScene(imageViewFstTeam.getBoundsInLocal());
        Bounds sndTeamBounds = imageViewSndTeam.localToScene(imageViewSndTeam.getBoundsInLocal());

        if (ballBounds.intersects(fstTeamBounds)) {
            txfFstTeamScore.setText(String.valueOf(getScore(txfFstTeamScore) + 1));
            System.out.println("Equipo 1 :" + txfFstTeamScore.getText());
            if (isTieBreaker) seconds = 0;
        } else if (ballBounds.intersects(sndTeamBounds)) {
            txfSndTeamScore.setText(String.valueOf(getScore(txfSndTeamScore) + 1));
            System.out.println("Equipo 2 :" + txfSndTeamScore.getText());
            if (isTieBreaker) seconds = 0;
        }
        imageViewBall.toFront();
    }

    @FXML
    void onActionBtnIlegal(ActionEvent event) {
        showTieBreakerMenuAndChildren(false);
        Platform.runLater(() -> {
            double centerX = ballPane.getWidth() / 2 - imageViewBall.getFitWidth() / 2;
            double centerY = ballPane.getHeight() / 2 - imageViewBall.getFitHeight() / 2;
            imageViewBall.setLayoutX(centerX);
            imageViewBall.setLayoutY(centerY);
            imageViewBall.toFront();
        });
        seconds = 10; // Reset the timer to 10 seconds
        startMatch();
    }

    @FXML
    void onActionBtnLegal(ActionEvent event) {
        showTieBreakerMenuAndChildren(false);
        winner = match.tieBreaker();
        onActionFinishMatch(null);
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

        imageViewBall.setImage(new Image(InfoManager.GetSport(sportID).getBallImageURL()));

        // Load team images, fallback to default if loading fails
        try {
            imageViewFstTeam.setImage(new Image(match.getFstTeam().getTeamImageURL()));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del primer equipo: " + e.getMessage());
            imageViewFstTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Default-Image.png").toExternalForm()));
        }

        try {
            imageViewSndTeam.setImage(new Image(match.getSndTeam().getTeamImageURL()));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del segundo equipo: " + e.getMessage());
            imageViewSndTeam.setImage(new Image(getClass().getResource("/cr/ac/una/tournamentmanager/Resources/Default-Image.png").toExternalForm()));
        }

        txfFstTeamScore.setText("0");
        txfSndTeamScore.setText("0");

        // Center the ball on the pane after rendering
        Platform.runLater(() -> {
            double centerX = ballPane.getWidth() / 2 - imageViewBall.getFitWidth() / 2;
            double centerY = ballPane.getHeight() / 2 - imageViewBall.getFitHeight() / 2;
            imageViewBall.setLayoutX(centerX);
            imageViewBall.setLayoutY(centerY);
            imageViewBall.toFront();
        });

        showTieBreakerMenuAndChildren(false);

        startMatch();
    }

    private void startMatch() {
        if (timer != null) {
            timer.cancel(); // Cancel any existing timer
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
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000); // Schedule the timer to update every second
    }

    private void endMatch() {
        TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");
        int fstScore = getScore(txfFstTeamScore);
        int sndScore = getScore(txfSndTeamScore);

        if (fstScore > sndScore) {
            winner = match.getFstTeam();
            onActionFinishMatch(null);
            } else if (fstScore < sndScore) {
            winner = match.getSndTeam();
            onActionFinishMatch(null);
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
        isTieBreaker = true;
        showTieBreakerMenuAndChildren(true);
    }

    private void showTieBreakerMenuAndChildren(boolean show) {
        tieBreakerMenu.setVisible(show);
        tieBreakerMenu.setManaged(show);
        tieBreakerMenu.setDisable(!show);

        if (show) tieBreakerMenu.toFront();

        for (Node child : tieBreakerMenu.getChildren()) {
            child.setVisible(show);
            child.setManaged(show);
            child.setDisable(!show);
        }
    }

    private void closeMatch() {
        if (timer != null) timer.cancel();
        isTieBreaker = false;
        match = null;
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
