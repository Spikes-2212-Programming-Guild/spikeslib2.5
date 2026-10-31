
package itay.dashboard;

import edu.wpi.first.networktables.*;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.*;

public class RootNamespace implements Namespace {

    protected final NetworkTable table;
    protected final String name;

    private final List<PublishedField<?>> publishedFields = new ArrayList<>();
    private final HashMap<String, Sendable> sendableTable = new HashMap<>();

    public RootNamespace(String name) {
        this.name = name;
        this.table = NetworkTableInstance.getDefault().getTable(this.name);
    }

    @Override
    public DoubleSupplier addTunableDouble(String key, double defaultValue, boolean persistent) {
        DoubleEntry entry = table.getDoubleTopic(key).getEntry(defaultValue);
        entry.setDefault(defaultValue);
        entry.getTopic().setPersistent(persistent);
        return entry::get;
    }

    @Override
    public BooleanSupplier addTunableBoolean(String key, boolean defaultValue, boolean persistent) {
        BooleanEntry entry = table.getBooleanTopic(key).getEntry(defaultValue);
        entry.setDefault(defaultValue);
        entry.getTopic().setPersistent(persistent);
        return entry::get;
    }

    @Override
    public LongSupplier addTunableLong(String key, long defaultValue, boolean persistent) {
        IntegerEntry entry = table.getIntegerTopic(key).getEntry(defaultValue);
        entry.setDefault(defaultValue);
        entry.getTopic().setPersistent(persistent);
        return entry::get;
    }

    @Override
    public Supplier<String> addTunableString(String key, String defaultValue, boolean persistent) {
        StringEntry entry = table.getStringTopic(key).getEntry(defaultValue);
        entry.setDefault(defaultValue);
        entry.getTopic().setPersistent(persistent);
        return entry;
    }

    @Override
    public void publishDouble(String key, Supplier<Double> value) {
        DoublePublisher publisher = table.getDoubleTopic(key).publish();
        publish(value, publisher::set);
    }

    @Override
    public void publishBoolean(String key, Supplier<Boolean> value) {
        BooleanPublisher publisher = table.getBooleanTopic(key).publish();
        publish(value, publisher::set);
    }

    @Override
    public void publishInteger(String key, Supplier<Long> value) {
        IntegerPublisher publisher = table.getIntegerTopic(key).publish();
        publish(value, publisher::set);
    }

    @Override
    public void publishString(String key, Supplier<String> value) {
        StringPublisher publisher = table.getStringTopic(key).publish();
        publish(value, publisher::set);
    }

    @Override
    public void publishSendable(String key, Sendable sendable) {
        sendableTable.put(key, sendable);
        NetworkTable dataTable = table.getSubTable(key);
        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(dataTable);
        SendableRegistry.publish(sendable, builder);
        builder.startListeners();
        dataTable.getEntry(".name").setString(key);
    }

    @Override
    public void publishCommand(String key, Command command) {
        publishSendable(key, command);
    }

    @Override
    public void publishRunnable(String key, Runnable runnable) {
        publishCommand(key, new InstantCommand(runnable));
    }

    @Override
    public Sendable getSendable(String key) {
        return sendableTable.get(key);
    }

    private <T> void publish(Supplier<T> valueSupplier, Consumer<T> valuePublisher) {
        publishedFields.add(new PublishedField<>(valueSupplier, valuePublisher));
    }

    @Override
    public void update() {
        for (PublishedField<?> value : publishedFields) {
            value.update();
        }

        for (Sendable data : sendableTable.values()) {
            SendableRegistry.update(data);
        }
    }
}
