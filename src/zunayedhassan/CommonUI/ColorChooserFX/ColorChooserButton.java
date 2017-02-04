package zunayedhassan.CommonUI.ColorChooserFX;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author Zunayed Hassan
 */
public class ColorChooserButton extends Button {
    public HBox ButtonContent = new HBox();
    public Node PreviewContent = null;
    public ColorChooser CustomColorPicker = new ColorChooser();
    
    public ColorChooserButton(Node previewContent) {
        this.PreviewContent = previewContent;
        
        this.ButtonContent.setAlignment(Pos.CENTER);
        
        Path downArrow = new Path();
        downArrow.getElements().addAll(new MoveTo(0  , 0  ),
                                       new LineTo(7  , 0  ),
                                       new LineTo(3.5, 3.5),
                                       new ClosePath());
        
        downArrow.setStroke(null);
        downArrow.setFill(Color.GRAY);
        
        Pane gap = new Pane();
        gap.setMinWidth(5);
        
        this.ButtonContent.getChildren().addAll(this.PreviewContent, gap, downArrow);
        
        super.setGraphic(this.ButtonContent);
        
        // Event
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                _onMouseClicked(event);
            }
        });
    }
    
    public void SetColor(Color color) {
        this.CustomColorPicker.ColorChooserBaseControl.SetCustomColor(color);
    }
    
    private void _onMouseClicked(MouseEvent event) {
        CustomMenuItem itemColor = new CustomMenuItem(CustomColorPicker);
        itemColor.setHideOnClick(false);
        ContextMenu contextMenu = new ContextMenu(itemColor);

        Bounds screenBounds = localToScreen(this.getBoundsInLocal());

        contextMenu.show(this, screenBounds.getMinX(), screenBounds.getMinY() + this.getHeight() + 1);
    }
}
