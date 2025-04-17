package cr.ac.una.tournamentmanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;


public class TeamDto {

    private final StringProperty name;
    private final StringProperty teamImageURL;
    private final StringProperty sportName;
    private int globalPoins; //de la clasificacion general
    private int performance; //va de 1 a 100 entre mayor sea mayor posibilidad de ganar el desempate

    public TeamDto() {
        this.name = new SimpleStringProperty("");
        this.teamImageURL = new SimpleStringProperty("");
        this.sportName = new SimpleStringProperty("");
        this.globalPoins = 0;
        this.performance = (int) (Math.random() * 100) + 1;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String nombre) {
        this.name.set(nombre);
    }

    public String getTeamImageURL() {
        return teamImageURL.get();
    }

    public void setTeamImageURL(String foto) {
        this.teamImageURL.set(foto);
    }

    public String getSportName() {
        return sportName.get();
    }

    public void setSportName(String sport) {
        this.sportName.set(sport);
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

    public StringProperty getNameProperty() {
        return name;
    }

    public StringProperty getTeamImageURLProperty() {
        return teamImageURL;
    }

    public StringProperty getSportNameProperty() {
        return sportName;
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
