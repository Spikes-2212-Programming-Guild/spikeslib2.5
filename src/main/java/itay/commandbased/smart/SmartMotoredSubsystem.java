package itay.commandbased.smart;

import itay.commandbased.motored.MotoredSubsystem;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.dashboard.Namespace;
import itay.util.UnifiedControlMode;
import itay.util.smartmotorcontrollers.SmartTalonFX;

import java.util.List;

/*
TODO:
 is SmartMotoredSubsystem trying to get to a certain state, or is only
 moveSmartSubsystem trying to move a subsystem to a certain state?
 */
public abstract class SmartMotoredSubsystem extends MotoredSubsystem {

    private PIDSettings pidSettings = PIDSettings.EMPTY_PID_SETTINGS();
    private FFSettings ffSettings = FFSettings.EMPTY_FF_SETTINGS();

    private final List<SmartTalonFX> smartMotorControllers;

    public SmartMotoredSubsystem(Namespace namespace, SmartTalonFX... smartMotorController) {
        super(namespace, smartMotorController);
        smartMotorControllers = List.of(smartMotorController);
    }

    public SmartMotoredSubsystem(String namespaceName, SmartTalonFX... smartMotorController) {
        super(namespaceName, smartMotorController);
        smartMotorControllers = List.of(smartMotorController);
    }

    public void configurePID(PIDSettings pidSettings) {
        smartMotorControllers.forEach(s -> s.configurePID(pidSettings, true));
        this.pidSettings = pidSettings;
    }

    public void configureFF(FFSettings ffSettings) {
        smartMotorControllers.forEach(s -> s.configureFF(ffSettings, true));
        this.ffSettings = ffSettings;
    }

    public void configureClosedLoop(PIDSettings pidSettings, FFSettings ffSettings) {
        smartMotorControllers.forEach(s -> s.configureLoop(pidSettings, ffSettings));
        this.pidSettings = pidSettings;
        this.ffSettings = ffSettings;
    }

    @Override
    public void periodic() {
        super.periodic();
        if (!pidSettings.isConstant() || !ffSettings.isConstant()) {
            configureClosedLoop(pidSettings, ffSettings);
        }
    }

    public void setPosition(double position) {
        smartMotorControllers.forEach(s -> s.setPosition(position));
    }

    public double getPosition() {
        return smartMotorControllers.get(0).getPosition();
    }

    public double getVelocity() {
        return smartMotorControllers.get(0).getVelocity();
    }

    public void setSetpoint(UnifiedControlMode controlMode, double setpoint) {
        smartMotorControllers.forEach(s -> s.setSetpoint(controlMode, setpoint));
    }

    public boolean atSetpoint(UnifiedControlMode controlMode, double setpoint, double tolerance) {
        return smartMotorControllers.stream().allMatch(s -> s.atSetpoint(controlMode, tolerance, setpoint));
    }
}
