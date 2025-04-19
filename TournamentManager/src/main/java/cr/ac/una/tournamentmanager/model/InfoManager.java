/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.tournamentmanager.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cr.ac.una.tournamentmanager.util.AppContext;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfoManager {

    private static final Logger LOGGER = Logger.getLogger(InfoManager.class.getName());

    public InfoManager() {

    }

    private void saveSports() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Sports.json")) {
            gson.toJson(AppContext.getInstance().get("fullSportArrayList"), writer);
            writer.flush();
            System.out.println("All sports were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving sports", s);
        }
    }

    private void LoadSports() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type sportList = new TypeToken<ArrayList<SportDto>>() {}.getType();
        ArrayList<SportDto> sports = new ArrayList<>();

        try (FileReader reader = new FileReader("Sports.json")) {
            sports = gson.fromJson(reader, sportList);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading sports", e);
        }
        AppContext.getInstance().set("fullSportArrayList", sports);
    }

    private void SaveTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Tournament.json")) {
            gson.toJson(AppContext.getInstance().get("FullTournamentsArrayList"), writer);
            writer.flush();
            System.out.println("All tournaments were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving tournaments", s);
        }
    }

    private void loadTournaments() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type tournamentList = new TypeToken<ArrayList<TourneyDto>>() {}.getType();
        ArrayList<TourneyDto> tournaments = new ArrayList<>();

        try (FileReader reader = new FileReader("Sports.json")) {
            tournaments = gson.fromJson(reader, tournamentList);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading tournaments", e);
        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }

    private void SaveTeams() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("Teams.json")) {
            gson.toJson(AppContext.getInstance().get("FullTeamArrayList"), writer);
            writer.flush();
            System.out.println("All teams were saved successfully.");
        } catch (IOException s) {
            LOGGER.log(Level.SEVERE, "Error saving teams", s);
        }
    }

    private void LoadTeams() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type teamsList = new TypeToken<ArrayList<TeamDto>>() {}.getType();
        ArrayList<TeamDto> teams = new ArrayList<>();

        try (FileReader reader = new FileReader("Teams.json")) {
            teams = gson.fromJson(reader, teamsList);
        } catch (IOException s) {
            LOGGER.log(Level.WARNING, "Error loading teams", s);
        }
        AppContext.getInstance().set("FullTeamArrayList", teams);
    }

    public void LoadInfo() {
        LoadSports();
        LoadTeams();
        loadTournaments();
    }

    public void saveInfo() {
        SaveTeams();
        SaveTournaments();
        saveSports();
    }

    public static ArrayList<SportDto> GetSportList() {
        ArrayList<SportDto> sports = (ArrayList<SportDto>) AppContext.getInstance().get("fullSportArrayList");
        if (sports == null) {
            sports = new ArrayList<SportDto>();
            AppContext.getInstance().set("fullSportArrayList", sports);
        }
        return sports;
    }

    public static ArrayList<TeamDto> GetTeamList() {
        ArrayList<TeamDto> teams = (ArrayList<TeamDto>) AppContext.getInstance().get("fullTeamArrayList");
        if (teams == null) {
            teams = new ArrayList<TeamDto>();
            AppContext.getInstance().set("fullTeamArrayList", teams);
        }
        return teams;
    }

    public static ArrayList<TourneyDto> GetTournamentList() {
        ArrayList<TourneyDto> tournaments = (ArrayList<TourneyDto>) AppContext.getInstance().get("fullTournamentsArrayList");
        if (tournaments == null) {
            tournaments = new ArrayList<TourneyDto>();
            AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
        }
        return tournaments;
    }

    public static void SetSportList(ArrayList<SportDto> sports) {
        if (sports == null) {
            sports = new ArrayList<SportDto>();
        }
        AppContext.getInstance().set("fullSportArrayList", sports);
    }

    public static void SetTeamList(ArrayList<TeamDto> teams) {
        if (teams == null) {
            teams = new ArrayList<TeamDto>();
        }
        AppContext.getInstance().set("fullTeamArrayList", teams);
    }

    public static void SetTournamentList(ArrayList<TourneyDto> tournaments) {
        if (tournaments == null) {
            tournaments = new ArrayList<TourneyDto>();
        }
        AppContext.getInstance().set("fullTournamentsArrayList", tournaments);
    }

    public static String GetSportName(int sportID) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getID() == sportID) {
                return sport.getName();
            }
        }
        return "";
    }

    public static int GetSportID(String sportName) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getName().equalsIgnoreCase(sportName)) {
                return sport.getID();
            }
        }
        return 0;
    }

    public static String GetSportImage(int sportID) {
        ArrayList<SportDto> sports = GetSportList();
        for (SportDto sport : sports) {
            if (sport.getID() == sportID) {
                return sport.getBallImageURL();
            }
        }
        return "";
    }
}
