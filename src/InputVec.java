public class InputVec extends Vec {
    private final String label;
    private String labelClassified;

    public InputVec(double[] components, String label) {
        super(components);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getLabelClassified() {
        return labelClassified;
    }

    public void setLabelClassified(String labelClassified) {
        this.labelClassified = labelClassified;
    }

    public void setComponents(double[] components) {
        System.out.println("Components cannot be changed for input vector.");
    }

    @Override
    public String toString() {
        return label + '\n' + labelClassified + '\n' + super.toString() + '\n';
    }
}
