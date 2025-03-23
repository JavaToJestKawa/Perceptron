import java.util.Arrays;

public class Vec {
    private double[] components;

    public Vec(double[] components) {
        this.components = components;
    }

    public double[] getComponents() {
        return components;
    }

    public void setComponents(double[] components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return Arrays.toString(components);
    }
}
