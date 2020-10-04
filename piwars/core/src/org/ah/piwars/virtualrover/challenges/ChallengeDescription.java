package org.ah.piwars.virtualrover.challenges;

public class ChallengeDescription {

    private String name;

    private String year;

    private String description;

    private ChallengeArena challengeArena;

    private boolean remote = false;

    private int maxRovers = 1;

    public ChallengeDescription(String name, String year, String description, ChallengeArena challengeArena, int maxRovers, boolean remote) {
        this.name = name;
        this.year = year;
        this.description = description;
        this.challengeArena = challengeArena;
        this.maxRovers = maxRovers;
        this.remote = remote;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public ChallengeArena getChallengeArena() {
        return challengeArena;
    }

    public boolean isRemote() {
        return remote;
    }

    public int getMaxRovers() {
        return maxRovers;
    }
}
