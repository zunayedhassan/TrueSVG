package zunayedhassan.CommonUI;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author ZUNAYED_PC
 */
public class IconToggleButton extends ToggleButton {
    public IconToggleButton(String icon, String tooltipText) {
        this.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(icon))));
        this.setTooltip(new Tooltip(tooltipText));
    }
    
    public IconToggleButton(String title, String icon, ContentDisplay contentDisplay) {
        super(title);
        this.setGraphic(new ImageView(new Image(this.getClass().getResourceAsStream(icon))));
        this.setContentDisplay(contentDisplay);
    }
    
    public void Reset() {
        this.setSelected(false);
    }
    
    public ImageView GetContent() {
        return ((ImageView) this.getGraphic());
    }
}
