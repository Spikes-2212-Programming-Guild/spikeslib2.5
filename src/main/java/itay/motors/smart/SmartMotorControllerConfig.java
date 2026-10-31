package itay.motors.smart;

import itay.control.FFSettings;
import itay.control.PIDSettings;


public record SmartMotorControllerConfig(
        boolean inverted,
        MotorNeutralMode neutralMode,
        PIDSettings pidSettings,
        FFSettings ffSettings
) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .inverted(inverted)
                .neutralMode(neutralMode)
                .pid(pidSettings)
                .ff(ffSettings);
    }

    public static class Builder {

        private boolean inverted = false;
        private MotorNeutralMode neutralMode = MotorNeutralMode.COAST;

        private PIDSettings pidSettings;
        private FFSettings ffSettings;

        public Builder inverted(boolean inverted) {
            this.inverted = inverted;
            return this;
        }

        public Builder neutralMode(MotorNeutralMode neutralMode) {
            this.neutralMode = neutralMode;
            return this;
        }

        public Builder pid(PIDSettings pid) {
            this.pidSettings = pid;
            return this;
        }

        public Builder ff(FFSettings ff) {
            this.ffSettings = ff;
            return this;
        }

        public SmartMotorControllerConfig build() {
            return new SmartMotorControllerConfig(
                    inverted, neutralMode, pidSettings, ffSettings
            );
        }
    }
}