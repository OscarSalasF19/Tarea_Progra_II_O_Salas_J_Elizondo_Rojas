package cr.ac.una.tournamentmanager.model;

import java.util.ArrayList;
import java.util.Objects;


public class TeamDto {

    private int id = 0; //id del equipo
    private String name = "";
    private String teamImageURL = "";
    private int sportID = 0; // sustituye por el id del deporte
    private int globalPoins = 0; //de la clasificacion general
    private int performance; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate
    private int scores = 0; //de la clasificacion general
    private int wins = 0; //de la clasificacion general
    private int losses = 0; //de la clasificacion general
    private int ties = 0; //de la clasificacion general

    public TeamDto() {
        this.performance = (int) (Math.random() * 100) + 1;
        newId();
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public String getTeamImageURL() {
        return teamImageURL;
    }

    public void setTeamImageURL(String foto) {
        this.teamImageURL = foto;
    }

    public int getSportID() {
        return sportID;
    }

    public void setSportID(int sportID) {
        this.sportID = sportID;
    }

    public int getPoints() {
        return globalPoins;
    }

    public void setPoints(int puntos) {
        this.globalPoins = puntos;
    }

    public int getPerformance() {
        return this.performance;
    }

    public int getScores() {
        return this.scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }
    
    public int getWins() {
        return this.wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
    
    public int getLosses() {
        return this.losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
    
     public int getTies() {
        return this.ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public void sumPoints(int valor) {
        valor += getPoints();
        setPoints(valor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TeamDto other = (TeamDto) obj;
        return Objects.equals(this.getName(), other.getName());
    }

    private void newId() {
        ArrayList<TeamDto> fullTeamArrayList = InfoManager.GetTeamList();
        if (fullTeamArrayList.isEmpty()) {
            this.id = 1;
        } else {
            int maxId = 0;
            for (TeamDto team : fullTeamArrayList) {
                if (team.getID() > maxId) {
                    maxId = team.getID();
                }
            }
            this.id = maxId + 1;
        }
    }

}
