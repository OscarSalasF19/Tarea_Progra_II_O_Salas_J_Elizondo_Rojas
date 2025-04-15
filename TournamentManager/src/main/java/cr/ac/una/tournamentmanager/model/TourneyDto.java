package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.util.AppContext;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TourneyDto {

    private StringProperty sport = new SimpleStringProperty("");
    private StringProperty totalOfTeams = new SimpleStringProperty("");
    private int matchTimeMilliseconds = 10 * 1000; // Match duration in milliseconds
    private ArrayList<ArrayList<TeamDto>> tournamentStages = new ArrayList<>();
    private boolean timesUp = false;

    public String getSport() {
        return sport.get();
    }

    public void setSport(String sport) {
        this.sport.set(sport);
    }

    public int getTotalOfTeams() {
        String totalTeams = totalOfTeams.get();
        return (totalTeams != null && !totalTeams.isEmpty()) ? Integer.parseInt(totalTeams) : 0;
    }

    public void setTotalOfTeams(int totalOfTeams) {
        this.totalOfTeams.set(String.valueOf(totalOfTeams));
    }

    public int getMatchTimeMilliseconds() {
        return matchTimeMilliseconds;
    }

    public void setMatchTimeMilliseconds(int matchTimeMilliseconds) {
        this.matchTimeMilliseconds = matchTimeMilliseconds;
    }

    public StringProperty getSportProperty() {
        return sport;
    }

    public StringProperty getTotalTeamsProperty() {
        return totalOfTeams;
    }

    // Filters teams by the selected sport
    private ArrayList<TeamDto> filterTeamsBySport() {
        ArrayList<TeamDto> teams = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
        teams.removeIf(team -> !team.getSportName().equals(getSport()));
        return teams;
    }

    // Creates the initial list of teams for the tournament
    public void createTeamList() {
        tournamentStages.add(filterTeamsBySport());
        while (tournamentStages.get(0).size() > getTotalOfTeams()) {
            tournamentStages.get(0).remove((int) (Math.random() * tournamentStages.get(0).size()));
        }
        startTournament();
    }

    // Starts the tournament and organizes matches by stages
    private void startTournament() {
        for (int stage = 0; tournamentStages.get(stage).size() > 1; stage++) {
            while (tournamentStages.get(stage).size() > tournamentStages.get(stage + 1).size() * 2) {
                startMatch(stage, 0);
            }
        }
    }

    // Starts a match between two teams
    public void startMatch(int stage, int team) {
        if (tournamentStages.get(stage).size() <= team + 1) {
            tournamentStages.get(stage + 1).add(tournamentStages.get(stage).get(team));
            return;
        }

        Timer timer = new Timer();
        ArrayList<Integer> score = new ArrayList<>();
        score.add(0); // Score for team 1
        score.add(0); // Score for team 2

        // Task to finish the match after the timer ends
        TimerTask taskFinishMatch = new TimerTask() {
            @Override
            public void run() {
                timesUp = true;
                if (score.get(0).equals(score.get(1))) {
                    // Handle tie-breaker logic
                } else if (score.get(0) > score.get(1)) {
                    tournamentStages.get(stage).get(team).sumPoints(3);
                    tournamentStages.get(stage + 1).add(tournamentStages.get(stage).get(team));
                } else {
                    tournamentStages.get(stage).get(team + 1).sumPoints(3);
                    tournamentStages.get(stage + 1).add(tournamentStages.get(stage).get(team + 1));
                }
            }
        };

        timer.schedule(taskFinishMatch, matchTimeMilliseconds);

        // Simulates the match until the timer ends
        while (!timesUp) {
            // Logic to update scores
        }
    }
}
