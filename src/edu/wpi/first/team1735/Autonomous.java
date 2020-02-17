/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.team1735;

import edu.wpi.first.team1735.drive.DeadDriveController;
import edu.wpi.first.team1735.drive.JoeDriveline;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Administrator
 */
public class Autonomous extends Thread {

    private int mode;
    private JoeDriveline driveline;
    private Joystick joystick;
    private Team1735 robot;
    public boolean autoModeBegun;
    public boolean teleopModeBegun;
    public boolean kicking;

    public Autonomous(JoeDriveline driveline, Joystick joystick, Team1735 robot) {
        this.driveline = driveline;
        this.autoModeBegun = false;
        this.teleopModeBegun = false;
        this.joystick = joystick;
        this.robot = robot;
        kicking = false;
        start();
    }

    public void run() {
        boolean flag = false;
        boolean flag2 = false;
        int autodelay = 0;
        boolean blinkflag = false;
        //Whiteboard.write(1, "Team 1735");
        while (!this.autoModeBegun && !this.teleopModeBegun) {
            if (this.joystick.getTrigger()) {
                if (!flag) {
                    mode++;
                   // Whiteboard.write(1, "1");
                    flag = true;
                    if (mode > 5) {
                        mode = 0;
                    }
                }
            } else {
                flag = false;
            }
            if (this.joystick.getRawButton(2)) {
                if (!flag2) {
                    autodelay++;
                    if (autodelay > 12) {
                        autodelay = 0;
                    }
                    flag2 = true;
                } else {
                    flag2 = false;
                }
            }
            Whiteboard.write(2, "Auto Delay: " + autodelay);
            if (mode == 0) {
                if(blinkflag) {
                    Whiteboard.write(1, "No AUTONOMOUS");
                    //Whiteboard.write(2, "No AUTONOMOUS");
                    Whiteboard.write(3, "No AUTONOMOUS");
                    //Whiteboard.write(4, "No AUTONOMOUS");
                    Whiteboard.write(5, "No AUTONOMOUS");
                    //Whiteboard.write(6, "No AUTONOMOUS");
                    blinkflag = false;
                } else {
                    Whiteboard.write(1, "");
                   // Whiteboard.write(2, "");
                    Whiteboard.write(3, "");
                  //  Whiteboard.write(4, "");
                    Whiteboard.write(5, "");
                  //  Whiteboard.write(6, "");
                    blinkflag = true;
                }
            } else if (mode == 1) {
                Whiteboard.write(1, "Drive Straight");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 2) {
                Whiteboard.write(1, "Shoot Once");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 3) {
                Whiteboard.write(1, "Shoot Twice - Long Drive");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 4) {
                Whiteboard.write(1, "Spin!");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            } else if (mode == 5) {
                Whiteboard.write(1, "Drive short distance");
                Whiteboard.write(3, "");
                Whiteboard.write(5, "");
            }
            Timer.delay(.05);
        }

        if(!this.teleopModeBegun) {
            driveline.setDriveController(new DeadDriveController());
            driveline.resetGyro();
            driveline.enable();

            Timer.delay(autodelay);

            System.out.println("Passed the point");

            if (mode == 1) { //shoot twice
                Timer.delay(1);
                //claw down a little
                robot.wrist.set(-.3);

                Timer.delay(.1);
                //Drive Straight
                robot.wrist.set(0);
                driveline.fieldOrientedAngle(.5, 0, 0);//.75

                Timer.delay(2.45);//1.7
                //Stop Driving, lift elevator
                driveline.fieldOrientedAngle(0, 0, 0);
                robot.elevator1.set(.5);
                robot.elevator2.set(.5);

                Timer.delay(4);
                //Drive straight
                driveline.fieldOrientedAngle(.50, 0, 0);

                Timer.delay(.5);
                driveline.fieldOrientedAngle(0,0,0);
                robot.wrist.set(-.3);

                Timer.delay(.4);
                robot.wrist.set(0);
                robot.elevator1.set(-.3);
                robot.elevator2.set(-.3);
                robot.rollersbottom.set(.5);
                robot.rollerstop.set(.5);

                Timer.delay(1.2);
                driveline.fieldOrientedAngle(-.5, 0, 0);
                robot.elevator1.set(0);
                robot.elevator2.set(0);

                Timer.delay(.8);
                driveline.fieldOrientedAngle(0, 0, 0);

            }
        }

        driveline.fieldOrientedAngle(0, 0, 0);
    }
}
