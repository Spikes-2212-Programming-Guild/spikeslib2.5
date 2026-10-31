package itay.commandbased.motored;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.DoubleSupplier;

// TODO - maybe add "canMove()" to isFinished
public class MoveMotoredSubsystem extends Command {

    protected final MotoredSubsystem subsystem;
    protected final DoubleSupplier speed;

    public MoveMotoredSubsystem(MotoredSubsystem subsystem, DoubleSupplier speed) {
        addRequirements(subsystem);
        this.subsystem = subsystem;
        this.speed = speed;
    }

    public MoveMotoredSubsystem(MotoredSubsystem subsystem, double speed) {
        this(subsystem, () -> speed);
    }

    @Override
    public void execute() {
        subsystem.move(speed.getAsDouble());
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }
}
