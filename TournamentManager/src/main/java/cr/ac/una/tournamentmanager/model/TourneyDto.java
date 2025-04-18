package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.Util.FlowController;
import cr.ac.una.tournamentmanager.util.AppContext;

import java.util.ArrayList;

public class TourneyDto {

    private int sportID = 0;
    private int totalOfTeams = 0;
    private int matchTimeMilliseconds = 10000; // Match duration in milliseconds
    private final ArrayList<ArrayList<TeamDto>> tournamentRounds = new ArrayList<>();

    public int getsportIDID() {
        return sportID;
    }

    public void setsportID(int sportIDID) {
        this.sportID = sportIDID;
    }

    public int getTotalOfTeams() {
        return totalOfTeams;
    }

    public void setTotalOfTeams(int totalOfTeams) {
        this.totalOfTeams = totalOfTeams;
    }

    public int getMatchTimeMilliseconds() {
        return matchTimeMilliseconds;
    }

    public void setMatchTimeMilliseconds(int matchTimeMilliseconds) {
        this.matchTimeMilliseconds = matchTimeMilliseconds;
    }

    private ArrayList<TeamDto> filterTeamsBysportID() {
        ArrayList<TeamDto> availableTeams = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");
        for (TeamDto team : availableTeams) {
            if (team.getSportID() != sportID) {
                availableTeams.remove(team);
            }
        }
        return availableTeams;
    }

    public void createRandomTeamList() {
        tournamentRounds.add(filterTeamsBysportID());
        while (tournamentRounds.get(0).size() > getTotalOfTeams()) {
            tournamentRounds.get(0).remove((int) (Math.random() * tournamentRounds.get(0).size())); // Remove random teams until the size is equal to totalOfTeams
        }
        startTournament();
    }

    // Starts the tournament and organizes matches by stages
    private void startTournament() {

        for (int round = 0; tournamentRounds.get(round).size() > 1; round++) {
            tournamentRounds.set(round + 1, new ArrayList<TeamDto>());// Create a new round

            for (int team = 0; team < tournamentRounds.get(round).size(); team += 2) {
                MatchDto match = new MatchDto(tournamentRounds.get(round).get(team), tournamentRounds.get(round).get(team + 1));
                FlowController.getInstance().goViewInWindowModal("MatchView", null, true);

                tournamentRounds.get(round + 1).add((TeamDto) AppContext.getInstance().get("LastMatchWinner"));
                AppContext.getInstance().set("LastMatchWinner", null);
            }

        }
        //ganador el unico equipo del ultimo stage
    }


}
