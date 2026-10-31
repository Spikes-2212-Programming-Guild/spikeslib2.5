package itay.motors.smart;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public abstract class SmartMotorController implements Sendable, AutoCloseable {

    protected SmartMotorControllerConfig config;

    protected SmartMotorController(SmartMotorControllerConfig config) {
        this.config = config;
    }

    public abstract void stopMotor();

    public abstract void disable();

    public abstract void setVoltage(Voltage voltage);

    public abstract void setDutyCycle(double output);

    public abstract void setPosition(double position);

    public abstract void setVelocity(AngularVelocity velocity);

    public abstract double getPosition();

    public abstract AngularVelocity getVelocity();

    public abstract double getSupplyCurrent();

    public abstract double getVoltage();

    public abstract double getDutyCycle();

    public abstract boolean isConnected();

    public boolean atPosition(double target, double tolerance) {
        return MathUtil.isNear(target, getPosition(), tolerance);
    }

    public boolean atVelocity(AngularVelocity target, AngularVelocity tolerance) {
        return target.isNear(getVelocity(), tolerance);
    }

    public abstract void resetToFactoryDefaults();

    protected abstract void applyConfig();

    public SmartMotorControllerConfig getSmartConfig() {
        return config;
    }

    @Override
    public abstract void initSendable(SendableBuilder builder);

    @Override
    public abstract void close();
}
