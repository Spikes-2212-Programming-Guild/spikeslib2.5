// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.swerve.Drivetrain;
import frc.robot.swerve.module.RebuiltModuleDrive;
import frc.robot.swerve.module.RebuiltModuleTurn;
import itay.motors.sparkmax.SparkConfig;
import itay.motors.sparkmax.SparkMaxWrapper;

public class Robot extends TimedRobot {

    private Drivetrain drivetrain;
    private SparkMaxWrapper motor;
    private OI oi;

    @Override
    public void robotInit() {
//        drivetrain = Drivetrain.getInstance();

        oi = new OI();

        motor = new SparkMaxWrapper(1);
        motor.setSparkConfig(SparkConfig.builder()
                .smartCurrentLimit(12)
                .build());
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
        RebuiltModuleDrive.updateDriveTuner();
        RebuiltModuleTurn.updateTurnTuner();
    }

    @Override
    public void disabledInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void autonomousInit() {

    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
//        drivetrain.setDefaultCommand(drivetrain.driveCommand(
//                () -> new ChassisSpeeds(oi.getLeftX(), oi.getLeftY(), oi.getRightX()), true
//        ));
    }

    @Override
    public void teleopPeriodic() {
        motor.setDutyCycle(oi.getLeftX());
    }

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {

    }

    @Override
    public void simulationInit() {

    }

    @Override
    public void simulationPeriodic() {

    }
}
