package itay.commandbased.swerve.module;

import itay.commandbased.smart.SmartMotoredSubsystem;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.util.UnifiedControlMode;
import itay.util.smartmotorcontrollers.SmartTalonFX;

public abstract class ModuleDrive extends SmartMotoredSubsystem {

    private final PIDSettings pidSettings;
    private final FFSettings ffSettings;
    protected final SmartTalonFX talonFX;

    public ModuleDrive(String namespaceName, SmartTalonFX talonFX, PIDSettings pidSettings, FFSettings ffSettings) {
        super(namespaceName, talonFX);
        this.talonFX = talonFX;
        this.pidSettings = pidSettings;
        this.ffSettings = ffSettings;
    }

    public abstract void configureMotor();

    public void applyClosedLoopSettings() {
        configureClosedLoop(pidSettings, ffSettings);
    }

    public void setTargetVelocity(double velocityMps) {
        setSetpoint(UnifiedControlMode.VELOCITY, velocityMps);
    }
}
