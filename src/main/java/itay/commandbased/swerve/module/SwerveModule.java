package itay.commandbased.swerve.module;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveModule extends SubsystemBase {

    private final ModuleDrive moduleDrive;
    private final ModuleTurn moduleTurn;

    public SwerveModule(String namespaceName, ModuleDrive moduleDrive, ModuleTurn moduleTurn) {
        super(namespaceName);
        this.moduleDrive = moduleDrive;
        this.moduleTurn = moduleTurn;
    }

    public void setTargetState(SwerveModuleState state) {
        state.optimize(moduleTurn.getAngle());
        state.cosineScale(getAngle());
        setTargetAngle(state.angle);
        setTargetVelocity(state.speedMetersPerSecond);
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(getVelocity(), getAngle());
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(getRelativePosition(), getAngle());
    }

    public void setTargetVelocity(double velocity) {
        moduleDrive.setTargetVelocity(velocity);
    }

    public void setTargetAngle(Rotation2d angle) {
        moduleTurn.setTargetAngle(angle);
    }

    public Rotation2d getAngle() {
        return moduleTurn.getAngle();
    }

    public double getVelocity() {
        return moduleDrive.getVelocity();
    }

    public double getRelativePosition() {
        return moduleDrive.getPosition();
    }

    public double getRelativeAngle() {
        return moduleTurn.getPosition();
    }

    public void resetRelativeAngle() {
        moduleTurn.resetRelativeAngle();
    }

    public void stop() {
        moduleDrive.stop();
        moduleTurn.stop();
    }
}
