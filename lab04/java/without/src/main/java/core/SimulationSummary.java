package core;

public class SimulationSummary {

    public final double distance;
    public final double maxHeight;
    public final double finalSpeed;

    public SimulationSummary(
        double distance,
        double maxHeight,
        double finalSpeed
    ) {
        this.distance = distance;
        this.maxHeight = maxHeight;
        this.finalSpeed = finalSpeed;
    }
}
