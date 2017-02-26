package zunayedhassan;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import zunayedhassan.CommonUI.IconButton;
import zunayedhassan.TrueSVG.Svg;

/**
 *
 * @author Zunayed Hassan
 */
public class RootUI extends BaseUI {
    private final ToolBar     _mainToolBar     = new ToolBar();
    private final ScrollPane  _scrollPane      = new ScrollPane();
    private final IconButton  _openIconButton  = new IconButton("Open", "icons/document-open_24.png", "Open a SVG file");
    private final FileChooser _openFileDialog  = new FileChooser();
    
    public RootUI() {
        this._initializeVariables();
        this._initializeLayout();
        this._initializeEvents();
    }
    
    private void _initializeVariables() {
        
    }
    
    private void _initializeLayout() {
        // ScrollPane
        this.setCenter(this._scrollPane);
        
        // Main Tool Bar
        this._mainToolBar.getItems().add(this._openIconButton);
        this.setTop(this._mainToolBar);
        
        // File Dialog
        this._openFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg"));
    }
    
    private void _initializeEvents() {
        // Open File
        this._openIconButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _applyOpenFile();
            }
        });
    }
    
    // Show SVG
    private void _setSvg(Svg svg) {
        this._scrollPane.setContent(svg);
        CommonTools.PRIMARY_STAGE.setTitle(svg.GetTitle());
        
        if (Settings.IS_ZOOM_TEST) {
            this._test(svg);
        }
    }
    
    // Open File
    private void _applyOpenFile() {
        File choosenFile = this._openFileDialog.showOpenDialog(CommonTools.PRIMARY_STAGE);
        
        if (choosenFile != null) {
            this._setSvg(new Svg(choosenFile.getAbsoluteFile().toString()));
        }
    }
    
    private void _test(Svg svg) {
        svg.setScaleX(4);
        svg.setScaleY(4);
        svg.setTranslateX(svg.GetWidth() * 1.5);
        svg.setTranslateY(svg.GetHeight() * 1.5);
    }
}
