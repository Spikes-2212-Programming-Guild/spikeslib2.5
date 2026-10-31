package itay.dashboard;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj2.command.Command;
import itay.control.FFController;
import itay.control.FFSettings;
import itay.control.PIDSettings;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public interface Namespace {

    BooleanSupplier addTunableBoolean(String name, boolean defaultValue, boolean persistent);

    default BooleanSupplier addTunableBoolean(String name, boolean defaultValue) {
        return addTunableBoolean(name, defaultValue, true);
    }

    DoubleSupplier addTunableDouble(String name, double defaultValue, boolean persistent);

    default DoubleSupplier addTunableDouble(String name, double defaultValue) {
        return addTunableDouble(name, defaultValue, true);
    }

    LongSupplier addTunableLong(String name, long defaultValue, boolean persistent);

    default LongSupplier addTunableLong(String name, long defaultValue) {
        return addTunableLong(name, defaultValue, true);
    }

    Supplier<String> addTunableString(String name, String defaultValue, boolean persistent);

    default Supplier<String> addTunableString(String name, String defaultValue) {
        return addTunableString(name, defaultValue, true);
    }

    void publishSendable(String key, Sendable sendable);

    void publishCommand(String key, Command command);

    default void publishCommand(Command command) {
        publishCommand(command.getName(), command);
    }

    void publishRunnable(String key, Runnable runnable);

    Sendable getSendable(String key);

    default PIDController addPIDController(String key, PIDSettings initialSettings) {
        return addSendable(key, PIDSettings.buildControllerFromSettings(initialSettings));
    }

    default FFController addFFController(String key, FFSettings initialSettings) {
        return addSendable(key, new FFController(initialSettings));
    }

    default PIDSettings addPIDSettings(String key, PIDSettings defaultSetting, boolean updateFromNamespace) {
        if (updateFromNamespace) {
            return new PIDSettings(
                    addTunableDouble(key + "pid/kP", defaultSetting.getkP()),
                    addTunableDouble(key + "pid/kI", defaultSetting.getkI()),
                    addTunableDouble(key + "pid/kD", defaultSetting.getkD()),
                    addTunableDouble(key + "pid/iZone", defaultSetting.getiZone()),
                    addTunableDouble(key + "pid/tolerance", defaultSetting.getTolerance()),
                    addTunableDouble(key + "pid/waitTime", defaultSetting.getWaitTime())
            );
        }
        return defaultSetting;
    }

    default PIDSettings addPIDSettings(PIDSettings defaultSetting, boolean updateFromNamespace) {
        return addPIDSettings("", defaultSetting, updateFromNamespace);
    }

    default FFSettings addFFSettings(String key, FFSettings defaultSetting, boolean updateFromNamespace) {
        if (updateFromNamespace) {
            return new FFSettings(
                    addTunableDouble(key + "ff/kS", defaultSetting.getkS()),
                    addTunableDouble(key + "ff/kV", defaultSetting.getkV()),
                    addTunableDouble(key + "ff/kA", defaultSetting.getkA()),
                    addTunableDouble(key + "ff/kG", defaultSetting.getkG()),
                    defaultSetting.getControlMode()
            );
        }
        return defaultSetting;
    }

    default FFSettings addFFSettings(FFSettings defaultSetting, boolean updateFromNamespace) {
        return addFFSettings("", defaultSetting, updateFromNamespace);
    }

    default <T extends Sendable> T addSendable(String key, T sendable) {
        publishSendable(key, sendable);
        return sendable;
    }

    default boolean containsSendable(String key) {
        return getSendable(key) != null;
    }

    void publishString(String key, Supplier<String> value);

    void publishInteger(String key, Supplier<Long> value);

    default void publishString(String key, String value) {
        publishString(key, () -> value);
    }

    void publishDouble(String key, Supplier<Double> value);

    default void publishDouble(String key, Double value) {
        publishDouble(key, () -> value);
    }

    void publishBoolean(String key, Supplier<Boolean> value);

    default void publishBoolean(String key, boolean value) {
        publishBoolean(key, () -> value);
    }

    void update();
}