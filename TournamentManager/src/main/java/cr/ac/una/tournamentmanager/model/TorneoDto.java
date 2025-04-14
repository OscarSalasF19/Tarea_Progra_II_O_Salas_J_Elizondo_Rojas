 package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.util.AppContext;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TorneoDto {
    
    private StringProperty Deporte;
    private StringProperty cantEquipos;
    private int matchTimeSeconds;// se ocupan cambios mayores aca, el tiempo se elige en un reloj improvisado, hay que hacerlo en segundos para este
    private ArrayList<EquipoDto> competingTeams;
    
    public TorneoDto() {
        this.Deporte = new SimpleStringProperty("");
        this.cantEquipos = new SimpleStringProperty("");
        this.matchTimeSeconds = 10;
        this.competingTeams = new ArrayList<>();
    }

    public String getDeporte() {
        return Deporte.get();
    }

    public void setDeporte(String Deporte) {
        this.Deporte.set(Deporte);
    }

    public int getCantEquipos() {
        if (cantEquipos.get() != null && !cantEquipos.get().isEmpty()) {
            return Integer.valueOf(cantEquipos.get());
        } else {
            return 0;
        }
    }

    public void setCantEquipos(int cantEquipos) {
        this.cantEquipos.set(String.valueOf(cantEquipos));
    }

    public int getMatchTimeSeconds() {
        return matchTimeSeconds;
    }

    public void setMatchTimeSeconds(int matchTimeSeconds) {
        this.matchTimeSeconds = matchTimeSeconds;
    }

    public StringProperty getDeporteProperty() {
        return Deporte;
    }

    public StringProperty getCantEquiposProperty() {
        return cantEquipos;
    }

    private ArrayList<EquipoDto> justSuitableTeams() {
        ArrayList<EquipoDto> fullTeamArrayList = (ArrayList<EquipoDto>) AppContext.getInstance().get("FullTeamArrayList");
        
        for(int i = 0; i < fullTeamArrayList.size(); i++){
            if(!fullTeamArrayList.get(i).getSportName().equals(getDeporte())){
                fullTeamArrayList.remove(i);
            }
        }
        
        return fullTeamArrayList;//array de equipos que tienen un deporte en especifico
    }

    public void createTeamList() { //llamar en un onAction de un botton 
        competingTeams = justSuitableTeams();
        
        while(competingTeams.size() > getCantEquipos()){
            competingTeams.remove((int) (Math.random() * competingTeams.size()));//elimina equipos en un orden aleatorio
        }

        startTorney();
    }
    
    private void startTorney() {
        PartidoDto match = new PartidoDto(matchTimeSeconds);
        while (competingTeams.size() > 1){
            for (int i = 0; i < competingTeams.size(); i++) {
                match.startGame(competingTeams,i);
            }
        }
    }
        
}

