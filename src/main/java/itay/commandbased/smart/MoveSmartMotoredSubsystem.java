package itay.commandbased.smart;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.util.UnifiedControlMode;

import java.util.function.DoubleSupplier;

public abstract class MoveSmartMotoredSubsystem extends Command {

    private final SmartMotoredSubsystem subsystem;
    private final PIDSettings pidSettings;
    private final FFSettings ffSettings;

    private final DoubleSupplier setpoint;
    private final UnifiedControlMode controlMode;
    private final boolean stopAtSetpoint;

    private double lastTimeNotOnTarget;

    public MoveSmartMotoredSubsystem(SmartMotoredSubsystem subsystem, UnifiedControlMode controlMode,
                                     DoubleSupplier setpoint, PIDSettings pidSettings, FFSettings ffSettings,
                                     boolean stopAtSetpoint) {
        addRequirements(subsystem);
        this.subsystem = subsystem;
        this.controlMode = controlMode;
        this.setpoint = setpoint;
        this.pidSettings = pidSettings;
        this.ffSettings = ffSettings;
        this.stopAtSetpoint = stopAtSetpoint;
    }

    @Override
    public void initialize() {
        subsystem.configureClosedLoop(pidSettings, ffSettings);
    }

    @Override
    public void execute() {
        subsystem.setSetpoint(controlMode, setpoint.getAsDouble());
    }

    public boolean atSetpoint() {
        return subsystem.atSetpoint(controlMode, setpoint.getAsDouble(), pidSettings.getTolerance());
    }

    @Override
    public boolean isFinished() {
        if (!stopAtSetpoint) return false;
        if (!atSetpoint()) {
            lastTimeNotOnTarget = Timer.getFPGATimestamp();
        }
        return Timer.getFPGATimestamp() - lastTimeNotOnTarget >= pidSettings.getWaitTime();
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
    }
}
