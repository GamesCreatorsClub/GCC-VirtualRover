package org.ah.piwars.virtualrover.challenges;

public class ChallengeDescription {

    private String name;

    private String description;

    private ChallengeArena challengeArena;

    private boolean remote = false;

    private int maxRovers = 1;

    public ChallengeDescription(String name, String description, ChallengeArena challengeArena, int maxRovers, boolean remote) {
        this.name = name;
        this.description = description;
        this.challengeArena = challengeArena;
        this.maxRovers = maxRovers;
        this.remote = remote;
    }

    public String getName() {
        return name;
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
