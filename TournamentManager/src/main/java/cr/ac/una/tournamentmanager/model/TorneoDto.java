package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.util.AppContext;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class TorneoDto {
    
    private ObjectProperty<DeporteDto> Deporte;
    private StringProperty cantEquipos;
    private int matchTimeSeconds;// se ocupan cambios mayores aca, el tiempo se elige en un reloj improvisado, hay que hacerlo en segundos para este
    private ArrayList<EquipoDto> competingTeams;
    
    public TorneoDto() {
        this.Deporte = new SimpleObjectProperty<>();
        this.cantEquipos = new SimpleStringProperty("");
        this.matchTimeSeconds = 10;
        this.competingTeams = new ArrayList<>();
    }

    public DeporteDto getDeporte() {
        return Deporte.get();
    }

    public void setDeporte(DeporteDto Deporte) {
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

    public ObjectProperty<DeporteDto> getDeporteProperty() {
        return Deporte;
    }

    public StringProperty getCantEquiposProperty() {
        return cantEquipos;
    }
//hace falta crear una instancia de AppContext con un Array/HashMap con TODOS los equipos
    private boolean checkForHowManyTeams() {//hace falta que compruebe que son del mismo deporte
        return AppContext.getInstance().get("FullTeamArrayList").size() > getCantEquipos();
    }

    private void createTeamList() { //lamar en un onAction de un botton 
        if(checkForHowManyTeams()) {
            int index = 0;
            while(competingTeams.size() < getCantEquipos()){
                index = 0; //numero random de la cantidad global de equipos (de preferencia que no se repitan)
                if (AppContext.getInstance().get("FullTeamArrayList").get(index).getSport().equals(getDeporte())) {
                    competingTeams.add(AppContext.getInstance().get("FullTeamArrayList").get(index));
                }
            }
        } else {
            //no existen suficientes equipos registrados en "deporte"
        }
    }
    
    
    
}

