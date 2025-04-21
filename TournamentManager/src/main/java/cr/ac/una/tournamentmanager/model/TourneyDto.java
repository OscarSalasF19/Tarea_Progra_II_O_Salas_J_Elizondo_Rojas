package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.Controller.MatchController;
import cr.ac.una.tournamentmanager.Controller.TournamentController;
import cr.ac.una.tournamentmanager.Util.FlowController;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class TourneyDto {

    private final ArrayList<ArrayList<Integer>> tournamentRoundsID = new ArrayList<>();
    private int sportID = 0;
    private int totalOfTeams = 0;
    private int matchTimeSeconds = 10000;
    private int currentRound = 0;
    private int currentMatch = 0;

    private final int numberOfRounds;
    private final String creationDate;     // solo la fecha: "20-04-2025"
    private final String creationTime;     // solo la hora: "14:45:30"


    public TourneyDto(int sportID, int totalOfTeams, int matchTimeSeconds, ArrayList<TeamDto> teams) {
        this.sportID = sportID;
        this.totalOfTeams = totalOfTeams;
        this.matchTimeSeconds = matchTimeSeconds;
        tournamentRoundsID.add(new ArrayList<Integer>());
        currentRound = 0;
        currentMatch = 0;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        this.creationDate = now.format(dateFormatter);
        this.creationTime = now.format(timeFormatter);

        Iterator<TeamDto> iterator = teams.iterator();
        while (iterator.hasNext()) {
            TeamDto team = iterator.next();
            if (team.getSportID() != sportID) {
                System.out.println("El equipo " + team.getName() + " no es del deporte " + InfoManager.GetSport(sportID).getName());
                iterator.remove();
            }
        }

        for (TeamDto team : teams) {
            tournamentRoundsID.get(0).add(team.getID());
            System.out.println("El equipo " + team.getName() + " tiene el ID: " + team.getID());
        }

        if (teams.size() < totalOfTeams) {
            System.out.println("Se van a anadir equipos aptos para el torneo");
            searchForNewTeams(totalOfTeams - teams.size());
        }

        if (totalOfTeams == 1) {
            System.out.println("El torneo no se puede realizar, solo hay un equipo.");
            numberOfRounds = 0;
            return;
        }

        this.numberOfRounds = findHowManyRounds();

        for (int i = 0; i < numberOfRounds; i++) {
            tournamentRoundsID.add(new ArrayList<>());
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

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public String getCreationDate() {
    return creationDate;
    }

    public String getCreationTime() {
    return creationTime;
    }

    public void nextMatch() {

        if (currentMatch >= tournamentRoundsID.get(currentRound).size()) {// Check if there are more matches in the current round
            currentMatch = 0;
            currentRound++;
        }

        if (tournamentRoundsID.get(currentRound).size() == 1) {// Check if there is a winner
            System.out.println("El torneo ha terminado");

            ArrayList<TourneyDto> tournaments = InfoManager.GetTournamentList();
            if (!tournaments.isEmpty() && tournaments.get(tournaments.size() - 1).equals(this)) {
                System.out.println("El torneo ya fue guardado previamente.");
                return;
            }

            tournaments.add(this);
            InfoManager.SetTournamentList(tournaments);
            System.out.println("\nTorneo guardado con exito");

            System.out.println("Se va a crear el certificado del ganador...");
            TeamDto winner = InfoManager.GetTeam((tournamentRoundsID.get(currentRound).get(0)));
            InfoManager.createWinnerCertificate(winner, this);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> { //waits for a correct tread
                        FlowController.getInstance().goView("TournamentFormView");
                        FlowController.getInstance().limpiarLoader("TournamentView");
                    });
                }
            }, 10 * 1000);// 10,000 = 10 segundos, 1000 = 1 segundo // tiempo antes de quitar la animacion

            TournamentController tournamentController = (TournamentController) FlowController.getInstance().getController("TournamentView");
            tournamentController.winnerAnimation(winner);//llama a la animacion y le manda el equipo ganador

            return;
        }

        if (tournamentRoundsID.get(currentRound).get(currentMatch) == null && currentMatch == 0) {//check if the first team is single
            luckyTeam(tournamentRoundsID.get(currentRound).get(currentMatch + 1));
            currentMatch += 2;
        }

        FlowController.getInstance().limpiarLoader("MatchView");

        //experimento
        TeamDto fstTeam = InfoManager.GetTeam(tournamentRoundsID.get(currentRound).get(currentMatch));
        TeamDto sndTeam = InfoManager.GetTeam(tournamentRoundsID.get(currentRound).get(currentMatch + 1));


        MatchDto match = new MatchDto(fstTeam, sndTeam);
        System.out.println("\nEl partido es entre los equipos: |" + currentMatch + "| y |" + (currentMatch + 1) + "|\n");
        MatchController controller = (MatchController) FlowController.getInstance().getController("MatchView");
        controller.setValues(match, sportID, matchTimeSeconds);
        FlowController.getInstance().goViewInWindowModal("MatchView", controller.getStage(), true);
    }

    public ArrayList<ArrayList<TeamDto>> getTournamentRoundsTeams() {
        ArrayList<ArrayList<TeamDto>> tournamentRoundsTeams = new ArrayList<>();

        ArrayList<TeamDto> firstRoundTeams = new ArrayList<>();
        for (Integer teamID : tournamentRoundsID.get(0)) {
            firstRoundTeams.add(InfoManager.GetTeam(teamID));
        }
        tournamentRoundsTeams.add(firstRoundTeams);

        for (int i = 1; i < tournamentRoundsID.size(); i++) {
            ArrayList<TeamDto> currentRoundTeams = new ArrayList<>();
            ArrayList<Integer> currentRoundIDs = tournamentRoundsID.get(i);

            if (!currentRoundIDs.isEmpty() && currentRoundIDs.get(0) == null) currentRoundTeams.add(null);

            for (TeamDto team : tournamentRoundsTeams.get(i - 1)) {
                if (currentRoundIDs.contains(team.getID())) {
                    currentRoundTeams.add(team);
                }
            }

            tournamentRoundsTeams.add(currentRoundTeams);
        }

        return tournamentRoundsTeams;//hasta aca

        //return tournamentRounds;
    }

    public void searchForNewTeams(int cuantity) {
        ArrayList<TeamDto> teams = InfoManager.GetTeamList();
        for (TeamDto team : teams) {
            if (team.getSportID() == sportID && !tournamentRoundsID.get(0).contains(team.getID())) {
                tournamentRoundsID.get(0).add(team.getID());
                cuantity--;
                if (cuantity == 0) return;
            }
        }
        totalOfTeams = tournamentRoundsID.get(0).size();
        System.out.println("Por falta de equipos el torneo sera de " + totalOfTeams + " participantes.");
    }

    public void weHaveAWinner(TeamDto team) {
        TournamentController controller = (TournamentController) FlowController.getInstance().getController("TournamentView");
        int updateTheRound = currentRound + 1;

        if (currentMatch % 2 == 0 && nextRoundSize(currentRound) > 1 && nextRoundSize(currentRound) % 2 != 0) { // Check if the first team MAY be single
            tournamentRoundsID.get(currentRound + 1).add(null);
            System.out.println("\nSe añade equipo nulo\n");
        }

        tournamentRoundsID.get(currentRound + 1).add(team.getID());
        System.out.println("\nGanó el equipo: " + team.getName() + "\n");

        currentMatch += 2; // Next match

        if (currentMatch + 1 == tournamentRoundsID.get(currentRound).size()) {// Check if there is a singleteam
            luckyTeam(tournamentRoundsID.get(currentRound).get(currentMatch));
            currentMatch = 0;
            currentRound++;
        }
        controller.updateRound(updateTheRound);
    }

    public int findHowManyRounds() {
        int rounds = 0;
        int teams = tournamentRoundsID.get(0).size();
        while (teams > 1) {
            teams = teams / 2 + teams % 2;
            rounds++;
        }
        return rounds;
    }

    public int nextRoundSize(int roundActual) {
        return tournamentRoundsID.get(roundActual).size() / 2 + tournamentRoundsID.get(roundActual).size() % 2;
    }

    public void luckyTeam(Integer teamID) {
        tournamentRoundsID.get(currentRound + 1).add(teamID);
        System.out.println("\nPasa el equipo: " + InfoManager.GetTeam(teamID).getName() + "\n");
    }
}
