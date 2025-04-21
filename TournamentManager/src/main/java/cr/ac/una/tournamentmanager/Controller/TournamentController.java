package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.model.TourneyDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TournamentController extends Controller implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private HBox totalAvailableSpace;

    @FXML
    private VBox fieldRoundZero;

    @FXML
    private VBox fieldRoundOne;

    @FXML
    private VBox fieldRoundTwo;

    @FXML
    private VBox fieldRoundThree;

    @FXML
    private VBox fieldRoundFour;

    @FXML
    private VBox fieldRoundFive;

    @FXML
    private VBox fieldRoundSix;

    private TourneyDto tourney = null;
    private ArrayList<ArrayList<Line>> roundLines;

    @FXML
    void onActionNextGame(ActionEvent event) {
        tourney.nextMatch();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic for the controller
    }

    public TourneyDto getTourney() {
        return tourney;
    }

    public void setValues(TourneyDto tourney) {
        this.tourney = tourney;

        totalAvailableSpace.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(totalAvailableSpace, Priority.ALWAYS);

        // Clear and construct boxes for each round
        for (int i = 0; i <= 6; i++) {
            VBox vBox = getVBoxForRound(i);
            if (vBox != null) {
                vBox.getChildren().clear();
                constructBox(vBox);
            }
        }
        updateRound(0);

        // Initialize line connections for rounds
        roundLines = new ArrayList<>();
        for (int i = 0; i < tourney.findHowManyRounds(); i++) {
            roundLines.add(new ArrayList<>());
        }
    }

    private VBox getVBoxForRound(int round) {
        switch (round) {
            case 0:
                return fieldRoundZero;
            case 1:
                return fieldRoundOne;
            case 2:
                return fieldRoundTwo;
            case 3:
                return fieldRoundThree;
            case 4:
                return fieldRoundFour;
            case 5:
                return fieldRoundFive;
            case 6:
                return fieldRoundSix;
            default:
                return null;
        }
    }

    public void updateRound(int roundToUpdate) {
        ArrayList<ArrayList<TeamDto>> rounds = tourney.getTournamentRoundsTeams();
        if (roundToUpdate >= rounds.size()) return;

        VBox currentRoundBox = getVBoxForRound(roundToUpdate);
        if (currentRoundBox == null) return;

        currentRoundBox.getChildren().clear();
        ArrayList<TeamDto> currentRound = rounds.get(roundToUpdate);

        // Handle first round (no lines to draw)
        if (roundToUpdate == 0) {
            for (int i = 0; i < currentRound.size(); i += 2) {
                TeamDto team1 = currentRound.get(i);
                TeamDto team2 = (i + 1 < currentRound.size()) ? currentRound.get(i + 1) : null;

                VBox matchBox = createTeamMatchBox(team1, team2);
                currentRoundBox.getChildren().add(matchBox);
            }
            return;
        }

        ArrayList<TeamDto> previousRound = rounds.get(roundToUpdate - 1);
        VBox previousRoundBox = getVBoxForRound(roundToUpdate - 1);
        if (previousRoundBox == null) return;

        // Remove previous lines
        for (Line line : roundLines.get(roundToUpdate - 1)) {
            root.getChildren().remove(line);
        }
        roundLines.get(roundToUpdate - 1).clear();

        // Create match boxes and draw lines for the current round
        for (int i = 0; i < currentRound.size(); i += 2) {
            TeamDto team1 = currentRound.get(i);
            TeamDto team2 = (i + 1 < currentRound.size()) ? currentRound.get(i + 1) : null;

            VBox matchBox = createTeamMatchBox(team1, team2);
            currentRoundBox.getChildren().add(matchBox);

            if (team1 != null) {
                VBox source = findVBoxForTeam(previousRoundBox, previousRound, team1);
                if (source != null) roundLines.get(roundToUpdate - 1).add(drawLine(source, matchBox));
            }

            if (team2 != null) {
                VBox source = findVBoxForTeam(previousRoundBox, previousRound, team2);
                if (source != null) roundLines.get(roundToUpdate - 1).add(drawLine(source, matchBox));
            }
        }

        root.getChildren().addAll(roundLines.get(roundToUpdate - 1));
    }

    private VBox findVBoxForTeam(VBox previousRoundBox, ArrayList<TeamDto> previousTeams, TeamDto target) {
        // Locate the VBox corresponding to a specific team in the previous round
        for (int i = 0; i < previousTeams.size(); i++) {
            TeamDto team = previousTeams.get(i);
            if (team != null && team.equals(target)) {
                return (VBox) previousRoundBox.getChildren().get(i / 2);
            }
        }
        return null;
    }

    private VBox createTeamMatchBox(TeamDto team1, TeamDto team2) {
        // Create a VBox to represent a match between two teams
        VBox matchBox = new VBox();
        matchBox.getStyleClass().add("jfx-tournament-match-team-vbox-format");

        if (team1 != null) matchBox.getChildren().add(setupHBox(team1));
        if (team2 != null) matchBox.getChildren().add(setupHBox(team2));

        return matchBox;
    }

    private void constructBox(VBox vBox) {
        // Configure the layout properties of a VBox
        vBox.setSpacing(20);
        vBox.setFillWidth(true);
        vBox.setMaxWidth(80);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.setAlignment(Pos.CENTER_LEFT);
    }

    private Line drawLine(VBox sourceBox, VBox targetBox) {
        // Draw a line connecting two VBoxes
        Line line = new Line();
        line.setStyle("-fx-stroke: #009999; -fx-stroke-width: 3;");

        Runnable updateLine = () -> {
            Point2D start = sourceBox.localToScene(sourceBox.getWidth(), sourceBox.getHeight() / 2);
            Point2D end = targetBox.localToScene(0, targetBox.getHeight() / 2);

            Point2D startInRoot = root.sceneToLocal(start);
            Point2D endInRoot = root.sceneToLocal(end);

            line.setStartX(startInRoot.getX());
            line.setStartY(startInRoot.getY());
            line.setEndX(endInRoot.getX());
            line.setEndY(endInRoot.getY());
        };

        // Update line position on layout or window changes
        root.layoutBoundsProperty().addListener((obs, o, n) -> updateLine.run());
        sourceBox.boundsInParentProperty().addListener((obs, o, n) -> updateLine.run());
        targetBox.boundsInParentProperty().addListener((obs, o, n) -> updateLine.run());

        sourceBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.showingProperty().addListener((obsShow, wasShowing, isNowShowing) -> {
                            if (isNowShowing) updateLine.run();
                        });
                    }
                });
            }
        });

        return line;
    }

    private HBox setupHBox(TeamDto team) {
        // Create an HBox to display a team's information
        HBox teamBox = new HBox();
        teamBox.setSpacing(5);
        teamBox.setAlignment(Pos.CENTER_LEFT);
        teamBox.setMaxHeight(30);
        teamBox.setMaxWidth(Double.MAX_VALUE);

        ImageView teamImageView = new ImageView(new Image(team.getTeamImageURL()));
        teamImageView.setFitHeight(30);
        teamImageView.setFitWidth(30);

        Label teamNameLabel = new Label(team.getName());

        teamBox.getChildren().addAll(teamImageView, teamNameLabel);
        return teamBox;
    }

    public void weHaveAWinner(TeamDto team) {
        tourney.weHaveAWinner(team);
    }

    public void winnerAnimation(TeamDto team) {


        // Implement winner animation logic here
        // This method can be used to animate the winning team


    }


}
