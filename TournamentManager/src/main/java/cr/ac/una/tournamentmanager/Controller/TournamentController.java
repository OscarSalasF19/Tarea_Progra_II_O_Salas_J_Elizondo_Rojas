package cr.ac.una.tournamentmanager.Controller;

import cr.ac.una.tournamentmanager.model.TeamDto;
import cr.ac.una.tournamentmanager.model.TourneyDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
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

    private TourneyDto tourney;

    @FXML
    void onActionNextGame(ActionEvent event) {
        tourney.nextMatch();
    }

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //llamar a setValues
        //setValues((TourneyDto) AppContext.getInstance().get("tourney"));
    }

    public void setValues(TourneyDto tourney) {

        this.tourney = tourney;

        totalAvailableSpace.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(totalAvailableSpace, Priority.ALWAYS);

        fieldRoundZero.getChildren().clear();
        constructBox(fieldRoundZero);

        fieldRoundOne.getChildren().clear();
        constructBox(fieldRoundOne);

        fieldRoundTwo.getChildren().clear();
        constructBox(fieldRoundTwo);

        fieldRoundThree.getChildren().clear();
        constructBox(fieldRoundThree);

        fieldRoundFour.getChildren().clear();
        constructBox(fieldRoundFour);

        fieldRoundFive.getChildren().clear();
        constructBox(fieldRoundFive);

        fieldRoundSix.getChildren().clear();
        constructBox(fieldRoundSix);

        updateRound(fieldRoundZero, 0);

    }

    public void updateRound(int round) {
        switch (round) {
            //case 0 is impossible to happen
            case 1:
                updateRound(fieldRoundOne, round);
                break;
            case 2:
                updateRound(fieldRoundTwo, round);
                break;
            case 3:
                updateRound(fieldRoundThree,round);
                break;
            case 4:
                updateRound(fieldRoundFour, round);
                break;
            case 5:
                updateRound(fieldRoundFive,round);
                break;
            case 6:
                updateRound(fieldRoundSix, round);
                break;
            default:
                System.out.println("Invalid round");
                break;
        }
    }

    private void addEncounterBox(VBox vBox, int round, int match) {

        VBox encounterBox = new VBox();
        encounterBox.getStyleClass().add("jfx-tournament-match-team-vbox-format");

        TeamDto team = new TeamDto();
        try {//team 1 mini box
            team = tourney.getTournamentRounds().get(round).get(match);
            if (team != null) encounterBox.getChildren().add(setupHBox(team));//equipo 1
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No hay equipo rival");
        }

        try {//team 2 mini box
            team = tourney.getTournamentRounds().get(round).get(match + 1);
            if (team != null) encounterBox.getChildren().add(setupHBox(team));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No hay equipo rival");
        }

        vBox.getChildren().add(encounterBox);
    }

    private void constructBox(VBox vBox) {
        vBox.setSpacing(20);
        vBox.setFillWidth(true);
        vBox.setMaxWidth(80);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.setAlignment(Pos.CENTER_LEFT);
    }

    private void updateRound(VBox vBox, int round) {//updates the round visually
        vBox.getChildren().clear();
        for (int i = 0; i < tourney.getTournamentRounds().get(round).size(); i += 2) {
            addEncounterBox(vBox, round, i);
        }
    }

    private HBox setupHBox(TeamDto team) {
        HBox teamBox = new HBox();
        teamBox.setSpacing(5);
        teamBox.setAlignment(Pos.CENTER_LEFT);
        teamBox.setMaxHeight(30);
        teamBox.setMaxWidth(Double.MAX_VALUE);
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
    }

}


