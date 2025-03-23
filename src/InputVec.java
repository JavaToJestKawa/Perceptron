public class InputVec extends Vec{
    private final String label;
    private String labelClassified;

    public InputVec(double[] components, String label) {
        super(components);
        this.label = label;
    }

    public void setComponents(double[] components) {
        System.out.println("Components cannot be changed for input vector.");
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + '\n' + labelClassified + '\n' + super.toString() + '\n';
    }
}
