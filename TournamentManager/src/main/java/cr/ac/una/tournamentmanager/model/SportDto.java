package cr.ac.una.tournamentmanager.model;

import java.util.ArrayList;
import java.util.Objects;

public class SportDto {

    private final int sportID;
    private String name;
    private String ballImageURL;

    public SportDto() {
        this.sportID = newID();
        this.name = "";
        this.ballImageURL = "";
    }

    public int getID() {
        return sportID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBallImageURL() {
        return ballImageURL;
    }

    public void setBallImageURL(String ballImageURL) {
        this.ballImageURL = ballImageURL;
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
        final SportDto other = (SportDto) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "SportDto{" + "name=" + name + ", ballImageURL=" + ballImageURL + '}';
    }

    private int newID() {
        ArrayList<SportDto> fullSportArrayList = InfoManager.GetSportList();
        if (fullSportArrayList == null) {
            return 1;
        }
        int maxId = 0;
        for (SportDto sport : fullSportArrayList) {
            if (sport.getID() > maxId) {
                maxId = sport.getID();
            }
        }
        return maxId + 1;
    }

}
