package core;

public class FlightParams {

    public final double v0;
    public final double y0;
    public final double angle;
    public final double m;
    public final double k;
    public final double rho0;
    public final double H;
    public final double dt;

    public FlightParams(
        double v0,
        double y0,
        double angle,
        double m,
        double k,
        double rho0,
        double H,
        double dt
    ) {
        this.v0 = v0;
        this.y0 = y0;
        this.angle = angle;
        this.m = m;
        this.k = k;
        this.rho0 = rho0;
        this.H = H;
        this.dt = dt;
    }
}
