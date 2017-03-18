package zunayedhassan.TrueSVG;

/**
 *
 * @author Zunayed Hassan
 */
public class FeGaussianBlur extends FilterPrimitiveElement {
    public double StandardDeviation = 0.0;
    
    public FeGaussianBlur(String id, double stdDeviation) {
        super(id, FILTER_PRIMITIVE_ELEMENT_TYPE.FE_GAUSSIAN_BLUR);
        this.StandardDeviation = stdDeviation;
    }
}
