package frc.robot.swerve.module;

import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Commands;
import itay.commandbased.swerve.module.ModuleDrive;
import itay.control.FFController;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.dashboard.RootNamespace;
import itay.util.smartmotorcontrollers.SmartTalonFX;

import java.util.function.DoubleSupplier;

public class RebuiltModuleDrive extends ModuleDrive {

    private static final RootNamespace driveTuner = new RootNamespace("drive tuner");

    private static final PIDSettings pidSettings = driveTuner.addPIDSettings(
            new PIDSettings(0, 0, 0, 0, 0, 0), true
    );

    private static final FFSettings ffSettings = driveTuner.addFFSettings(
            new FFSettings(0, 0, FFController.ControlMode.LINEAR_VELOCITY), true
    );

    private static final double GEAR_RATIO = 1.0 / 6.12;
    private static final double WHEEL_DIAMETER_METERS = Units.inchesToMeters(4);
    private static final double MOTOR_ROTATIONS_TO_WHEEL_ROTATIONS =
            GEAR_RATIO * WHEEL_DIAMETER_METERS * Math.PI;

    private final boolean motorInverted;

    public RebuiltModuleDrive(String namespaceName, SmartTalonFX talonFX, boolean motorInverted) {
        super(namespaceName, talonFX, pidSettings, ffSettings);
        this.motorInverted = motorInverted;
        configureMotor();
        applyClosedLoopSettings();
        configureDashboard();
    }

    @Override
    public void configureMotor() {
        talonFX.setIdleMode(NeutralModeValue.Brake);
        talonFX.setInverted(motorInverted);
        talonFX.setEncoderConversionFactor(MOTOR_ROTATIONS_TO_WHEEL_ROTATIONS);
    }

    @Override
    public void configureDashboard() {
        namespace.publishDouble("velocity", this::getVelocity);

        DoubleSupplier targetVelocity = namespace.addTunableDouble("target velocity", 0);
        namespace.publishCommand("set target velocity", Commands.runEnd(
                () -> setTargetVelocity(targetVelocity.getAsDouble()), this::stop, this
        ));

        namespace.publishCommand("move at 0.1", Commands.runEnd(
                () -> move(0.1), this::stop, this
        ));
    }

    public static void updateDriveTuner() {
        driveTuner.update();
    }
}
