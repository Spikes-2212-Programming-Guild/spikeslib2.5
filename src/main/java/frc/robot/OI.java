package frc.robot;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.commandbased.state.ShootingStateMachine;
import itay.util.joystickwrappers.XboxControllerWrapper;

public class OI {

    private final XboxControllerWrapper controller;

    public OI() {
        controller = new XboxControllerWrapper(0).withDeadband(0.08);
    }

    private void configureController() {
        ShootingStateMachine stateMachine = ShootingStateMachine.getInstance();
        controller.getBButton().onTrue(new InstantCommand(() -> stateMachine.requestShooting(() -> 5)));
        controller.getBButton().onFalse(new InstantCommand(stateMachine::cancelShooting));

        controller.getXButton().onTrue(new InstantCommand(stateMachine::requestFeeding));
        controller.getXButton().onFalse(new InstantCommand(stateMachine::cancelFeeding));
    }

    public double getLeftX() {
        return controller.getLeftX();
    }

    public double getLeftY() {
        return controller.getLeftY();
    }

    public double getRightX() {
        return controller.getRightX();
    }
}
