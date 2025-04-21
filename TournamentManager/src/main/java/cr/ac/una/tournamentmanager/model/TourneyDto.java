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

    private final ArrayList<ArrayList<TeamDto>> tournamentRounds = new ArrayList<>();
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
        tournamentRounds.add(teams);
        currentRound = 0;
        currentMatch = 0;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        this.creationDate = now.format(dateFormatter);
        this.creationTime = now.format(timeFormatter);

        Iterator<TeamDto> iterator = tournamentRounds.get(0).iterator();
        while (iterator.hasNext()) {
            TeamDto team = iterator.next();
            if (team.getSportID() != sportID) {
                System.out.println("El equipo " + team.getName() + " no es del deporte " + InfoManager.GetSport(sportID).getName());
                iterator.remove();
            }
        }

        if (tournamentRounds.get(0).size() < totalOfTeams) {
            System.out.println("Se van a anadir equipos aptos para el torneo");
            searchForNewTeams(totalOfTeams - tournamentRounds.get(0).size());
        }

        if (totalOfTeams == 1) {
            System.out.println("El torneo no se puede realizar, solo hay un equipo.");
            numberOfRounds = 0;
            return;
        }

        this.numberOfRounds = findHowManyRounds();

        for (int i = 0; i < numberOfRounds; i++) {
            tournamentRounds.add(new ArrayList<>());
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
        if (currentMatch >= tournamentRounds.get(currentRound).size()) {
            currentMatch = 0;
            currentRound++;
        }

        if (tournamentRounds.get(currentRound).size() == 1) {
            System.out.println("El torneo ha terminado");

            ArrayList<TourneyDto> tournaments = InfoManager.GetTournamentList();
            if (!tournaments.isEmpty() && tournaments.get(tournaments.size() - 1).equals(this)) {
                System.out.println("El torneo ya fue guardado previamente.");
                return;
            }

            System.out.println("⚠️ Se va a crear el certificado del ganador...");
            TeamDto winner = tournamentRounds.get(currentRound).get(0);
            InfoManager.createWinnerCertificate(winner, this);

            tournaments.add(this);
            InfoManager.SetTournamentList(tournaments);
            System.out.println("\nTorneo guardado con exito");

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
            tournamentController.winnerAnimation(tournamentRounds.get(currentRound).get(0));//llama a la animacion y le manda el equipo ganador

            return;
        }

        if (tournamentRounds.get(currentRound).get(currentMatch) == null && currentMatch == 0) {//check if the first team is single
            luckyTeam(tournamentRounds.get(currentRound).get(currentMatch + 1));
            currentMatch += 2;
        }

        FlowController.getInstance().limpiarLoader("MatchView");

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

        if (currentMatch == 0 && nextRoundSize(currentRound) != 1 && nextRoundSize(currentRound) % 2 != 0) { // Check if the first team MAY be single
            tournamentRounds.get(currentRound + 1).add(null);
            System.out.println("\nSe añade equipo nulo\n");
        }

        tournamentRounds.get(currentRound + 1).add(team);
        System.out.println("\nGanó el equipo: " + team.getName() + "\n");

        currentMatch += 2; // Next match

        if (currentMatch + 1 == tournamentRounds.get(currentRound).size()) {// Check if there is a singleteam
            luckyTeam(tournamentRounds.get(currentRound).get(currentMatch));
            currentMatch = 0;
            currentRound++;
        }
        controller.updateRound(updateTheRound);
    }

    public int findHowManyRounds() {
        int rounds = 0;
        int teams = tournamentRounds.get(0).size();
        while (teams > 1) {
            teams = teams / 2 + teams % 2;
            rounds++;
        }
        return rounds;
    }

    public int nextRoundSize(int roundActual) {
        return tournamentRounds.get(roundActual).size() / 2 + tournamentRounds.get(roundActual).size() % 2;
    }

    public void luckyTeam(TeamDto team) {
        tournamentRounds.get(currentRound + 1).add(team);
        System.out.println("\nPasa el equipo: " + team.getName() + "\n");
    }

}
