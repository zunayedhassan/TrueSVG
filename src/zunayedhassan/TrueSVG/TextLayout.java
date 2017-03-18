package zunayedhassan.TrueSVG;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 *
 * @author Zunayed Hassan
 */
public class TextLayout extends Pane {
    public TextLayout(double x, double y) {
        this.SetPosition(x, y);
    }
    
    public void SetPosition(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }
    
    public void Add(Node node) {
        this.getChildren().add(node);
    }
    
    public void Add(Text node) {
        this.getChildren().add(node);
    }
    
    @Override
    public String toString() {
        String output = null;
        
        for (Node node : this.getChildren()) {
            if (node instanceof HBox) {
                HBox line = (HBox) node;
                
                for (Node lineNode : line.getChildren()) {
                    if (lineNode instanceof Text) {
                        Text text = (Text) lineNode;
                        
                        output += text;
                    }
                }
                
                output += '\n';
            }
        }
        
        return output;
    }
}
