package math.splines;

import static java.lang.Math.exp;
import static java.lang.Math.log;

public class Exponent {
    double C1 = 0;
    double C2 = 0;

    double T0 = 0;

    // e^(C1*(T0 + t) + C2)

    public Exponent(double c1, double c2, double t0) {
        C1 = c1;
        C2 = c2;
        T0 = t0;
    }

    public Exponent(double c1, double t0, double t, double x) {
        C1 = c1;
        T0 = t0;
        double tk = t + t0;
        C2 = log(x) - c1 * tk;
    }

    // here t in [0, T]
    public double sb(double t) {
        return exp(C1 * (T0 + t) + C2);
    }

    // return t in [0, T]
    public double inverse(double x) {
        return (log(x) - C2) / C1 - T0;
    }
}