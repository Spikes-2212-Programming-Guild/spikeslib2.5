package frc.robot.commandbased.transport;

import itay.commandbased.smart.MoveSmartMotoredSubsystem;
import itay.control.FFController;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.dashboard.Namespace;
import itay.dashboard.RootNamespace;
import itay.util.UnifiedControlMode;

import java.util.function.DoubleSupplier;

public class SmartMoveTransport extends MoveSmartMotoredSubsystem {

    private static final Namespace namespace = new RootNamespace("smart transport");

    private static final PIDSettings pidSettings = namespace.addPIDSettings(
            new PIDSettings(0.0, 0.0, 0.0, 0.0, 0.0), false);

    private static final FFSettings ffSettings = namespace.addFFSettings(
            new FFSettings(0.0, 0.0, FFController.ControlMode.LINEAR_VELOCITY), false);

    public SmartMoveTransport(DoubleSupplier setpoint) {
        super(Transport.getInstance(), UnifiedControlMode.VELOCITY, setpoint,
                pidSettings, ffSettings, false);
    }

    public SmartMoveTransport(double setpoint) {
        this(() -> setpoint);
    }

    public static void updateNamespace() {
        namespace.update();
    }
}
