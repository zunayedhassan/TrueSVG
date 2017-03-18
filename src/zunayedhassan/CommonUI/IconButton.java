package zunayedhassan.CommonUI;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Zunayed Hassan
 */
public class IconButton extends Button {
    public IconButton(String icon) {
        this.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(icon))));
    }
    
    public IconButton(String icon, String tooltipText) {
        this(icon);
        
        if (tooltipText != null) {
            this.setTooltip(new Tooltip(tooltipText));
        }
    }
    
    public IconButton(String title, String icon, String tooltipText) {
        this(icon, tooltipText);
        this.setText(title);
    }
    
    public IconButton(String title, String icon, String tooltipText, ContentDisplay contentDisplay) {
        this(title, icon, tooltipText);
        this.setContentDisplay(contentDisplay);
    }
    
    public ImageView GetContent() {
        return ((ImageView) this.getGraphic());
    }
}
