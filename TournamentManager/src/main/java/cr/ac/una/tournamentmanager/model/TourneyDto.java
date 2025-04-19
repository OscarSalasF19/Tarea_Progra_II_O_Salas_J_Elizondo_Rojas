package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.Controller.MatchController;
import cr.ac.una.tournamentmanager.Controller.TournamentController;
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
            tournamentRounds.add(new ArrayList<TeamDto>());
        }
        if (tournamentRounds.get(currentRound).size() == 1) {// Check if there is a winner
            System.out.println("El torneo ha terminado");
            //Aqui Joshua el equipo ganador es el tournamentRounds.get(currentRound).get(0)

            return;
        }

        if (tournamentRounds.get(currentRound).get(currentMatch) == null && currentMatch == 0) {//check if the first team is single
            luckyTeam(tournamentRounds.get(currentRound).get(currentMatch + 1));
            currentMatch += 2;
        }

        MatchDto match = new MatchDto(tournamentRounds.get(currentRound).get(currentMatch), tournamentRounds.get(currentRound).get(currentMatch + 1));
        System.out.println("\nEl partido es entre: " + currentMatch + " y " + (currentMatch + 1) + "\n");
        MatchController controller = (MatchController) FlowController.getInstance().getController("MatchView");
        controller.setValues(match, sportID, matchTimeSeconds);
        FlowController.getInstance().goViewInWindowModal("MatchView", controller.getStage(), true);
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
        System.out.println("Por falta de equipos el torneo sera de " + totalOfTeams + " participantes.");
    }

    public void weHaveAWinner(TeamDto team) {
        TournamentController controller = (TournamentController) FlowController.getInstance().getController("TournamentView");
        int updateTheRound = currentRound + 1;

        if (currentMatch == 0 && nextRoundSize(currentRound) % 2 != 0) { // Check if the first team may be single
            tournamentRounds.get(currentRound + 1).add(null);
        }

        tournamentRounds.get(currentRound + 1).add(team);
        System.out.println("\nGanó el equipo: " + team.getName() + "\n");

        currentMatch += 2;

        if (currentMatch == tournamentRounds.get(currentRound).size() - 1) {// Check if there is a singleteam
            luckyTeam(tournamentRounds.get(currentRound).get(currentMatch));
            currentMatch = 0;
            currentRound++;
        }
        controller.updateRound(updateTheRound);
    }

    public int nextRoundSize(int roundActual) {
        return tournamentRounds.get(roundActual).size() / 2 + tournamentRounds.get(roundActual).size() % 2;
    }

    public void luckyTeam(TeamDto team) {
        tournamentRounds.get(currentRound + 1).add(team);
        System.out.println("\nPasa el equipo: " + team.getName() + "\n");
    }
}
