package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.model.TourneyDto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.layout.Priority.*;

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

    private TourneyDto tourney;

    @FXML
    void onActionNextGame(ActionEvent event) {
        tourney.nextMatch();
    }

    @Override
    public void initialize() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { //llamar a setValues
        //setValues((TourneyDto) AppContext.getInstance().get("tourney"));
    }

    public void setValues(TourneyDto tourney) {
        this.tourney = tourney;
        fieldRoundZero.getChildren().clear();
        fieldRoundOne.getChildren().clear();
        fieldRoundTwo.getChildren().clear();
        fieldRoundThree.getChildren().clear();
        fieldRoundFour.getChildren().clear();
        fieldRoundFive.getChildren().clear();
        fieldRoundSix.getChildren().clear();
        disableAndHideUnusedfields();
    }

    private void disableAndHideUnusedfields() {
        fieldRoundZero.setDisable(false);
        fieldRoundOne.setDisable(false);
        fieldRoundTwo.setDisable(false);
        fieldRoundThree.setDisable(false);
        fieldRoundFour.setDisable(false);
        fieldRoundFive.setDisable(false);
        fieldRoundSix.setDisable(false);

        switch(calculateRounds(tourney.getTotalOfTeams())) {
            case 2:
                fieldRoundTwo.setDisable(true);
            case 3:
                fieldRoundThree.setDisable(true);
            case 4:
                fieldRoundFour.setDisable(true);
            case 5:
                fieldRoundFive.setDisable(true);
            case 6:
                fieldRoundSix.setDisable(true);
                break;
        }
    }

    private int calculateRounds(int totalOfTeams) {
        int maxTeamCapacity = 1;
        for (int i = 1; i < totalOfTeams; i++) {
            maxTeamCapacity *= 2;
            if (maxTeamCapacity >= totalOfTeams) return i;

        }
        return 0;
    }

    private void updateRound(int round) {
        switch (round) {
            case 0:
                setupRound(fieldRoundZero, round);
                break;
            case 1:
                setupRound(fieldRoundOne, round);
                break;
            case 2:
                setupRound(fieldRoundTwo, round);
                break;
            case 3:
                setupRound(fieldRoundThree, round);
                break;
            case 4:
                setupRound(fieldRoundFour, round);
                break;
            case 5:
                setupRound(fieldRoundFive, round);
                break;
            case 6:
                setupRound(fieldRoundSix, round);
                break;
            default:
                System.out.println("Invalid round");
                break;
        }
    }

    private void setupRound(VBox vBox, int round) {
        vBox.getChildren().clear();
        vBox.setSpacing(5);
        vBox.setFillWidth(true);
        vBox.setMaxWidth(Double.MAX_VALUE);
        //vBox.setMaxHeight(65);
        VBox.setVgrow(vBox, Priority.ALWAYS);//sospechar
        vBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < tourney.getTournamentRounds().get(round).size(); i += 2) {
            VBox matchBox = new VBox();
            matchBox.getStyleClass().add("jfx-tournament-match-team-vbox-format");

            TeamDto team = tourney.getTournamentRounds().get(round).get(i);
            matchBox.getChildren().add(setupHBox(team));//equipo 1

            team = tourney.getTournamentRounds().get(round).get(i + 1);
            if (team != null) {
                matchBox.getChildren().add(setupHBox(team));//equipo 2
            }

            vBox.getChildren().add(matchBox);
        }

    }

    private HBox setupHBox(TeamDto team) {
        HBox teamBox = new HBox();
        teamBox.setSpacing(5);
        teamBox.setAlignment(Pos.CENTER_LEFT);
        teamBox.setMaxHeight(30);
        ImageView teamImageView = new ImageView(new Image(team.getTeamImageURL()));
        teamImageView.setFitHeight(30);
        teamImageView.setFitWidth(30);
        Label teamNameLabel = new Label(team.getName());

        teamBox.getChildren().add(teamImageView);
        teamBox.getChildren().add(teamNameLabel);
        return teamBox;
    }

    public void weHaveAWinner(TeamDto team) {
        tourney.weHaveAWinner(team);
        updateRound(tourney.getCurrentRound() + 1);
    }
}


