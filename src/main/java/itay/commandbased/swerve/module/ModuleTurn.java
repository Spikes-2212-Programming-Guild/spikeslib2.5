package itay.commandbased.swerve.module;

import com.ctre.phoenix6.hardware.CANcoder;
import edu.wpi.first.math.geometry.Rotation2d;
import itay.commandbased.smart.SmartMotoredSubsystem;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.util.UnifiedControlMode;
import itay.util.smartmotorcontrollers.SmartTalonFX;

public abstract class ModuleTurn extends SmartMotoredSubsystem {

    protected final PIDSettings pidSettings;
    protected final FFSettings ffSettings;
    protected final CANcoder cancoder;
    protected final SmartTalonFX talonFX;

    public ModuleTurn(String namespaceName, CANcoder cancoder, SmartTalonFX talonFX,
                      PIDSettings pidSettings, FFSettings ffSettings) {
        super(namespaceName, talonFX);
        this.cancoder = cancoder;
        this.talonFX = talonFX;
        this.pidSettings = pidSettings;
        this.ffSettings = ffSettings;
    }

    public abstract void configureMotor();

    public abstract void configureCancoder();

    public Rotation2d getAbsoluteAngle() {
        return Rotation2d.fromDegrees(cancoder.getAbsolutePosition().getValueAsDouble());
    }

    public void resetRelativeAngle() {
        setPosition(getAbsoluteAngle().getDegrees());
    }

    public void applyClosedLoopSettings() {
        configureClosedLoop(pidSettings, ffSettings);
    }

    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(getPosition());
    }

    public void setTargetAngle(Rotation2d angle) {
        setSetpoint(UnifiedControlMode.POSITION, angle.getDegrees());
    }
}
