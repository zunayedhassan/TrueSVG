package zunayedhassan.TrueSVG;

/**
 *
 * @author Zunayed Hassan
 */
public class Filter {
    private String                 _id                     = null;
    private FilterPrimitiveElement _filterPrimitiveElement = null;
    
    public Filter(String id, FilterPrimitiveElement filterPrimitiveElement) {
        this.SetId(id);
        this.SetFilterPrimitiveElement(filterPrimitiveElement);
    }
    
    public Filter(String id) {
        this.SetId(id);
        this.SetFilterPrimitiveElement(null);
    }
    
    public void SetId(String id) {
        this._id = id;
    }
    
    public String GetId() {
        return this._id;
    }
    
    public void SetFilterPrimitiveElement(FilterPrimitiveElement filterPrimitiveElement) {
        this._filterPrimitiveElement = filterPrimitiveElement;
    }
    
    public FilterPrimitiveElement GetFilterPrimitiveElement() {
        return this._filterPrimitiveElement;
    }
}
