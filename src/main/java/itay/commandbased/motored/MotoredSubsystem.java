package itay.commandbased.motored;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import itay.commandbased.dashboarded.DashboardSubsystem;
import itay.dashboard.Namespace;
import itay.dashboard.RootNamespace;

import java.util.List;

// TODO - maybe add "canMove()"
public abstract class MotoredSubsystem extends DashboardSubsystem {

    protected final List<MotorController> motorControllers;

    public MotoredSubsystem(Namespace namespace, MotorController... motorControllers) {
        super(namespace);
        this.motorControllers = List.of(motorControllers);
    }

    public MotoredSubsystem(String namespaceName, MotorController... motorControllers) {
        super(namespaceName);
        this.motorControllers = List.of(motorControllers);
    }

    public void move(double speed) {
        motorControllers.forEach(m -> m.set(speed));
    }

    public void stop() {
        motorControllers.forEach(MotorController::stopMotor);
    }
}
