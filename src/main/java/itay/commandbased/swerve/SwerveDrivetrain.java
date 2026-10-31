package itay.commandbased.swerve;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import itay.commandbased.dashboarded.DashboardSubsystem;
import itay.commandbased.swerve.module.SwerveModule;

import java.util.function.Supplier;

public abstract class SwerveDrivetrain extends DashboardSubsystem implements AutoCloseable {

    protected final SwerveModule frontLeftModule;
    protected final SwerveModule frontRightModule;
    protected final SwerveModule backLeftModule;
    protected final SwerveModule backRightModule;

    protected final double frameLength;
    protected final double frameWidth;
    protected final double maxPossibleVelocity;

    protected final Pigeon2 pigeon2;
    protected final SwerveDriveKinematics kinematics;

    public SwerveDrivetrain(String namespaceName, Pigeon2 pigeon2,
                            SwerveModule frontLeftModule, SwerveModule frontRightModule,
                            SwerveModule backLeftModule, SwerveModule backRightModule,
                            double frameLength, double frameWidth, double maxPossibleVelocity) {
        super(namespaceName);
        this.pigeon2 = pigeon2;
        this.frontLeftModule = frontLeftModule;
        this.frontRightModule = frontRightModule;
        this.backLeftModule = backLeftModule;
        this.backRightModule = backRightModule;
        this.frameLength = frameLength;
        this.frameWidth = frameWidth;
        this.maxPossibleVelocity = maxPossibleVelocity;
        kinematics = calculateKinematics();
        pigeon2.getConfigurator().apply(getPigeon2Configuration());
    }

    public Command driveCommand(Supplier<ChassisSpeeds> chassisSpeeds, boolean fieldRelative) {
        return driveCommand(chassisSpeeds, maxPossibleVelocity, fieldRelative);
    }

    public Command driveCommand(Supplier<ChassisSpeeds> chassisSpeeds,
                                double speedMultiplier, boolean fieldRelative) {
        Timer timer = new Timer();
        return Commands.runEnd(
                () -> {
                    double dt = timer.get();
                    timer.restart();

                    drive(chassisSpeeds.get().times(speedMultiplier), fieldRelative, dt);
                },
                this::stop,
                this
        ).beforeStarting(timer::restart);
    }

    public void drive(ChassisSpeeds chassisSpeeds, boolean isFieldRelative, double deltaTime) {
        ChassisSpeeds discretizedSpeeds = ChassisSpeeds.discretize(chassisSpeeds, deltaTime);
        if (isFieldRelative) {
            discretizedSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(discretizedSpeeds, getAngle());
        }
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(discretizedSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxPossibleVelocity);
        setTargetModuleStates(states);
    }

    protected void setTargetModuleStates(SwerveModuleState[] moduleStates) {
        frontLeftModule.setTargetState(moduleStates[0]);
        frontRightModule.setTargetState(moduleStates[1]);
        backLeftModule.setTargetState(moduleStates[2]);
        backRightModule.setTargetState(moduleStates[3]);
    }

    public abstract Pigeon2Configuration getPigeon2Configuration();

    public void resetFieldRelativity() {
        pigeon2.reset();
    }

    private SwerveDriveKinematics calculateKinematics() {
        double halfWidth = frameWidth / 2;
        double halfLength = frameLength / 2;
        Translation2d frontLeftWheelDistanceFromCenter = new Translation2d(halfWidth, halfLength);
        Translation2d frontRightWheelDistanceFromCenter = new Translation2d(halfWidth, -halfLength);
        Translation2d backLeftWheelDistanceFromCenter = new Translation2d(-halfWidth, halfLength);
        Translation2d backRightWheelDistanceFromCenter = new Translation2d(-halfWidth, -halfLength);
        return new SwerveDriveKinematics(
                frontLeftWheelDistanceFromCenter, frontRightWheelDistanceFromCenter,
                backLeftWheelDistanceFromCenter, backRightWheelDistanceFromCenter
        );
    }

    public Rotation2d getAngle() {
        return pigeon2.getRotation2d();
    }

    public SwerveModulePosition[] getModulePositions() {
        return new SwerveModulePosition[]{
                frontLeftModule.getPosition(), frontRightModule.getPosition(),
                backLeftModule.getPosition(), backRightModule.getPosition()
        };
    }

    public ChassisSpeeds getChassisSpeeds() {
        return kinematics.toChassisSpeeds(
                frontLeftModule.getState(), frontRightModule.getState(),
                backLeftModule.getState(), backRightModule.getState()
        );
    }

    public SwerveModule getFrontLeftModule() {
        return frontLeftModule;
    }

    public SwerveModule getFrontRightModule() {
        return frontRightModule;
    }

    public SwerveModule getBackLeftModule() {
        return backLeftModule;
    }

    public SwerveModule getBackRightModule() {
        return backRightModule;
    }

    public SwerveDriveKinematics getKinematics() {
        return kinematics;
    }

    public void resetRelativeEncoders() {
        frontLeftModule.resetRelativeAngle();
        frontRightModule.resetRelativeAngle();
        backLeftModule.resetRelativeAngle();
        backRightModule.resetRelativeAngle();
    }

    public void stop() {
        frontLeftModule.stop();
        frontRightModule.stop();
        backLeftModule.stop();
        backRightModule.stop();
    }

    @Override
    public void close() {
        SendableRegistry.remove(this);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SwerveDrive");
        addModuleToSendable(builder, frontLeftModule, "Front Left");
        addModuleToSendable(builder, frontRightModule, "Front Right");
        addModuleToSendable(builder, backLeftModule, "Back Left");
        addModuleToSendable(builder, backRightModule, "Back Right");
        builder.addDoubleProperty("Robot Angle", () -> getAngle().getRadians(), null);
    }

    private void addModuleToSendable(SendableBuilder builder, SwerveModule module, String name) {
        builder.addDoubleProperty(name + " Angle", () -> module.getState().angle.getRadians(), null);
        builder.addDoubleProperty(name + " Velocity", () -> module.getState().speedMetersPerSecond, null);
    }
}
