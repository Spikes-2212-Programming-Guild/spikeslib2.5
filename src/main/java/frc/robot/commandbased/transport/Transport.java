package frc.robot.commandbased.transport;

import com.ctre.phoenix6.CANBus;
import itay.commandbased.motored.MotoredSubsystem;
import itay.commandbased.smart.SmartMotoredSubsystem;
import itay.util.smartmotorcontrollers.SmartTalonFX;
import itay.util.smartmotorcontrollers.TalonFXWrapper;

public class Transport extends SmartMotoredSubsystem {

    private static Transport instance;

    private final SmartTalonFX talonFX;

    public static Transport getInstance() {
        if (instance == null) {
            instance = new Transport("transport", new SmartTalonFX(20, new CANBus()));
        }
        return instance;
    }

    private Transport(String namespaceName, SmartTalonFX talonFX) {
        super(namespaceName, talonFX);
        this.talonFX = talonFX;
    }

    @Override
    public void configureDashboard() {
        namespace.publishDouble("velocity", talonFX::getVelocity);
    }
}
