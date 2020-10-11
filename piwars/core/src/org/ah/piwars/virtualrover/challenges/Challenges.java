package org.ah.piwars.virtualrover.challenges;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.blastoff.BlastOffArena;
import org.ah.piwars.virtualrover.challenges.blastoff.BlastOffScreen;
import org.ah.piwars.virtualrover.challenges.canyonsofmars.CanyonsOfMarsArena;
import org.ah.piwars.virtualrover.challenges.canyonsofmars.CanyonsOfMarsScreen;
import org.ah.piwars.virtualrover.challenges.ecodisaster.EcoDisasterArena;
import org.ah.piwars.virtualrover.challenges.ecodisaster.EcoDisasterScreen;
import org.ah.piwars.virtualrover.challenges.feedthefish.FeedTheFishArena;
import org.ah.piwars.virtualrover.challenges.feedthefish.FeedTheFishScreen;
import org.ah.piwars.virtualrover.challenges.minesweeper.MineSweeperArena;
import org.ah.piwars.virtualrover.challenges.minesweeper.MineSweeperScreen;
import org.ah.piwars.virtualrover.challenges.pinoon.PiNoonArena;
import org.ah.piwars.virtualrover.challenges.pinoon.PiNoonScreen;
import org.ah.piwars.virtualrover.challenges.straightline.StraightLineSpeedTestArena;
import org.ah.piwars.virtualrover.challenges.straightline.StraightLineSpeedTestScreen;
import org.ah.piwars.virtualrover.challenges.tidyupthetoys.TidyUpTheToysArena;
import org.ah.piwars.virtualrover.challenges.tidyupthetoys.TidyUpTheToysScreen;
import org.ah.piwars.virtualrover.challenges.upthegardenpath.UpTheGardenPathArena;
import org.ah.piwars.virtualrover.challenges.upthegardenpath.UpTheGardenPathScreen;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

import java.util.List;

import static java.util.Arrays.asList;

public class Challenges {

    private List<ChallengeDescription> challenges;

    private MainGame mainGame;
    private PlatformSpecific platformSpecific;
    private AssetManager assetManager;
    private SoundManager soundManager;
    private ServerCommunicationAdapter serverCommunicationAdapter;
    private Console console;

    private PiNoonArena piNoonArena;
    private TidyUpTheToysArena tidyUpTheToysArena;
    private FeedTheFishArena feedTheFishArena;
    private UpTheGardenPathArena upTheGardenPathArena;
    private EcoDisasterArena ecoDisasterArena;
    private CanyonsOfMarsArena canyonsOfMarsArena;
    private StraightLineSpeedTestArena straightLineSpeedTestArena;
    private BlastOffArena blastOffArena;
    private MineSweeperArena mineSweeperArena;

    private PiNoonScreen piNoonScreen;
    private EcoDisasterScreen ecoDisasterScreen;
    private TidyUpTheToysScreen tidyUpTheToysScreen;
    private FeedTheFishScreen feedTheFishScreen;
    private UpTheGardenPathScreen upTheGardenPathScreen;
    private CanyonsOfMarsScreen canyonsOfMarsScreen;
    private StraightLineSpeedTestScreen straightLineSpeedTestScreen;
    private BlastOffScreen blastOffScreen;
    private MineSweeperScreen mineSweeperScreen;

    public Challenges(
            MainGame mainGame,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.mainGame = mainGame;
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

//            AssetManager assetManager, ServerCommunicationAdapter serverCommunicationAdapter) {
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

    @SuppressWarnings("unchecked")
    public <T extends ChallengeScreen> T getChallengeScreen(String mapId) {
        ChallengeScreen challengeScreen = null;
        if ("PiNoon".equals(mapId)) {
            if (piNoonScreen == null) {
                piNoonScreen = new PiNoonScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = piNoonScreen;
        } else if ("EcoDisaster".equals(mapId)) {
            if (ecoDisasterScreen == null) {
                ecoDisasterScreen = new EcoDisasterScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = ecoDisasterScreen;
        } else if ("CanyonsOfMars".equals(mapId)) {
            if (canyonsOfMarsScreen == null) {
                canyonsOfMarsScreen = new CanyonsOfMarsScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = canyonsOfMarsScreen;
        } else if ("StraightLineSpeedTest".equals(mapId)) {
            if (straightLineSpeedTestScreen == null) {
                straightLineSpeedTestScreen = new StraightLineSpeedTestScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = straightLineSpeedTestScreen;
        } else if ("BlastOff".equals(mapId)) {
            if (blastOffScreen == null) {
                blastOffScreen = new BlastOffScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = blastOffScreen;
        } else if ("MineSweeper".equals(mapId)) {
            if (mineSweeperScreen == null) {
                mineSweeperScreen = new MineSweeperScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = mineSweeperScreen;
        } else if ("TidyUpTheToys".equals(mapId)) {
            if (tidyUpTheToysScreen == null) {
                tidyUpTheToysScreen = new TidyUpTheToysScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = tidyUpTheToysScreen;
        } else if ("FeedTheFish".equals(mapId)) {
            if (feedTheFishScreen == null) {
                feedTheFishScreen = new FeedTheFishScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = feedTheFishScreen;
        } else if ("UpTheGardenPath".equals(mapId)) {
            if (upTheGardenPathScreen == null) {
                upTheGardenPathScreen = new UpTheGardenPathScreen(mainGame, platformSpecific, assetManager, soundManager, serverCommunicationAdapter, console);
            }
            challengeScreen = upTheGardenPathScreen;
        }

        ChallengeArena challengeArena = getChallengeArena(mapId);
        challengeArena.init();
        challengeScreen.setChallengeArena(challengeArena);
        challengeScreen.reset();
        return (T)challengeScreen;
    }
}
