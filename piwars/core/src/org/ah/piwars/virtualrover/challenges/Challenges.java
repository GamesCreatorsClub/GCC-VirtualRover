package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.ModelFactory;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;

import java.util.List;

import static java.util.Arrays.asList;

public class Challenges {

    private List<ChallengeDescription> challenges;

    private PiNoonArena piNoonArena;
    private EcoDisasterArena ecoDisasterArena;
    private CanyonsOfMarsArena canyonsOfMarsArena;
    private StraightLineSpeedTestArena straightLineSpeedTestArena;
    private BlastOffArena blastOffArena;
    private MineSweeperArena mineSweeperArena;

    public Challenges(ModelFactory modelFactory, AssetManager assetManager, ServerCommunicationAdapter serverCommunicationAdapter) {
        piNoonArena = new PiNoonArena(modelFactory);
        ecoDisasterArena = new EcoDisasterArena(modelFactory);
        canyonsOfMarsArena = new CanyonsOfMarsArena(modelFactory, assetManager);
        straightLineSpeedTestArena = new StraightLineSpeedTestArena(modelFactory);
        blastOffArena = new BlastOffArena(modelFactory);
        mineSweeperArena = new MineSweeperArena(modelFactory, serverCommunicationAdapter);

        challenges = asList(
//                new ChallengeDescription("PiNoon", "Pi Noon (Robot vs Robot)", piNoonArena, 2, true),
                new ChallengeDescription("PiNoon", "Pi Noon (Robot vs Robot)", piNoonArena, 2, false),
                new ChallengeDescription("EcoDisaster", "Eco Disaster", ecoDisasterArena, 1, false),
                new ChallengeDescription("CanyonsOfMars", "Canyons Of Mars (Maze)", canyonsOfMarsArena, 1, false),
                new ChallengeDescription("StraightLineSpeedTest", "Straight Line Speed Test", straightLineSpeedTestArena, 1, false),
                new ChallengeDescription("BlastOff", "Blast Off Speed Test", blastOffArena, 1, false),
                new ChallengeDescription("MineSweeper", "MineS weeper", mineSweeperArena, 1, false));

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
        }

        return (T)challenge;
    }
}
