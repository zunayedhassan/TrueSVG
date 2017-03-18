package zunayedhassan.TrueSVG;

/**
 *
 * @author Zunayed Hassan
 */
public class FilterPrimitiveElement {
    public static enum FILTER_PRIMITIVE_ELEMENT_TYPE {
        FE_GAUSSIAN_BLUR
    }
    
    public String                        Id                         = null;
    public FILTER_PRIMITIVE_ELEMENT_TYPE FilterPrimitiveElementType = null;
    
    public FilterPrimitiveElement(String id, FILTER_PRIMITIVE_ELEMENT_TYPE type) {
        this.Id                         = id;
        this.FilterPrimitiveElementType = type;
    }
}
