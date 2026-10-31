package frc.robot.commandbased.state;

import frc.robot.commandbased.shooter.Shooter;
import frc.robot.commandbased.transport.Transport;
import itay.commandbased.state.State;
import itay.commandbased.state.StateMachine;
import itay.control.FFController;
import itay.control.FFSettings;
import itay.control.PIDSettings;
import itay.util.UnifiedControlMode;

import java.util.function.DoubleSupplier;

public class ShootingStateMachine {

    private static ShootingStateMachine shootingStateMachine;

    public static ShootingStateMachine getInstance() {
        if (shootingStateMachine == null) {
            shootingStateMachine = new ShootingStateMachine();
        }

        return shootingStateMachine;
    }

    private static final PIDSettings SHOOTER_PID_SETTINGS =
            new PIDSettings(0, 0.02, 0, 0, 0.2, 0.1);
    private static final FFSettings SHOOTER_FF_SETTINGS =
            new FFSettings(0, 0, FFController.ControlMode.LINEAR_VELOCITY);

    private static final double TRANSPORT_SPEED = 0.1;

    private final Shooter shooter = Shooter.getInstance();
    private final Transport transport = Transport.getInstance();

    private final StateMachine stateMachine;

    private DoubleSupplier targetShooterVelocity = () -> 0;
    private boolean shootRequested = false;
    private boolean feedRequested = false;

    private ShootingStateMachine() {
        State idle = idle();
        State charging = charging();
        State charged = charged();
        State feeding = feeding();

        idle.addTrigger(() -> shootRequested, charging);

        charging.addTrigger(this::shooterAtTargetVelocity, charged);
        charging.addTrigger(() -> !shootRequested, idle);

        charged.addTrigger(() -> feedRequested, feeding);
        charged.addTrigger(() -> !shootRequested, idle);

        feeding.addTrigger(() -> !feedRequested, charged);
        feeding.addTrigger(() -> !shootRequested, idle);

        stateMachine = new StateMachine(idle);
    }

    public void updateStateMachine() {
        stateMachine.update();
    }

    public void requestShooting(DoubleSupplier velocity) {
        targetShooterVelocity = velocity;
        shootRequested = true;
    }

    public void cancelShooting() {
        shootRequested = false;
    }

    public void requestFeeding() {
        feedRequested = true;
    }

    public void cancelFeeding() {
        feedRequested = false;
    }

    private boolean shooterAtTargetVelocity() {
        return shooter.atSetpoint(UnifiedControlMode.VELOCITY,
                targetShooterVelocity.getAsDouble(), SHOOTER_PID_SETTINGS.getTolerance());
    }

    private void maintainShooterVelocity() {
        shooter.setSetpoint(UnifiedControlMode.VELOCITY, targetShooterVelocity.getAsDouble());
    }

    private void configureShooterCloseLoop() {
        shooter.configureClosedLoop(SHOOTER_PID_SETTINGS, SHOOTER_FF_SETTINGS);
    }

    private State idle() {
        return new State("idle",
                () -> {
                    shooter.stop();
                    transport.stop();
                },
                () -> {
                },
                shooter,
                transport
        );
    }

    private State charging() {
        return new State("charging",
                this::maintainShooterVelocity,
                this::configureShooterCloseLoop
        );
    }

    private State charged() {
        return new State("charged",
                () -> {
                },
                this::maintainShooterVelocity,
                shooter
        );
    }

    private State feeding() {
        return new State("feeding",
                () -> {
                },
                () -> {
                    maintainShooterVelocity();
                    transport.move(TRANSPORT_SPEED);
                },
                shooter,
                transport
        );
    }
}