package itay.motors.sparkmax;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkMaxConfigAccessor;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;
import itay.motors.smart.MotorNeutralMode;
import itay.motors.smart.SmartMotorController;
import itay.motors.smart.SmartMotorControllerConfig;

public class SparkMaxWrapper extends SmartMotorController {

    private final SparkMax motor;
    private final SparkClosedLoopController closedLoopController;
    private final RelativeEncoder encoder;
    private SparkMaxWrapper master;
    private boolean followInverted;

    private SmartMotorControllerConfig smartConfig;
    private SparkConfig sparkConfig;

    public SparkMaxWrapper(int deviceId, MotorType motorType,
                           SmartMotorControllerConfig smartConfig, SparkConfig sparkConfig) {
        super(smartConfig);
        this.smartConfig = smartConfig;
        this.sparkConfig = sparkConfig;
        motor = new SparkMax(deviceId, motorType);
        encoder = motor.getEncoder();
        closedLoopController = motor.getClosedLoopController();
        applyConfig();
    }

    public SparkMaxWrapper(int deviceId, SmartMotorControllerConfig smartConfig, SparkConfig sparkConfig) {
        this(deviceId, MotorType.kBrushless, smartConfig, sparkConfig);
    }

    public SparkMaxWrapper(int deviceId) {
        this(deviceId, SmartMotorControllerConfig.builder().build(), SparkConfig.builder().build());
    }

    public void setConfig(SmartMotorControllerConfig smartConfig, SparkConfig sparkConfig) {
        this.smartConfig = smartConfig;
        this.sparkConfig = sparkConfig;
        applyConfig();
    }

    public void setSmartConfig(SmartMotorControllerConfig smartConfig) {
        this.smartConfig = smartConfig;
        applyConfig();
    }

    public void setSparkConfig(SparkConfig sparkConfig) {
        this.sparkConfig = sparkConfig;
        applyConfig();
    }

//    public void updateSmartConfig(Consumer<SmartMotorControllerConfig.Builder> updater) {
//        var builder = smartConfig.toBuilder();
//        updater.accept(builder);
//        smartConfig = builder.build();
//        applyConfig();
//    }
//
//    public void updateSparkConfig(Consumer<SparkConfig.Builder> updater) {
//        var builder = sparkConfig.toBuilder();
//        updater.accept(builder);
//        sparkConfig = builder.build();
//        applyConfig();
//    }

    public SparkConfig getSparkConfig() {
        return sparkConfig;
    }

    public SparkMaxConfigAccessor getInnerSparkMaxConfig() {
        return motor.configAccessor;
    }

    @Override
    protected void applyConfig() {
        SparkMaxConfig c = new SparkMaxConfig();
        c.inverted(smartConfig.inverted());
        c.idleMode(smartConfig.neutralMode() == MotorNeutralMode.BRAKE
                ? SparkBaseConfig.IdleMode.kBrake
                : SparkBaseConfig.IdleMode.kCoast
        );

        c.smartCurrentLimit(sparkConfig.smartCurrentLimit());
        c.secondaryCurrentLimit(sparkConfig.secondaryCurrentLimit());

        c.encoder.positionConversionFactor(sparkConfig.positionConversionFactor())
                .velocityConversionFactor(sparkConfig.velocityConversionFactor());

        if (master != null) {
            c.follow(master.motor, followInverted);
        }

        sparkConfig.customConfig().accept(c);
        motor.configure(c, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    public void follow(SparkMaxWrapper master, boolean inverted) {
        this.master = master;
        this.followInverted = inverted;
        applyConfig();
    }

    public void unfollow() {
        master = null;
        applyConfig();
    }

    @Override
    public void stopMotor() {
        motor.stopMotor();
    }

    @Override
    public void disable() {
        motor.disable();
    }

    @Override
    public void setVoltage(Voltage voltage) {
        motor.setVoltage(voltage.in(Units.Volts));
    }

    @Override
    public void setDutyCycle(double output) {
        motor.set(output);
    }

    @Override
    public void setPosition(double position) {
        closedLoopController.setSetpoint(position, SparkBase.ControlType.kPosition);
    }

    @Override
    public void setVelocity(AngularVelocity velocity) {
        closedLoopController.setSetpoint(velocity.in(Units.RPM), SparkBase.ControlType.kVelocity);
    }

    @Override
    public double getPosition() {
        return encoder.getPosition();
    }

    @Override
    public AngularVelocity getVelocity() {
        return Units.RPM.of(encoder.getVelocity());
    }

    @Override
    public double getSupplyCurrent() {
        return motor.getOutputCurrent();
    }

    @Override
    public double getVoltage() {
        return motor.getBusVoltage();
    }

    @Override
    public double getDutyCycle() {
        return motor.getAppliedOutput();
    }

    @Override
    public boolean isConnected() {
        return motor.getLastError() == REVLibError.kOk;
    }

    @Override
    public void resetToFactoryDefaults() {
        smartConfig = SmartMotorControllerConfig.builder().build();
        sparkConfig = SparkConfig.builder().build();
        applyConfig();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("SparkMax");
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