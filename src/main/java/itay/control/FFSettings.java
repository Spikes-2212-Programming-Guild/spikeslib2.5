
package itay.control;

import java.util.function.DoubleSupplier;

public class FFSettings {

    private DoubleSupplier kS;
    private DoubleSupplier kV;
    private DoubleSupplier kA;
    private DoubleSupplier kG;
    private boolean isConstant;

    private final FFController.ControlMode controlMode;

    public FFSettings(DoubleSupplier kS, DoubleSupplier kV, DoubleSupplier kA,
                      DoubleSupplier kG, FFController.ControlMode controlMode) {
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;
        this.kG = kG;
        this.controlMode = controlMode;
        isConstant = false;
    }

    public FFSettings(double kS, double kV, double kA, double kG, FFController.ControlMode controlMode) {
        this(() -> kS, () -> kV, () -> kA, () -> kG, controlMode);
        isConstant = true;
    }

    public FFSettings(double kS, double kV, double kA, FFController.ControlMode controlMode) {
        this(kS, kV, kA, 0.0, controlMode);
    }

    public FFSettings(double kS, double kV, FFController.ControlMode controlMode) {
        this(kS, 0.0, 0.0, 0.0, controlMode);
    }

    public FFSettings(FFController.ControlMode controlMode) {
        this(0.0, 0.0, 0.0, 0.0, controlMode);
    }

    public static FFSettings EMPTY_FF_SETTINGS() {
        return new FFSettings(FFController.ControlMode.LINEAR_POSITION);
    }

    public double getkS() {
        return kS.getAsDouble();
    }

    public double getkV() {
        return kV.getAsDouble();
    }

    public double getkA() {
        return kA.getAsDouble();
    }

    public double getkG() {
        return kG.getAsDouble();
    }

    public boolean isConstant() {
        return isConstant;
    }

    public FFController.ControlMode getControlMode() {
        return controlMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FFSettings that = (FFSettings) o;

        return Double.compare(getkS(), that.getkS()) == 0
                && Double.compare(getkV(), that.getkV()) == 0
                && Double.compare(getkA(), that.getkA()) == 0
                && Double.compare(getkG(), that.getkG()) == 0
                && controlMode == that.controlMode;
    }
}
