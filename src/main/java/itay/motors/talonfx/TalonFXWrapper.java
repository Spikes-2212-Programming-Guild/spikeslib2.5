
package itay.motors.talonfx;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;
import itay.motors.smart.MotorNeutralMode;
import itay.motors.smart.SmartMotorController;
import itay.motors.smart.SmartMotorControllerConfig;

public class TalonFXWrapper extends SmartMotorController {

    private final TalonFX motor;

    private SmartMotorControllerConfig smartConfig;
    private TalonFXConfig talonFXConfig;

    private final DutyCycleOut dutyCycleRequest = new DutyCycleOut(0);
    private final VoltageOut voltageRequest = new VoltageOut(0);
    private final PositionVoltage positionRequest = new PositionVoltage(0);
    private final VelocityVoltage velocityRequest = new VelocityVoltage(0);
    private final NeutralOut neutralRequest = new NeutralOut();

    public TalonFXWrapper(int deviceId, CANBus canBus,
                          SmartMotorControllerConfig smartConfig, TalonFXConfig talonFXConfig) {
        super(smartConfig);
        this.smartConfig = smartConfig;
        this.talonFXConfig = talonFXConfig;
        motor = new TalonFX(deviceId, canBus);
        applyConfig();
    }

    public TalonFXWrapper(int deviceId, CANBus canBus) {
        this(deviceId, canBus, SmartMotorControllerConfig.builder().build(), TalonFXConfig.builder().build());
    }

    public void setConfig(SmartMotorControllerConfig smartConfig, TalonFXConfig talonFXConfig) {
        this.smartConfig = smartConfig;
        this.talonFXConfig = talonFXConfig;
        applyConfig();
    }

    public void setSmartConfig(SmartMotorControllerConfig smartConfig) {
        this.smartConfig = smartConfig;
        applyConfig();
    }

    public void setTalonFXConfig(TalonFXConfig talonConfig) {
        this.talonFXConfig = talonConfig;
        applyConfig();
    }

    //    public void updateSmartConfig(Consumer<SmartMotorControllerConfig.Builder> updater) {
//        var builder = smartConfig.toBuilder();
//        updater.accept(builder);
//        smartConfig = builder.build();
//        applyConfig();
//    }
//
//    public void updateTalonFXConfig(Consumer<TalonFXConfig.Builder> updater) {
//        var builder = talonFXConfig.toBuilder();
//        updater.accept(builder);
//        talonFXConfig = builder.build();
//        applyConfig();
//    }

    public TalonFXConfig getTalonFXConfig() {
        return talonFXConfig;
    }

    /**
     * this is ready-only!
     */
    public TalonFXConfiguration getInnerTalonFXConfig() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        motor.getConfigurator().refresh(config);
        return config.clone();
    }

    @Override
    protected void applyConfig() {
        var c = new TalonFXConfiguration();
        c.MotorOutput.Inverted = smartConfig.inverted()
                ? InvertedValue.Clockwise_Positive
                : InvertedValue.CounterClockwise_Positive;

        c.MotorOutput.NeutralMode = smartConfig.neutralMode().equals(MotorNeutralMode.BRAKE)
                ? NeutralModeValue.Brake
                : NeutralModeValue.Coast;

        c.CurrentLimits.SupplyCurrentLimit = talonFXConfig.supplyCurrentLimit();
        c.CurrentLimits.SupplyCurrentLimitEnable = talonFXConfig.supplyCurrentLimitEnabled();
        c.CurrentLimits.StatorCurrentLimit = talonFXConfig.statorCurrentLimit();
        c.CurrentLimits.StatorCurrentLimitEnable = talonFXConfig.statorCurrentLimitEnabled();
        c.Feedback.RotorToSensorRatio = talonFXConfig.rotorToSensorRatio();
        c.Feedback.SensorToMechanismRatio = talonFXConfig.sensorToMechanismRatio();
        c.ClosedLoopGeneral.ContinuousWrap = talonFXConfig.continuousWrap();

        talonFXConfig.customConfig().accept(c);
        motor.getConfigurator().apply(c);
    }

    /**
     * Note - the talonFX will stop following if you set a different control request,
     *        like setDutyCycle
     *        (it can be blocked by checking adding a boolean, and checking if it's true
     *        when attempting to set a different control request)
     */
    public void follow(TalonFXWrapper master, boolean inverted) {
        motor.setControl(new Follower(master.getDeviceId(),
                inverted ? MotorAlignmentValue.Opposed : MotorAlignmentValue.Aligned)
        );
    }

    public void unfollow() {
        motor.setControl(neutralRequest);
    }

    public int getDeviceId() {
        return motor.getDeviceID();
    }

    @Override
    public void stopMotor() {
        motor.setControl(neutralRequest);
    }

    @Override
    public void disable() {
        motor.setControl(neutralRequest);
    }

    @Override
    public void setVoltage(Voltage voltage) {
        motor.setControl(voltageRequest.withOutput(voltage.in(Units.Volts)));
    }

    @Override
    public void setDutyCycle(double output) {
        motor.setControl(dutyCycleRequest.withOutput(output));
    }

    @Override
    public void setPosition(double position) {
        motor.setControl(positionRequest.withPosition(position));
    }

    @Override
    public void setVelocity(AngularVelocity velocity) {
        motor.setControl(velocityRequest.withVelocity(velocity.in(Units.RotationsPerSecond)));
    }

    @Override
    public double getPosition() {
        return motor.getPosition().getValueAsDouble();
    }

    @Override
    public AngularVelocity getVelocity() {
        return Units.RotationsPerSecond.of(motor.getVelocity().getValueAsDouble());
    }

    @Override
    public double getSupplyCurrent() {
        return motor.getSupplyCurrent().getValueAsDouble();
    }

    @Override
    public double getVoltage() {
        return motor.getMotorVoltage().getValueAsDouble();
    }

    @Override
    public double getDutyCycle() {
        return motor.getDutyCycle().getValueAsDouble();
    }

    @Override
    public boolean isConnected() {
        return motor.isConnected();
    }

    @Override
    public void resetToFactoryDefaults() {
        smartConfig = SmartMotorControllerConfig.builder().build();
        talonFXConfig = TalonFXConfig.builder().build();
        applyConfig();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("TalonFX");
        builder.addDoubleProperty("Position", this::getPosition, null);
        builder.addDoubleProperty("Velocity RPM", () -> getVelocity().in(Units.RPM), null);
        builder.addDoubleProperty("Duty Cycle", this::getDutyCycle, null);
        builder.addDoubleProperty("Voltage", this::getVoltage, null);
        builder.addDoubleProperty("Supply Current", this::getSupplyCurrent, null);
        builder.addBooleanProperty("Connected", this::isConnected, null);
    }

    @Override
    public void close() {
        motor.close();
    }
}
