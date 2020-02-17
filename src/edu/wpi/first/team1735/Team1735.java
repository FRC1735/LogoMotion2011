/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.team1735;

import edu.wpi.first.team1735.drive.DeadDriveController;
import edu.wpi.first.team1735.drive.DriveController;
import edu.wpi.first.team1735.drive.JoeDriveline;
import edu.wpi.first.team1735.drive.JoystickDrive;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Watchdog;
//import edu.wpi.first.wpilibj.camera.AxisCamera;
//import java.io.PrintStream;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Team1735 extends IterativeRobot {

    Joystick driveJS = new JoeJoystick(1);
    Joystick twistJS = new JoeJoystick(2);
    Joystick operatorJS = new Joystick(3);
    //RobotDrive drive = new RobotDrive(5, 4, 2, 3);
    Jaguar elevator1 = new Jaguar(5);
    Jaguar elevator2 = new Jaguar(6);
    Jaguar wrist = new Jaguar(9);
    Jaguar rollerstop = new Jaguar(8);
    Jaguar rollersbottom = new Jaguar(7);
    Jaguar minibot = new Jaguar(10);
    Gyro gyro = new JoeGyro(1);
    AnalogChannel pot = new AnalogChannel(2);
    DigitalInput clawup = new DigitalInput(2);
    DigitalInput clawdown = new DigitalInput(1);
    DigitalInput obtained = new DigitalInput(3);
    JoeDriveline drive;
    DriveController joystickDrive;
    Autonomous autonomous;
    //AxisCamera camera = new AxisCamera(1);



    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        //drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        //gyro.setSensitivity(.0136);
        gyro.setSensitivity(.0125);
        gyro.reset();

        //AxisCamera cam = AxisCamera.getInstance();
        //cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        //cam.writeBrightness(0);
        drive = new JoeDriveline(gyro);
        this.joystickDrive = new JoystickDrive(driveJS, twistJS);
        
        joystickDrive.fieldOriented = true;
        drive.fieldOriented(0,0,0);
        this.autonomous = new Autonomous(drive, operatorJS, this);

    }



    /**
     * This function is called periodically during autonomous
     */
    boolean started = true;
    public void autonomousPeriodic() {
        //Feed the watchdog and make sure the driveline is enabled
        Watchdog.getInstance().feed();
        if(started) {
            drive.resetGyro();
            started=false;
           drive.enable();
        }
        this.autonomous.autoModeBegun = true;
    }

    int limitcount = 0;
    public void disabledPeriodic() {
        drive.disable();
        Whiteboard.write(4, (clawdown.get() ? "Claw Down " : "Claw Up ") + limitcount++);


        super.disabledPeriodic();
        //DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser4, 1, "Gyro " + gyro.getAngle());
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Watchdog.getInstance().feed();
        this.autonomous.teleopModeBegun = true;
        drive.enable();
        drive.setDriveController(joystickDrive);
        //this.autonomous.autoModeBegun = true;
        //System.out.println("Pot: " + pot.getValue() + "\tGyro: " + gyro.getAngle());

        if (driveJS.getRawButton(10) || twistJS.getRawButton(10)) {
            drive.resetGyro();
        }

        //System.out.println("hi");

        if (operatorJS.getRawButton(4)) {
            //low level
            if (pot.getValue() < 766) { // too high
                elevator1.set(-.4);
                elevator2.set(-.4);
            } else {
                elevator1.set(0.0);
                elevator2.set(0.0);
            }
        } else if (operatorJS.getRawButton(3)) {
            // go to middle
            if (pot.getValue() < 530) { // too high
                elevator1.set(-.4);
                elevator2.set(-.4);
            } else if (pot.getValue() > 540) {
                elevator1.set(.4);
                elevator2.set(.4);
            } else {
                elevator1.set(0.0);
                elevator2.set(0.0);
            }
        } else if (operatorJS.getRawButton(5)) {
            // go to top
            if (pot.getValue() < 210) { // too high
                elevator1.set(-.4);
                elevator2.set(-.4);
            } else if (pot.getValue() > 220) {
                elevator1.set(.4);
                elevator2.set(.4);
            } else {
                elevator1.set(0.0);
                elevator2.set(0.0);
            }
        } else if (operatorJS.getY() > 0.1) { //Go Up
            if (pot.getValue() > 32 || operatorJS.getRawButton(8)) {
                elevator1.set(operatorJS.getY());
                elevator2.set(operatorJS.getY());
            } else {
                elevator1.set(0.0);
                elevator2.set(0.0);
            }
        } else if (operatorJS.getY() < -0.1) { //Go Down
            if (pot.getValue() < 766 || operatorJS.getRawButton(8)) {
                elevator1.set(operatorJS.getY());
                elevator2.set(operatorJS.getY());
            } else {
                elevator1.set(0.0);
                elevator2.set(0.0);
            }
        } else { //Do Nothing
            elevator1.set(0.0);
            elevator2.set(0.0);
        }

        //Rollers on the claw
        if (operatorJS.getTrigger()) { //Suck
            if (!obtained.get()) {
                rollerstop.set(-0.8);
                rollersbottom.set(-0.8);
            } else if (!clawup.get()) {
                wrist.set(.5);
                rollerstop.set(0.0);
                rollersbottom.set(0.0);
            }
        } else if (operatorJS.getRawButton(2)) { //Unsuck
            rollerstop.set(0.6);
            rollersbottom.set(0.6);
        } else if (operatorJS.getRawButton(6)) { //Rotate Up
            rollerstop.set(0.3);
            rollersbottom.set(-0.3);
        } else if (operatorJS.getRawButton(7)) { //Rotate Down
            rollerstop.set(-0.3);
            rollersbottom.set(0.3);
        } else if (operatorJS.getRawButton(9)) {
            rollerstop.set(1.0);
            rollersbottom.set(1.0);
        } else { //Do Nothing
            rollerstop.set(0.0);
            rollersbottom.set(0.0);
        }

        //Wrist
        if (operatorJS.getRawButton(11) && (!clawup.get() || operatorJS.getRawButton(8))) { //Rotate Up
            wrist.set(.5);
        } else if (operatorJS.getRawButton(10) && (!clawdown.get() || operatorJS.getRawButton(8))) { //Rotate Down
            wrist.set(-.5);
        } else if (!operatorJS.getTrigger()) { //Do Nothing
            wrist.set(0.0);
        }

        //Minibot Deployment
        if (driveJS.getTrigger()) {
            minibot.set(-.45);
        } else if (driveJS.getRawButton(2)) {
            minibot.set(.3);
        } else {
            minibot.set(0.0);
        }
    }
}
