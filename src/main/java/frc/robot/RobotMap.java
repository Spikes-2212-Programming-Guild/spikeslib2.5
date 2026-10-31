package frc.robot;

public class RobotMap {

    public interface SWERVE {

        int FRONT_LEFT_DRIVE_TALON_FX = 1;
        int FRONT_RIGHT_DRIVE_TALON_FX = 4;
        int BACK_LEFT_DRIVE_TALON_FX = 7;
        int BACK_RIGHT_DRIVE_TALON_FX = 10;

        int FRONT_LEFT_TURN_SPARK_MAX = 41;
        int FRONT_RIGHT_TURN_SPARK_MAX = 5;
        int BACK_LEFT_TURN_SPARK_MAX = 8;
        int BACK_RIGHT_TURN_SPARK_MAX = 11;

        int FRONT_LEFT_CANCODER = 3;
        int FRONT_RIGHT_CANCODER = 6;
        int BACK_LEFT_CANCODER = 9;
        int BACK_RIGHT_CANCODER = 12;

        int GYRO_PIGEON_2 = 13;
    }

    public interface CAN {

    }

    public interface DIO {

    }

    public interface PWM {

    }

    public interface AIN {

    }

    public interface PCM {

    }
}
