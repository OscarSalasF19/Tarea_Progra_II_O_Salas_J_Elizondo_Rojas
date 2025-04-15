 package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.util.AppContext;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TourneyDto {
    
    private StringProperty sport;
    private StringProperty totalOfTeams;
    private int matchTimeSeconds;// se ocupan cambios mayores aca, el tiempo se elige en un reloj improvisado, hay que hacerlo en segundos para este
    private ArrayList<TeamDto> competingTeams;
    
    public TourneyDto() {
        this.sport = new SimpleStringProperty("");
        this.totalOfTeams = new SimpleStringProperty("");
        this.matchTimeSeconds = 10;
        this.competingTeams = new ArrayList<>();
    }

    public String getSport() {
        return sport.get();
    }

    public void setSport(String Deporte) {
        this.sport.set(Deporte);
    }

    public int getTotalOfTeams() {
        if (totalOfTeams.get() != null && !totalOfTeams.get().isEmpty()) {
            return Integer.valueOf(totalOfTeams.get());
        } else {
            return 0;
        }
    }

    public void setTotalOfTeams(int totalOfTeams) {
        this.totalOfTeams.set(String.valueOf(totalOfTeams));
    }

    public int getMatchTimeSeconds() {
        return matchTimeSeconds;
    }

    public void setMatchTimeSeconds(int matchTimeSeconds) {
        this.matchTimeSeconds = matchTimeSeconds;
    }

    public StringProperty getDeporteProperty() {
        return sport;
    }

    public StringProperty getCantEquiposProperty() {
        return totalOfTeams;
    }

    private ArrayList<TeamDto> justSuitableTeams() {
        ArrayList<TeamDto> fullTeamArrayList = (ArrayList<TeamDto>) AppContext.getInstance().get("FullTeamArrayList");

        for(int i = 0; i < fullTeamArrayList.size(); i++){
            if(!fullTeamArrayList.get(i).getSportName().equals(getSport())){
                fullTeamArrayList.remove(i);
            }
        }
        
        return fullTeamArrayList;//array de equipos que tienen un deporte en especifico
    }

    public void createTeamList() { //llamar en un onAction de un botton 
        competingTeams = justSuitableTeams();
        
        while(competingTeams.size() > getTotalOfTeams()){
            competingTeams.remove((int) (Math.random() * competingTeams.size()));//elimina equipos en un orden aleatorio
        }

        startTorney();
    }
    
    private void startTorney() {
        GameDto match = new GameDto(matchTimeSeconds);
        while (competingTeams.size() > 1){
            for (int i = 0; i < competingTeams.size(); i++) {
                match.startGame(competingTeams,i);
            }
        }
    }
        
}

