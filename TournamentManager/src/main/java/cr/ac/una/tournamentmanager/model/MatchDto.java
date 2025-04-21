package cr.ac.una.tournamentmanager.model;

public class MatchDto {

    private TeamDto fstTeam;
    private TeamDto sndTeam;

    public MatchDto(TeamDto fstTeam, TeamDto sndTeam) {
        this.fstTeam = fstTeam;
        this.sndTeam = sndTeam;
    }

    public TeamDto getFstTeam() {
        return fstTeam;
    }

    public void setFstTeam(TeamDto fstTeam) {
        this.fstTeam = fstTeam;
    }

    public TeamDto getSndTeam() {
        return sndTeam;
    }

    public void setSndTeam(TeamDto sndTeam) {
        this.sndTeam = sndTeam;
    }

    public TeamDto tieBreaker() {
        if (fstTeam.getPerformance() > sndTeam.getPerformance()) {
            return fstTeam;
        } else if (sndTeam.getPerformance() > fstTeam.getPerformance()) {
            return sndTeam;
        } else {
            return fstTeam;
        }
    }

}
