package core;

import java.util.List;

public class SimulationResult {

    public final List<Double> xs;
    public final List<Double> ys;
    public final double distance;
    public final double maxHeight;
    public final double finalSpeed;

    public SimulationResult(
        List<Double> xs,
        List<Double> ys,
        double distance,
        double maxHeight,
        double finalSpeed
    ) {
        this.xs = xs;
        this.ys = ys;
        this.distance = distance;
        this.maxHeight = maxHeight;
        this.finalSpeed = finalSpeed;
    }
}
