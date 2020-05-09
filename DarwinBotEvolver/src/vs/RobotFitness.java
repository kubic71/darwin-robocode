package vs;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.DoubleGene;
import robocode.BattleResults;
import robocode.BattleRules;
import robocode.control.*;
import robocode.control.events.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RobotFitness extends FitnessFunction implements IBattleListener {
    public static final String ROBOCODE_LOCATION = "/home/kubik/robocode";

    public static final int BATTLEFIELD_WIDTH = 800;
    public static final int BATTLEFIELD_HEIGHT = 600;

    public final int randSeed = 42;
    public final Random rand = new Random(randSeed);

    RobocodeEngine engine;
    RobotSpecification[] robotSpecs;
    BattlefieldSpecification battleSpec;

    private double lastResult;

    public RobotFitness() {
        // Create the RobocodeEngine
        engine = new RobocodeEngine(new java.io.File(ROBOCODE_LOCATION));

        // Show the Robocode battle view
        engine.setVisible(true);

        // Create the battlefield
        battleSpec = new BattlefieldSpecification(BATTLEFIELD_WIDTH, BATTLEFIELD_HEIGHT);

        engine.addBattleListener(this);
        robotSpecs = engine.getLocalRepository("vs.SuperRamFire*,vs.ParameterizedSuperTracker*");
    }


    @Override
    protected double evaluate(IChromosome iChromosome) {

        int N_ROUNDS = 4;
        int MAX_POSSIBLE_SCORE = 1000;

        double score = 0;
        for(int i = 0; i < N_ROUNDS; i++) {
            score += runOneRound(iChromosome);
        }
        score /= N_ROUNDS;

//        return MAX_POSSIBLE_SCORE - score;
        return  score;
    }

    private double runOneRound(IChromosome iChromosome) {
        try {
            saveChromosome(iChromosome);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup battle parameters
        int numberOfRounds = 1;
        long inactivityTime = 10000;
        double gunCoolingRate = 1.0;
        int sentryBorderSize = 50;
        boolean hideEnemyNames = false;
        RobotSetup[] robotSetups = new RobotSetup[]{getRandomRobotSetup(), getRandomRobotSetup()};
        /* Create and run the battle */
        BattleSpecification battleSpec = new BattleSpecification(this.battleSpec, numberOfRounds, inactivityTime,
                gunCoolingRate, sentryBorderSize, hideEnemyNames, robotSpecs, robotSetups);


        // Run our specified battle and let it run till it is over
        engine.runBattle(battleSpec, true); // waits till the battle finishes
        return lastResult;
    }

    public void destroy() {
        // Cleanup our RobocodeEngine
        engine.close();
    }




    RobotSetup getRandomRobotSetup() {
        return new RobotSetup(rand.nextDouble() * BATTLEFIELD_WIDTH, rand.nextDouble() * BATTLEFIELD_HEIGHT, 0.0);
    }


    void saveChromosome(IChromosome chromosome) throws IOException {
        Gene[] genes = chromosome.getGenes();
        FileWriter fw = new FileWriter(ParameterizedSuperTracker.PARAM_FILE);

        for (Gene gene : genes) {
            DoubleGene dg = (DoubleGene) gene;
            fw.write(Double.toString(dg.doubleValue()) + "\n");
        }

        fw.close();
    }

    @Override
    public void onBattleStarted(BattleStartedEvent battleStartedEvent) {

    }

    @Override
    public void onBattleFinished(BattleFinishedEvent battleFinishedEvent) {

    }

    @Override
    public void onBattleCompleted(BattleCompletedEvent battleCompletedEvent) {
        // we run only 1 round in each fitness evaluation
        for (BattleResults res : battleCompletedEvent.getIndexedResults()) {
            if(res.getTeamLeaderName().equals("vs.ParameterizedSuperTracker*")) {
                lastResult = res.getScore();
                return;
            }
        }

        System.exit(1);
    }

    @Override
    public void onBattlePaused(BattlePausedEvent battlePausedEvent) {

    }

    @Override
    public void onBattleResumed(BattleResumedEvent battleResumedEvent) {

    }

    @Override
    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {

    }

    @Override
    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
    }

    @Override
    public void onTurnStarted(TurnStartedEvent turnStartedEvent) {

    }

    @Override
    public void onTurnEnded(TurnEndedEvent turnEndedEvent) {

    }

    @Override
    public void onBattleMessage(BattleMessageEvent battleMessageEvent) {

    }

    @Override
    public void onBattleError(BattleErrorEvent battleErrorEvent) {

    }
}
