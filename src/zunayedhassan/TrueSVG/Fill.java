package zunayedhassan.TrueSVG;

import java.util.ArrayList;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 *
 * @author Zunayed Hassan
 */
public class Fill {
    private ArrayList<String> _ids      = new ArrayList<>();
    
    public  ArrayList<Stop>   Stops     = new ArrayList<>();
    public  Paint             PaintData = null;
    
    public Fill() {
        
    }
    
    public void SetId(String id) {
        this._ids.add(id);
    }
    
    public boolean IsIdExists(String id) {
        if (this._ids.size() == 0) {
            return false;
        }
        
        for (String currentId : this._ids) {
            if (currentId.equals(id)) {
                return true;
            }
        }
        
        return false;
    }
}
