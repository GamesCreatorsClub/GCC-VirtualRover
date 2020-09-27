package org.ah.piwars.virtualrover.screens;

import com.badlogic.gdx.assets.AssetManager;

import org.ah.piwars.virtualrover.MainGame;
import org.ah.piwars.virtualrover.PlatformSpecific;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.challenges.ChallengeArena;
import org.ah.piwars.virtualrover.challenges.Challenges;
import org.ah.piwars.virtualrover.utils.SoundManager;
import org.ah.piwars.virtualrover.view.Console;

public class ChallengeScreens {

    private MainGame mainGame;
    private PlatformSpecific platformSpecific;
    private Challenges challenges;
    private AssetManager assetManager;
    private SoundManager soundManager;
    private ServerCommunicationAdapter serverCommunicationAdapter;
    private Console console;

    private PiNoonScreen piNoonScreen;
    private EcoDisasterScreen ecoDisasterScreen;
    private TidyUpTheToysScreen tidyUpTheToysScreen;
    private FeedTheFishScreen feedTheFishScreen;
    private UpTheGardenPathScreen upTheGardenPathScreen;
    private CanyonsOfMarsScreen canyonsOfMarsScreen;
    private StraightLineSpeedTestScreen straightLineSpeedTestScreen;
    private BlastOffScreen blastOffScreen;
    private MineSweeperScreen mineSweeperScreen;

    public ChallengeScreens(
            MainGame mainGame,
            PlatformSpecific platformSpecific,
            Challenges challenges,
            AssetManager assetManager,
            SoundManager soundManager,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console
            ) {
                this.mainGame = mainGame;
                this.platformSpecific = platformSpecific;
                this.challenges = challenges;
                this.assetManager = assetManager;
                this.soundManager = soundManager;
                this.serverCommunicationAdapter = serverCommunicationAdapter;
                this.console = console;
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

        ChallengeArena challengeArena = challenges.getChallengeArena(mapId);
        challengeArena.init();
        challengeScreen.setChallengeArena(challengeArena);
        challengeScreen.reset();
        return (T)challengeScreen;
    }
}
