package itay.motors.sparkmax;

import com.revrobotics.spark.config.SparkMaxConfig;

import java.util.function.Consumer;

public record SparkConfig(
        Consumer<SparkMaxConfig> customConfig,
        int smartCurrentLimit,
        double secondaryCurrentLimit,
        double positionConversionFactor,
        double velocityConversionFactor
) {

    public static Builder builder() {
        return new Builder();
    }

//    public Builder toBuilder() {
//        return new Builder()
//                .customConfig(customConfig)
//                .smartCurrentLimit(smartCurrentLimit)
//                .secondaryCurrentLimit(secondaryCurrentLimit)
//                .positionConversionFactor(positionConversionFactor)
//                .velocityConversionFactor(velocityConversionFactor);
//    }

    public static class Builder {

        private Consumer<SparkMaxConfig> customConfig = config -> {
        };

        private int smartCurrentLimit = 80;
        private double secondaryCurrentLimit = 80;

        private double positionConversionFactor = 1.0;
        private double velocityConversionFactor = 1.0;

        public Builder customConfig(Consumer<com.revrobotics.spark.config.SparkMaxConfig> config) {
            this.customConfig = config;
            return this;
        }

        public Builder smartCurrentLimit(int limit) {
            this.smartCurrentLimit = limit;
            return this;
        }

        public Builder secondaryCurrentLimit(double limit) {
            this.secondaryCurrentLimit = limit;
            return this;
        }

        public Builder positionConversionFactor(double factor) {
            this.positionConversionFactor = factor;
            return this;
        }

        public Builder velocityConversionFactor(double factor) {
            this.velocityConversionFactor = factor;
            return this;
        }

        public SparkConfig build() {
            return new SparkConfig(
                    customConfig,
                    smartCurrentLimit,
                    secondaryCurrentLimit,
                    positionConversionFactor,
                    velocityConversionFactor
            );
        }
    }
}