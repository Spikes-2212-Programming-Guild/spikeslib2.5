package frc.robot.swerve;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.RobotMap;
import itay.commandbased.swerve.SwerveDrivetrain;
import itay.commandbased.swerve.module.SwerveModule;

public class Drivetrain extends SwerveDrivetrain {

    private static final String NAMESPACE_NAME = "swerve drivetrain";
    private static final CANBus CANIVORE = new CANBus("canivore");

    private static final double MAX_POSSIBLE_VELOCITY = 5.1;
    private static final double TRACK_WIDTH = 0.55;
    private static final double TRACK_LENGTH = 0.55;

    private final Field2d field2d;
    private final SwerveDriveOdometry odometry;

    private static Drivetrain instance;

    public static Drivetrain getInstance() {
        if (instance == null) {
            instance = new Drivetrain(
                    NAMESPACE_NAME, new Pigeon2(RobotMap.SWERVE.GYRO_PIGEON_2, CANIVORE),
                    SwerveModuleHolder.getFrontLeft(), SwerveModuleHolder.getFrontRight(),
                    SwerveModuleHolder.getBackLeft(), SwerveModuleHolder.getBackRight(),
                    TRACK_WIDTH, TRACK_LENGTH, MAX_POSSIBLE_VELOCITY
            );
        }
        return instance;
    }

    private Drivetrain(String namespaceName, Pigeon2 pigeon2, SwerveModule frontLeftModule,
                       SwerveModule frontRightModule, SwerveModule backLeftModule, SwerveModule backRightModule,
                       double frameLength, double frameWidth, double maxPossibleVelocity) {
        super(namespaceName, pigeon2, frontLeftModule, frontRightModule, backLeftModule, backRightModule,
                frameLength, frameWidth, maxPossibleVelocity);
        field2d = new Field2d();
        odometry = new SwerveDriveOdometry(getKinematics(), getAngle(), getModulePositions(), new Pose2d());
        configureDashboard();
    }

    @Override
    public void periodic() {
        super.periodic();
        field2d.setRobotPose(odometry.update(getAngle(), getModulePositions()));
    }

    @Override
    public Pigeon2Configuration getPigeon2Configuration() {
        return new Pigeon2Configuration();
    }

    @Override
    public void configureDashboard() {
        namespace.publishDouble("gyro angle", () -> getAngle().getDegrees());
        namespace.publishSendable("swerve drive", this);
        namespace.publishSendable("field", field2d);
    }
}
