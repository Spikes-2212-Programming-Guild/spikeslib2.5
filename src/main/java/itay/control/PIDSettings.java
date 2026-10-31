
package itay.control;

import edu.wpi.first.math.controller.PIDController;

import java.util.function.DoubleSupplier;

// TODO - should all gains be final?
public class PIDSettings {

    private DoubleSupplier kP;
    private DoubleSupplier kI;
    private DoubleSupplier kD;
    private DoubleSupplier iZone;
    private DoubleSupplier tolerance;
    private DoubleSupplier waitTime;
    private boolean isConstant;

    public PIDSettings(DoubleSupplier kP, DoubleSupplier kI, DoubleSupplier kD,
                       DoubleSupplier iZone, DoubleSupplier tolerance, DoubleSupplier waitTime) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.iZone = iZone;
        this.tolerance = tolerance;
        this.waitTime = waitTime;
        isConstant = false;
    }

    public PIDSettings(double kP, double kI, double kD, double iZone, double tolerance, double waitTime) {
        this(() -> kP, () -> kI, () -> kD, () -> iZone, () -> tolerance, () -> waitTime);
        isConstant = true;
    }

    public PIDSettings(double kP, double kI, double kD, double tolerance, double waitTime) {
        this(kP, kI, kD, 0, tolerance, waitTime);
    }

    public static PIDSettings EMPTY_PID_SETTINGS() {
        return new PIDSettings(0, 0, 0, 0, 0, 0);
    }

    public double getkP() {
        return kP.getAsDouble();
    }

    public double getkI() {
        return kI.getAsDouble();
    }

    public double getkD() {
        return kD.getAsDouble();
    }

    public double getiZone() {
        return iZone.getAsDouble();
    }

    public double getTolerance() {
        return tolerance.getAsDouble();
    }

    public double getWaitTime() {
        return waitTime.getAsDouble();
    }

    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PIDSettings that = (PIDSettings) o;
        return Double.compare(getkP(), that.getkP()) == 0
                && Double.compare(getkI(), that.getkI()) == 0
                && Double.compare(getkD(), that.getkD()) == 0
                && Double.compare(getiZone(), that.getiZone()) == 0
                && Double.compare(getTolerance(), that.getTolerance()) == 0
                && Double.compare(getWaitTime(), that.getWaitTime()) == 0;
    }

    public static PIDController buildControllerFromSettings(PIDSettings settings) {
        PIDController controller = new PIDController(settings.getkP(), settings.getkI(), settings.getkD());
        controller.setTolerance(settings.getTolerance());
        controller.setIZone(settings.getiZone());
        return controller;
    }
}
