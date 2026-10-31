package frc.robot.swerve.module;

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Commands;
import itay.commandbased.swerve.module.ModuleTurn;
import itay.control.FFController;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.dashboard.RootNamespace;
import itay.util.smartmotorcontrollers.SmartTalonFX;

import java.util.function.DoubleSupplier;

public class RebuiltModuleTurn extends ModuleTurn {

    private static final RootNamespace turnTuner = new RootNamespace("turn tuner");

    private static final PIDSettings pidSettings = turnTuner.addPIDSettings(
            new PIDSettings(0, 0, 0, 0, 0, 0), true
    );

    private static final FFSettings ffSettings = turnTuner.addFFSettings(
            new FFSettings(0, 0, FFController.ControlMode.LINEAR_POSITION), true
    );

    private static final double GEAR_RATIO = 7.0 / 150.0;
    private static final int DEGREES_IN_ROTATION = 360;
    private static final double MOTOR_POSITION_IN_ROTATION = GEAR_RATIO * DEGREES_IN_ROTATION;

    private final boolean motorInverted;
    private final double cancoderOffsetDegrees;

    public RebuiltModuleTurn(String namespaceName, CANcoder cancoder, SmartTalonFX talonFX, boolean motorInverted,
                             double cancoderOffsetDegrees) {
        super(namespaceName, cancoder, talonFX, pidSettings, ffSettings);
        this.motorInverted = motorInverted;
        this.cancoderOffsetDegrees = cancoderOffsetDegrees;
        configureMotor();
        configureCancoder();
        applyClosedLoopSettings();
        resetRelativeAngle();
        configureDashboard();
    }

    @Override
    public void configureMotor() {
        talonFX.setIdleMode(NeutralModeValue.Brake);
        talonFX.setEncoderConversionFactor(MOTOR_POSITION_IN_ROTATION);
        talonFX.setInverted(motorInverted);
        ClosedLoopGeneralConfigs configs = new ClosedLoopGeneralConfigs();
        talonFX.getConfigurator().refresh(configs);
        configs.ContinuousWrap = true;
        talonFX.getConfigurator().apply(configs);
    }

    @Override
    public void configureCancoder() {
        cancoder.getConfigurator().apply(new MagnetSensorConfigs()
                .withMagnetOffset(Units.degreesToRadians(cancoderOffsetDegrees))
                .withSensorDirection(SensorDirectionValue.CounterClockwise_Positive)
        );
    }

    @Override
    public void configureDashboard() {
        namespace.publishDouble("angle", () -> getAngle().getDegrees());
        namespace.publishDouble("relative angle", this::getPosition);
        namespace.publishDouble("absolute angle", () -> getAbsoluteAngle().getDegrees());

        DoubleSupplier targetAngle = namespace.addTunableDouble("target angle", 0);
        namespace.publishCommand("set target angle", Commands.runEnd(
                () -> setTargetAngle(Rotation2d.fromDegrees(targetAngle.getAsDouble())),
                this::stop, this
        ));

        namespace.publishCommand("move at 0.1", Commands.runEnd(
                () -> move(0.1), this::stop, this
        ));
    }

    public static void updateTurnTuner() {
        turnTuner.update();
    }
}
