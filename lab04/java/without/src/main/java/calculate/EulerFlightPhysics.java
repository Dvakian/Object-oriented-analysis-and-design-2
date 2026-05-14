package calculate;

import core.*;
import java.util.ArrayList;
import java.util.List;

public class EulerFlightPhysics extends FlightPhysics {

    private static final double G = 9.81;

    @Override
    public String getName() {
        return "Метод Эйлера";
    }

    @Override
    public SimulationResult simulate(FlightParams p) {
        double x = 0.0;
        double y = p.y0;

        double rad = Math.toRadians(p.angle);
        double vx = p.v0 * Math.cos(rad);
        double vy = p.v0 * Math.sin(rad);

        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();

        xs.add(x);
        ys.add(y);

        double maxHeight = p.y0;

        while (y >= 0) {
            double xPrev = x;
            double yPrev = y;

            double v = Math.sqrt(vx * vx + vy * vy);

            double rho = p.rho0 * Math.exp(-y / p.H);

            double drag = (p.k * (rho / p.rho0)) / p.m;

            double ax = -drag * v * vx;
            double ay = -G - drag * v * vy;

            x += vx * p.dt;
            y += vy * p.dt;

            vx += ax * p.dt;
            vy += ay * p.dt;

            if (y < 0) {
                double r = yPrev / (yPrev - y);
                double xHit = xPrev + r * (x - xPrev);

                xs.add(xHit);
                ys.add(0.0);

                x = xHit;
                y = 0.0;
                break;
            }

            xs.add(x);
            ys.add(y);

            maxHeight = Math.max(maxHeight, y);
        }

        double finalSpeed = Math.sqrt(vx * vx + vy * vy);

        return new SimulationResult(xs, ys, x, maxHeight, finalSpeed);
    }
}
