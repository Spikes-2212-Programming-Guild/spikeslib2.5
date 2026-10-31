package itay.util.smartmotorcontrollers;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.*;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue;
import edu.wpi.first.math.MathUtil;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.util.UnifiedControlMode;

/*
TODO - make it so you have to give the motor settings when u create it?
 */
public class SmartTalonFX extends TalonFXWrapper {

    protected final Slot0Configs closedLoopConfig;
    protected PIDSettings pidSettings = PIDSettings.EMPTY_PID_SETTINGS();
    protected FFSettings ffSettings = FFSettings.EMPTY_FF_SETTINGS();

    public SmartTalonFX(int deviceId, CANBus canbus) {
        super(deviceId, canbus);
        closedLoopConfig = new Slot0Configs();
        configureLoop(pidSettings, ffSettings);
    }

    /**
     * note - the motor doesn't use tolerance, wait time and iZone!
     */
    public void configurePID(PIDSettings pidSettings, boolean applyConfiguration) {
        if (this.pidSettings.equals(pidSettings)) return;
        this.pidSettings = new PIDSettings(
                pidSettings.getkP(), pidSettings.getkI(), pidSettings.getkD(),
                pidSettings.getTolerance(), pidSettings.getWaitTime()
        );

        closedLoopConfig.kP = pidSettings.getkP();
        closedLoopConfig.kI = pidSettings.getkI();
        closedLoopConfig.kD = pidSettings.getkD();
        if (applyConfiguration) applyClosedLoopConfig();
    }

    public void configureFF(FFSettings ffSettings, boolean applyConfiguration) {
        if (this.ffSettings.equals(ffSettings)) return;
        this.ffSettings = new FFSettings(
                ffSettings.getkS(), ffSettings.getkV(), ffSettings.getkA(),
                ffSettings.getkG(), ffSettings.getControlMode()
        );
        closedLoopConfig.kS = ffSettings.getkS();
        closedLoopConfig.kV = ffSettings.getkV();
        closedLoopConfig.kA = ffSettings.getkA();
        closedLoopConfig.kG = ffSettings.getkG();
        StaticFeedforwardSignValue kSType;
        GravityTypeValue kGType;
        switch (ffSettings.getControlMode()) {
            case LINEAR_POSITION -> {
                kSType = StaticFeedforwardSignValue.UseClosedLoopSign;
                kGType = GravityTypeValue.Elevator_Static;
            }
            case ANGULAR_POSITION -> {
                kSType = StaticFeedforwardSignValue.UseClosedLoopSign;
                kGType = GravityTypeValue.Arm_Cosine;
            }
            case LINEAR_VELOCITY -> {
                kSType = StaticFeedforwardSignValue.UseVelocitySign;
                kGType = GravityTypeValue.Elevator_Static;
            }
            case ANGULAR_VELOCITY -> {
                kSType = StaticFeedforwardSignValue.UseVelocitySign;
                kGType = GravityTypeValue.Arm_Cosine;
            }
            default -> throw new IllegalArgumentException("Invalid feed forward type!");
        }
        closedLoopConfig.withStaticFeedforwardSign(kSType);
        closedLoopConfig.withGravityType(kGType);
        if (applyConfiguration) applyClosedLoopConfig();
    }

    public void configureLoop(PIDSettings pidSettings, FFSettings ffSettings) {
        boolean pidChanged = !this.pidSettings.equals(pidSettings);
        boolean ffChanged = !this.ffSettings.equals(ffSettings);

        configurePID(pidSettings, false);
        configureFF(ffSettings, false);

        if (pidChanged || ffChanged) {
            applyClosedLoopConfig();
        }
    }

    public void setSetpoint(UnifiedControlMode controlMode, double setpoint) {
        ControlRequest request = switch (controlMode) {
            case CURRENT -> new TorqueCurrentFOC(setpoint);
            case PERCENT_OUTPUT -> new DutyCycleOut(setpoint);
            case TRAPEZOID_PROFILE -> new MotionMagicDutyCycle(setpoint);
            case VOLTAGE -> new VoltageOut(setpoint);
            case VELOCITY -> new VelocityDutyCycle(setpoint);
            case POSITION -> new PositionDutyCycle(setpoint);
        };
        talonFX.setControl(request);
    }

    public boolean atSetpoint(UnifiedControlMode controlMode, double tolerance, double setpoint) {
        double value = switch (controlMode) {
            case VELOCITY -> getVelocity();
            case POSITION, TRAPEZOID_PROFILE -> getPosition();
            case CURRENT -> getCurrent();
            case PERCENT_OUTPUT -> get();
            case VOLTAGE -> getVoltage();
        };
        return MathUtil.isNear(setpoint, value, tolerance);
    }

    public PIDSettings getPIDSettings() {
        return pidSettings;
    }

    public FFSettings getFFSettings() {
        return ffSettings;
    }

    private void applyClosedLoopConfig() {
        talonFX.getConfigurator().apply(closedLoopConfig, 0.2);
    }
}
