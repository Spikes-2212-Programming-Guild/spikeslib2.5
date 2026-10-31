package itay.motors.talonfx;

import com.ctre.phoenix6.configs.TalonFXConfiguration;

import java.util.function.Consumer;

public record TalonFXConfig(
        Consumer<TalonFXConfiguration> customConfig,
        double supplyCurrentLimit,
        boolean supplyCurrentLimitEnabled,
        double statorCurrentLimit,
        boolean statorCurrentLimitEnabled,
        double rotorToSensorRatio,
        double sensorToMechanismRatio,
        boolean continuousWrap
) {

    public static Builder builder() {
        return new Builder();
    }

//    public Builder toBuilder() {
//        return new Builder()
//                .customConfig(customConfig)
//                .supplyCurrentLimit(supplyCurrentLimit)
//                .supplyCurrentLimitEnabled(supplyCurrentLimitEnabled)
//                .statorCurrentLimit(statorCurrentLimit)
//                .statorCurrentLimitEnabled(statorCurrentLimitEnabled)
//                .rotorToSensorRatio(rotorToSensorRatio)
//                .sensorToMechanismRatio(sensorToMechanismRatio)
//                .continuousWrap(continuousWrap);
//    }

    public static class Builder {

        private Consumer<TalonFXConfiguration> customConfig = config -> {
        };

        private double supplyCurrentLimit = 40;
        private boolean supplyCurrentLimitEnabled = false;

        private double statorCurrentLimit = 80;
        private boolean statorCurrentLimitEnabled = false;

        private double rotorToSensorRatio = 1.0;
        private double sensorToMechanismRatio = 1.0;
        private boolean continuousWrap = false;

        public Builder customConfig(Consumer<TalonFXConfiguration> config) {
            this.customConfig = config;
            return this;
        }

        public Builder supplyCurrentLimit(double limit) {
            this.supplyCurrentLimit = limit;
            this.supplyCurrentLimitEnabled = true;
            return this;
        }

        public Builder supplyCurrentLimitEnabled(boolean enabled) {
            this.supplyCurrentLimitEnabled = enabled;
            return this;
        }

        public Builder statorCurrentLimit(double limit) {
            this.statorCurrentLimit = limit;
            this.statorCurrentLimitEnabled = true;
            return this;
        }

        public Builder statorCurrentLimitEnabled(boolean enabled) {
            this.statorCurrentLimitEnabled = enabled;
            return this;
        }

        public Builder rotorToSensorRatio(double ratio) {
            this.rotorToSensorRatio = ratio;
            return this;
        }

        public Builder sensorToMechanismRatio(double ratio) {
            this.sensorToMechanismRatio = ratio;
            return this;
        }

        public Builder continuousWrap(boolean enabled) {
            this.continuousWrap = enabled;
            return this;
        }

        public TalonFXConfig build() {
            return new TalonFXConfig(
                    customConfig, supplyCurrentLimit, supplyCurrentLimitEnabled, statorCurrentLimit,
                    statorCurrentLimitEnabled, rotorToSensorRatio, sensorToMechanismRatio, continuousWrap
            );
        }
    }
}