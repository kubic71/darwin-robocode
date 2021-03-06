package vs;


import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


/**
 * Modification of SuperTracker Robocode bot, which loads it's params from a file
 */
public class ParameterizedSuperTracker extends AdvancedRobot {
    int moveDirection = 1;//which way to move

    public static final String PARAM_FILE = "D:\\skola\\IS\\darwin-robocode\\super_tracker_params.conf";

    public double distanceLimit = 150;
    public double changeSpeedProb = 0.1;
    public double speedRange = 12;
    public double minSpeed = 12;

    // Number of colors evolved
    public static final int EVOLVED_COLORS_NUM = 4;
    public double[] colors = new double[EVOLVED_COLORS_NUM];
    
    private Random randomGenerator=new Random(42);


    public void loadParamsFromFile() throws IOException {
        File file = new File(PARAM_FILE);
        Scanner sc = new Scanner(file);
        distanceLimit = Double.parseDouble(sc.nextLine());
        changeSpeedProb = Double.parseDouble(sc.nextLine());
        speedRange = Double.parseDouble(sc.nextLine());
        minSpeed = Double.parseDouble(sc.nextLine());
       
        sc.close();
    }

    public ParameterizedSuperTracker() throws IOException {
        super();
        //best parameters found
        /*
        distanceLimit = 141.96;
        changeSpeedProb = 0.04317;
        speedRange = 2.33;
        minSpeed = 5.64;
         */
        loadParamsFromFile();
    }

    public static Color ColorFromDouble(double d) {
        return new Color(Color.HSBtoRGB((float) d, 1, 1));
    }

    public void run() {
        setAdjustRadarForRobotTurn(true);//keep the radar still while we turn

        setScanColor(Color.white);

        setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
        turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = e.getBearingRadians() + getHeadingRadians();//enemies absolute bearing
        double latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);//enemies later velocity
        double gunTurnAmt;//amount to turn our gun
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
        if (randomGenerator.nextDouble() > (1 - changeSpeedProb)) {
            setMaxVelocity((speedRange *randomGenerator.nextDouble()) + minSpeed);//randomly change speed
        }
        if (e.getDistance() > distanceLimit) {
            gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 22);//amount to turn our gun, lead just a little bit
            setTurnGunRightRadians(gunTurnAmt); //turn our gun
            setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing - getHeadingRadians() + latVel / getVelocity()));//drive towards the enemies predicted future location
            setAhead((e.getDistance() - 140) * moveDirection);//move forward
            setFire(3);//fire
        } else {//if we are close enough...
            gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 15);//amount to turn our gun, lead just a little bit
            setTurnGunRightRadians(gunTurnAmt);//turn our gun
            setTurnLeft(-90 - e.getBearing()); //turn perpendicular to the enemy
            setAhead((e.getDistance() - 140) * moveDirection);//move forward
            setFire(3);//fire
        }
    }

    public void onHitWall(HitWallEvent e) {
        moveDirection = -moveDirection;//reverse direction upon hitting a wall
    }


    /**
     * onWin:  Do a victory dance
     */
    public void onWin(WinEvent e) {
        for (int i = 0; i < 50; i++) {
            turnRight(30);
            turnLeft(30);
        }
    }
}
