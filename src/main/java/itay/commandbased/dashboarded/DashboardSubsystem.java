package itay.commandbased.dashboarded;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import itay.dashboard.Namespace;
import itay.dashboard.RootNamespace;

public abstract class DashboardSubsystem extends SubsystemBase {

    protected final Namespace namespace;

    public DashboardSubsystem(Namespace namespace) {
        this.namespace = namespace;
    }

    public DashboardSubsystem(String namespaceName) {
        this.namespace = new RootNamespace(namespaceName);
    }

    @Override
    public void periodic() {
        namespace.update();
    }

    public void configureDashboard() {

    }
}
