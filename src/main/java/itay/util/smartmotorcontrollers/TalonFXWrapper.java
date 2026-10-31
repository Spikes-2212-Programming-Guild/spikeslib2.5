package itay.util.smartmotorcontrollers;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

/*
TODO - make it so you have to give the motor its settings
 */
public class TalonFXWrapper implements Sendable, AutoCloseable, MotorController {

    protected final TalonFX talonFX;
    protected final MotorOutputConfigs motorOutputConfigs;

    public TalonFXWrapper(int deviceId, CANBus canbus) {
        talonFX = new TalonFX(deviceId, canbus);
        motorOutputConfigs = new MotorOutputConfigs();
        restoreFactoryDefaults();
    }

    public void restoreFactoryDefaults() {
        talonFX.getConfigurator().apply(new TalonFXConfiguration());
        motorOutputConfigs.withInverted(InvertedValue.CounterClockwise_Positive);
        setIdleMode(NeutralModeValue.Coast);
    }

    public void setIdleMode(NeutralModeValue neutralModeValue) {
        talonFX.getConfigurator().apply(motorOutputConfigs.withNeutralMode(neutralModeValue));
    }

    public TalonFXConfigurator getConfigurator() {
        return talonFX.getConfigurator();
    }

    public void setPosition(double position) {
        talonFX.setPosition(position);
    }

    public void follow(TalonFX master, boolean invert) {
        MotorAlignmentValue alignment = invert ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned;
        talonFX.setControl(new Follower(master.getDeviceID(), alignment));
    }

    public void follow(TalonFX master) {
        follow(master, false);
    }

    public void set(double speed) {
        talonFX.set(speed);
    }

    public void setVoltage(double voltage) {
        talonFX.setVoltage(voltage);
    }

    @Override
    public double get() {
        return talonFX.get();
    }

    @Override
    public void setInverted(boolean inverted) {
        talonFX.getConfigurator().apply(motorOutputConfigs.withInverted(inverted ?
                InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive));
    }

    @Override
    public boolean getInverted() {
        return motorOutputConfigs.Inverted == InvertedValue.Clockwise_Positive;
    }

    @Override
    public void disable() {
        talonFX.disable();
    }

    @Override
    public void stopMotor() {
        talonFX.stopMotor();
    }

    public void setEncoderConversionFactor(double factor) {
        FeedbackConfigs configs = new FeedbackConfigs();
        configs.withSensorToMechanismRatio(1 / factor);
        talonFX.getConfigurator().apply(configs);
    }

    public double getPosition() {
        return talonFX.getPosition().getValueAsDouble();
    }

    public double getVelocity() {
        return talonFX.getVelocity().getValueAsDouble();
    }

    public double getAcceleration() {
        return talonFX.getAcceleration().getValueAsDouble();
    }

    public double getCurrent() {
        return talonFX.getTorqueCurrent().getValueAsDouble();
    }

    public double getVoltage() {
        return talonFX.getMotorVoltage().getValueAsDouble();
    }

    public void close() {
        SendableRegistry.remove(this);
    }

    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("talon fx wrapper");
        builder.publishConstInteger("id", talonFX.getDeviceID());
        builder.addDoubleProperty("velocity", this::getVelocity, null);
        builder.addDoubleProperty("current", this::getCurrent, null);
        builder.addDoubleProperty("voltage", this::getVoltage, null);
        builder.addDoubleProperty("acceleration", this::getAcceleration, null);
        builder.addDoubleProperty("position", this::getPosition, null);
        builder.addBooleanProperty("inverted", this::getInverted, null);
    }
}
