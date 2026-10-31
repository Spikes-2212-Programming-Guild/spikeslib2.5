
package frc.robot.swerve;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.CANcoder;
import frc.robot.RobotMap;
import frc.robot.swerve.module.RebuiltModuleDrive;
import frc.robot.swerve.module.RebuiltModuleTurn;
import itay.commandbased.swerve.module.SwerveModule;
import itay.util.smartmotorcontrollers.SmartTalonFX;

public class SwerveModuleHolder {

    private final static CANBus CANIVORE = new CANBus("canivore");
    private final static CANBus RIO = new CANBus("rio");

    private static final boolean FRONT_LEFT_DRIVE_INVERTED = true;
    private static final boolean FRONT_RIGHT_DRIVE_INVERTED = false;
    private static final boolean BACK_LEFT_DRIVE_INVERTED = false;
    private static final boolean BACK_RIGHT_DRIVE_INVERTED = true;

    private static final boolean FRONT_LEFT_TURN_INVERTED = false;
    private static final boolean FRONT_RIGHT_TURN_INVERTED = false;
    private static final boolean BACK_LEFT_TURN_INVERTED = false;
    private static final boolean BACK_RIGHT_TURN_INVERTED = false;

    private static final String FRONT_LEFT_NAMESPACE_NAME = "front left";
    private static final String FRONT_RIGHT_NAMESPACE_NAME = "front right";
    private static final String BACK_LEFT_NAMESPACE_NAME = "back left";
    private static final String BACK_RIGHT_NAMESPACE_NAME = "back right";

    private static final double FRONT_LEFT_CANCODER_OFFSET_DEGREES = 62.05078126;
    private static final double FRONT_RIGHT_CANCODER_OFFSET_DEGREES = 27.421875;
    private static final double BACK_LEFT_CANCODER_OFFSET_DEGREES = 159.08203125;
    private static final double BACK_RIGHT_CANCODER_OFFSET_DEGREES =  162.86132812;

    private static SwerveModule frontLeft;
    private static SwerveModule frontRight;
    private static SwerveModule backLeft;
    private static SwerveModule backRight;

    public static SwerveModule getFrontLeft() {
        if (frontLeft == null) {
            frontLeft = new SwerveModule(FRONT_LEFT_NAMESPACE_NAME,
                    new RebuiltModuleDrive(FRONT_LEFT_NAMESPACE_NAME + " drive",
                            new SmartTalonFX(RobotMap.SWERVE.FRONT_LEFT_DRIVE_TALON_FX, CANIVORE),
                            FRONT_LEFT_DRIVE_INVERTED),
                    new RebuiltModuleTurn(FRONT_LEFT_NAMESPACE_NAME + " turn",
                            new CANcoder(RobotMap.SWERVE.FRONT_LEFT_CANCODER, CANIVORE),
                            new SmartTalonFX(RobotMap.SWERVE.FRONT_LEFT_TURN_SPARK_MAX, RIO),
                            FRONT_LEFT_TURN_INVERTED, FRONT_LEFT_CANCODER_OFFSET_DEGREES));
        }
        return frontLeft;
    }

    public static SwerveModule getFrontRight() {
        if (frontRight == null) {
            frontRight = new SwerveModule(FRONT_RIGHT_NAMESPACE_NAME,
                    new RebuiltModuleDrive(FRONT_RIGHT_NAMESPACE_NAME + " drive",
                            new SmartTalonFX(RobotMap.SWERVE.FRONT_RIGHT_DRIVE_TALON_FX, CANIVORE),
                            FRONT_RIGHT_DRIVE_INVERTED),
                    new RebuiltModuleTurn(FRONT_RIGHT_NAMESPACE_NAME + " turn",
                            new CANcoder(RobotMap.SWERVE.FRONT_RIGHT_CANCODER, CANIVORE),
                            new SmartTalonFX(RobotMap.SWERVE.FRONT_RIGHT_TURN_SPARK_MAX, RIO),
                            FRONT_RIGHT_TURN_INVERTED, FRONT_RIGHT_CANCODER_OFFSET_DEGREES));
        }
        return frontRight;
    }

    public static SwerveModule getBackLeft() {
        if (backLeft == null) {
            backLeft = new SwerveModule(BACK_LEFT_NAMESPACE_NAME,
                    new RebuiltModuleDrive(BACK_LEFT_NAMESPACE_NAME + " drive",
                            new SmartTalonFX(RobotMap.SWERVE.BACK_LEFT_DRIVE_TALON_FX, CANIVORE),
                            BACK_LEFT_DRIVE_INVERTED),
                    new RebuiltModuleTurn(BACK_LEFT_NAMESPACE_NAME + " turn",
                            new CANcoder(RobotMap.SWERVE.BACK_LEFT_CANCODER, CANIVORE),
                            new SmartTalonFX(RobotMap.SWERVE.BACK_LEFT_TURN_SPARK_MAX, RIO),
                            BACK_LEFT_TURN_INVERTED, BACK_LEFT_CANCODER_OFFSET_DEGREES));
        }
        return backLeft;
    }

    public static SwerveModule getBackRight() {
        if (backRight == null) {
            backRight = new SwerveModule(BACK_RIGHT_NAMESPACE_NAME,
                    new RebuiltModuleDrive(BACK_RIGHT_NAMESPACE_NAME + " drive",
                            new SmartTalonFX(RobotMap.SWERVE.BACK_RIGHT_DRIVE_TALON_FX, CANIVORE),
                            BACK_RIGHT_DRIVE_INVERTED),
                    new RebuiltModuleTurn(BACK_RIGHT_NAMESPACE_NAME + " turn",
                            new CANcoder(RobotMap.SWERVE.BACK_RIGHT_CANCODER, CANIVORE),
                            new SmartTalonFX(RobotMap.SWERVE.BACK_RIGHT_TURN_SPARK_MAX, RIO),
                            BACK_RIGHT_TURN_INVERTED, BACK_RIGHT_CANCODER_OFFSET_DEGREES));
        }
        return backRight;
    }
}
