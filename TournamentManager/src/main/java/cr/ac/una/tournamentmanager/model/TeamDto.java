package cr.ac.una.tournamentmanager.model;

import java.util.Objects;


public class TeamDto {

    private String name = "";
    private String teamImageURL = "";
    private int sportID = 0; // sustituye por el id del deporte
    private int globalPoins = 0; //de la clasificacion general
    private int performance; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate

    public TeamDto() {
        this.performance = (int) (Math.random() * 100) + 1;
    }

    public TeamDto(String nombre) {
        this.name = nombre;
        this.sportID = 1;
        this.performance = (int) (Math.random() * 100) + 1;
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

    public void setPerformance(int performance) {
        this.performance = performance;
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

}
