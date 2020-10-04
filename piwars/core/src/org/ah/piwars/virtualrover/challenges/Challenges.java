package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.ServerCommunicationAdapter;

import java.util.List;

import static java.util.Arrays.asList;

public class Challenges {

    private List<ChallengeDescription> challenges;

    private PiNoonArena piNoonArena;
    private TidyUpTheToysArena tidyUpTheToysArena;
    private FeedTheFishArena feedTheFishArena;
    private UpTheGardenPathArena upTheGardenPathArena;
    private EcoDisasterArena ecoDisasterArena;
    private CanyonsOfMarsArena canyonsOfMarsArena;
    private StraightLineSpeedTestArena straightLineSpeedTestArena;
    private BlastOffArena blastOffArena;
    private MineSweeperArena mineSweeperArena;

    public Challenges(AssetManager assetManager, ServerCommunicationAdapter serverCommunicationAdapter) {
        piNoonArena = new PiNoonArena(assetManager);
        tidyUpTheToysArena = new TidyUpTheToysArena(assetManager);
        feedTheFishArena = new FeedTheFishArena(assetManager);
        upTheGardenPathArena = new UpTheGardenPathArena(assetManager);
        ecoDisasterArena = new EcoDisasterArena(assetManager);
        canyonsOfMarsArena = new CanyonsOfMarsArena(assetManager);
        straightLineSpeedTestArena = new StraightLineSpeedTestArena(assetManager);
        blastOffArena = new BlastOffArena(assetManager);
        mineSweeperArena = new MineSweeperArena(assetManager, serverCommunicationAdapter);

        challenges = asList(
//                new ChallengeDescription("PiNoon", "Pi Noon (Robot vs Robot)", piNoonArena, 2, true),
                new ChallengeDescription("PiNoon", "", "Pi Noon (Robot vs Robot)", piNoonArena, 2, false),
                new ChallengeDescription("TidyUpTheToys", "2021", "Tidy Up The Toys", tidyUpTheToysArena, 1, false),
                new ChallengeDescription("FeedTheFish", "2021", "Feed The Fish", feedTheFishArena, 1, false),
                new ChallengeDescription("UpTheGardenPath", "2021", "Up The Garden Path", upTheGardenPathArena, 1, false),
                new ChallengeDescription("EcoDisaster", "2020", "Eco Disaster", ecoDisasterArena, 1, false),
                new ChallengeDescription("MineSweeper", "2020", "MineS weeper", mineSweeperArena, 1, false),
                new ChallengeDescription("CanyonsOfMars", "2019", "Canyons Of Mars (Maze)", canyonsOfMarsArena, 1, false),
                new ChallengeDescription("BlastOff", "2019", "Blast Off Speed Test", blastOffArena, 1, false),
                new ChallengeDescription("StraightLineSpeedTest", "2018", "Straight Line Speed Test", straightLineSpeedTestArena, 1, false)
        );

        for (ChallengeDescription challenge : challenges) {
            challenge.getChallengeArena().init();
        }
    }

    public List<ChallengeDescription> getAvailableChallenges() {
        return challenges;
    }

    @SuppressWarnings("unchecked")
    public <T extends ChallengeArena> T getChallengeArena(String mapId) {
        ChallengeArena challenge = null;
        if ("PiNoon".equals(mapId)) {
            challenge = piNoonArena;
        } else if ("EcoDisaster".equals(mapId)) {
            challenge = ecoDisasterArena;
        } else if ("CanyonsOfMars".equals(mapId)) {
            challenge = canyonsOfMarsArena;
        } else if ("StraightLineSpeedTest".equals(mapId)) {
            challenge = straightLineSpeedTestArena;
        } else if ("BlastOff".equals(mapId)) {
            challenge = blastOffArena;
        } else if ("MineSweeper".equals(mapId)) {
            challenge = mineSweeperArena;
        } else if ("TidyUpTheToys".equals(mapId)) {
            challenge = tidyUpTheToysArena;
        } else if ("FeedTheFish".equals(mapId)) {
            challenge = feedTheFishArena;
        } else if ("UpTheGardenPath".equals(mapId)) {
            challenge = upTheGardenPathArena;
        }

        return (T)challenge;
    }
}
