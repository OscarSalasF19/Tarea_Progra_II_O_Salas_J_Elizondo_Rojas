package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.Controller.MatchController;
import cr.ac.una.tournamentmanager.Util.FlowController;

import java.util.ArrayList;

public class TourneyDto {

    private int sportID = 0;
    private int totalOfTeams = 0;
    private int matchTimeSeconds = 10000; // Match duration in seconds
    private final ArrayList<ArrayList<TeamDto>> tournamentRounds = new ArrayList<>();
    private int currentRound = 0;
    private int currentMatch = 0;

    public TourneyDto(int sportID, int totalOfTeams, int matchTimeSeconds, ArrayList<TeamDto> teams) {
        this.sportID = sportID;
        this.totalOfTeams = totalOfTeams;
        this.matchTimeSeconds = matchTimeSeconds;
        tournamentRounds.add(teams);
        
        if (tournamentRounds.get(0).size() < totalOfTeams) {
            searchForNewTeams(totalOfTeams - tournamentRounds.get(0).size());
        }
        
    }

    public int getsportID() {
        return sportID;
    }
    
    public int getTotalOfTeams() {
        return totalOfTeams;
    }
    
    public int getmatchTimeSeconds() {
        return matchTimeSeconds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentMatch() {
        return currentMatch;
    }

    public void nextMatch() {

        if (currentMatch >= tournamentRounds.get(currentRound).size()) {// Check if there are more matches in the current round
            currentMatch = 0;
            currentRound++;
        }
        if (tournamentRounds.get(currentRound).size() > 1 && currentMatch == 0) {// Check if there are more rounds
            tournamentRounds.set(currentRound + 1, new ArrayList<TeamDto>());
        }
        if (tournamentRounds.get(currentRound).size() == 1) {// Check if there is a winner
            System.out.println("El torneo ha terminado");
            return;
        }

        MatchDto match = new MatchDto(tournamentRounds.get(currentRound).get(currentMatch), tournamentRounds.get(currentRound).get(currentMatch + 1));
        MatchController controller = (MatchController) FlowController.getInstance().getController("MatchView");
        controller.setValues(match, sportID);
        FlowController.getInstance().goView("MatchView");

        currentMatch += 2;
    }

    public ArrayList<ArrayList<TeamDto>> getTournamentRounds() {
        return tournamentRounds;
    }

    public void searchForNewTeams(int cuantity) {
        ArrayList<TeamDto> teams = InfoManager.GetTeamList();
        for (TeamDto team : teams) {
            if (team.getSportID() == sportID && !tournamentRounds.get(0).contains(team)) {
                tournamentRounds.get(0).add(team);
                cuantity--;
                if (cuantity == 0) return;
            }
        }
        totalOfTeams = tournamentRounds.get(0).size();
        System.out.println("Por falta de equipos el torneo sera de " + totalOfTeams + " equipos");
    }

    public void weHaveAWinner(TeamDto team) {
        tournamentRounds.get(currentRound + 1).add(team);
    }
}
