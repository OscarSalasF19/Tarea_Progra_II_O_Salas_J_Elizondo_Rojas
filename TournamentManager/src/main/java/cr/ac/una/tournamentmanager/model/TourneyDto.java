package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.util.AppContext;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class TourneyDto {

    private final StringProperty sport = new SimpleStringProperty("");
    private final StringProperty totalOfTeams = new SimpleStringProperty("");
    private final IntegerProperty matchTimeMilliseconds = new SimpleIntegerProperty(10000); // Match duration in milliseconds
    private final ArrayList<ArrayList<TeamDto>> tournamentStages = new ArrayList<>();
    private final boolean timesUp = false;

    public String getSport() {
        return sport.get();
    }

    public void setSport(String sport) {
        this.sport.set(sport);
    }

    public int getTotalOfTeams() {
        String totalTeams = this.totalOfTeams.get();
        return (totalTeams != null && !totalTeams.isEmpty()) ? Integer.parseInt(totalTeams) : 0;
    }

    public void setTotalOfTeams(int totalOfTeams) {
        this.totalOfTeams.set(String.valueOf(totalOfTeams));
    }

    public int getMatchTimeMilliseconds() {
        int matchTimeMilliseconds = this.matchTimeMilliseconds.get();
        return (matchTimeMilliseconds > 0) ? matchTimeMilliseconds : 10000; // Default to 10 seconds if invalid
    }

    public void setMatchTimeMilliseconds(int matchTimeMilliseconds) {
        this.matchTimeMilliseconds.set(matchTimeMilliseconds);
    }

    public StringProperty getSportProperty() {
        return sport;
    }

    public StringProperty getTotalTeamsProperty() {
        return totalOfTeams;
    }

    public IntegerProperty getMatchTimeMillisecondsProperty() {
        return matchTimeMilliseconds;
    }


    // Filters teams by the selected sport
    private ArrayList<TeamDto> filterTeamsBySport() {
        ArrayList<TeamDto> teams = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
        for (int i = 0; i < teams.size(); i++) {
            if (!teams.get(i).getSportName().equals(getSport())) {
                teams.remove(i);
                i--; // Ajustar el índice después de eliminar un elemento
            }
        }
        return teams;
    }

    // Creates the initial list of teams for the tournament
    public void createRandomTeamList() {
        tournamentStages.add(filterTeamsBySport());
        while (tournamentStages.get(0).size() > getTotalOfTeams()) {
            tournamentStages.get(0).remove((int) (Math.random() * tournamentStages.get(0).size())); // Remove random teams until the size is equal to totalOfTeams
        }
        startTournament();
    }

    // Starts the tournament and organizes matches by stages
    private void startTournament() {
        for (int round = 0; tournamentStages.get(round).size() > 1; round++) {
            tournamentStages.set(round + 1, new ArrayList<TeamDto>());// Create a new round

            for (int team = 0; team < tournamentStages.get(round).size(); team += 2) {
                MatchDto match = new MatchDto(tournamentStages.get(round).get(team), tournamentStages.get(round).get(team + 1));
                FlowController.getInstance().goViewInWindowModal("MatchView", null, true);

                tournamentStages.get(round + 1).add((TeamDto) AppContext.getInstance().get("LastMatchWinner"));
                AppContext.getInstance().set("LastMatchWinner", null);
            }

        }
        //ganador el unico equipo del ultimo stage
    }


}
