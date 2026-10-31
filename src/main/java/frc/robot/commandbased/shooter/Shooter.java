package frc.robot.commandbased.shooter;

import com.ctre.phoenix6.CANBus;
import itay.commandbased.smart.SmartMotoredSubsystem;
import itay.util.smartmotorcontrollers.SmartTalonFX;


public class Shooter extends SmartMotoredSubsystem {

    private static Shooter instance;

    public static Shooter getInstance() {
        if (instance == null) {
            instance = new Shooter(
                    "shooter",
                    new SmartTalonFX(10, new CANBus())
            );
        }
        return instance;
    }

    private Shooter(String namespaceName, SmartTalonFX talonFX) {
        super(namespaceName, talonFX);
    }

    @Override
    public void configureDashboard() {

    }
}
