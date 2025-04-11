package cr.ac.una.tournamentmanager.model;

import cr.ac.una.tournamentmanager.util.AppContext;
import java.util.ArrayList;
import java.util.Collections;
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
        
        //eliminar estas 3 lineas
        ArrayList<EquipoDto> FullTeamList = new ArrayList<>();
        FullTeamList.add(new EquipoDto());
        AppContext.getInstance().set("FullTeamArrayList", FullTeamList );
        
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
//hace falta crear una instancia de AppContext con un Array con TODOS los equipos
    private boolean checkForHowManyTeams() {//hace falta que compruebe que son del mismo deporte
        return AppContext.getInstance().get("FullTeamArrayList").size > getCantEquipos();
    }

    public void createTeamList() { //lamar en un onAction de un botton 
        if(checkForHowManyTeams() && getCantEquipos() >= 2) {
            ArrayList<Integer> indexList;
            indexList = randomNumberList(AppContext.getInstance().get("FullTeamArrayList").size() - 1);
            
            for (int i = 0; competingTeams.size() < getCantEquipos(); i++){
                if (AppContext.getInstance().get("FullTeamArrayList").get(indexList.get(i)).getSport().equals(getDeporte())) {
                    
                    competingTeams.add(AppContext.getInstance().get("FullTeamArrayList").get(indexList));
                }
            }
            startTorney();
        } else {
            //no existen suficientes equipos registrados en "deporte" o no deben ser más para iniciar el torneo
        }
    }
    
    private void startTorney() {
        PartidoDto match = new PartidoDto(matchTimeSeconds);
        while (competingTeams.size() > 1){
            for (int i = 0; i < competingTeams.size(); i++) {
                match.startGame(competingTeams,i);
            }
        }
    }
    
    private ArrayList<Integer> randomNumberList(int x){
        ArrayList<Integer> lista = new ArrayList<>();
        for (int i = 0; i <= x; i++) {
            lista.add(i);// lista: 0 1 2 3 ... x
        }
        Collections.shuffle(lista);// revuelve la lista
        return lista;
    }
    
}

