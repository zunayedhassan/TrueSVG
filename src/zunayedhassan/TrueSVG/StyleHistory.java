package zunayedhassan.TrueSVG;

import java.util.ArrayList;
import javafx.scene.shape.Shape;

/**
 *
 * @author Zunayed Hassan
 */
public class StyleHistory {
    public  String            StyleClass = null;
            
    public  ArrayList<Shape>  ShapesList = new ArrayList<>();
    
    public StyleHistory(String styleClass) {
        this.StyleClass = styleClass;
    }
    
    public void AddShape(Shape shape) {
        this.ShapesList.add(shape);
    }
    
    @Override
    public String toString() {
        String output = this.StyleClass + ":";
        
        for (Shape shape : this.ShapesList) {
            output += ("\t" + shape.toString());
        }
        
        return output;
    }
}
