package frc.robot.commandbased.transport;

import itay.commandbased.motored.MoveMotoredSubsystem;

public class MoveTransport extends MoveMotoredSubsystem {

    public MoveTransport(double speed) {
        super(Transport.getInstance(), speed);
    }
}
