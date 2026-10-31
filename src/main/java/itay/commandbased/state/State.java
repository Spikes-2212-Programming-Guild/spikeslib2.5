package itay.commandbased.state;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/*

state:

initialize
periodic
end

 */
public class State {

    private final HashMap<Trigger, State> triggerSet = new HashMap<>();
    private final Supplier<Command> command;
    private final String name;

    public State(String name, Runnable initialize, Runnable execute, Subsystem... requirements) {
        this.name = name;
        this.command = () -> Commands.startRun(initialize, execute, requirements);
    }

    public void addTrigger(Trigger trigger, State state) {
        triggerSet.put(trigger, state);
    }

    public void addTrigger(BooleanSupplier booleanSupplier, State state) {
        addTrigger(new Trigger(booleanSupplier), state);
    }

    public HashMap<Trigger, State> getTriggerSet() {
        return triggerSet;
    }

    public Command getCommand() {
        return command.get();
    }

    public String getName() {
        return name;
    }
}
