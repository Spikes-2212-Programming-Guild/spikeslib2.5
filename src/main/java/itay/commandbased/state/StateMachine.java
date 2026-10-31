package itay.commandbased.state;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    private State defaultState;

    public StateMachine(State defaultState) {
        this.defaultState = defaultState;
    }

    public void setState(State state) {
        CommandScheduler.getInstance().cancel(defaultState.getCommand());
        defaultState = state;
        CommandScheduler.getInstance().schedule(defaultState.getCommand());
        DataLogManager.log(defaultState.getName());
    }

    public void update() {
        HashMap<Trigger, State> triggerSet = defaultState.getTriggerSet();
        for (Map.Entry<Trigger, State> entry : triggerSet.entrySet()) {
            if (entry.getKey().getAsBoolean()) {
                setState(entry.getValue());
            }
        }
    }
}
