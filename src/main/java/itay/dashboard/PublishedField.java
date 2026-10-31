package itay.dashboard;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PublishedField<T> {

    private final Supplier<T> valueSupplier;
    private final Consumer<T> valuePublisher;

    private T lastPublishedValue;

    public PublishedField(Supplier<T> valueSupplier, Consumer<T> valuePublisher) {
        this.valueSupplier = valueSupplier;
        this.valuePublisher = valuePublisher;
    }

    public void update() {
        T currentValue = valueSupplier.get();

        if (Objects.equals(currentValue, lastPublishedValue))
            return;

        valuePublisher.accept(currentValue);
        lastPublishedValue = currentValue;
    }
}