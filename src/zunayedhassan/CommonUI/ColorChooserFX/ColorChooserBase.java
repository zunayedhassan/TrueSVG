package zunayedhassan.CommonUI.ColorChooserFX;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Priority;

/**
 *
 * @author Zunayed Hassan
 */
public class ColorChooserBase extends VBox {
    private final ObjectProperty<Color> _currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    public final ObjectProperty<Color> CustomColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
    
    private Pane _colorRect = new StackPane();
    private final Pane _colorBar = new Pane();
    private final Pane _colorRectOverlayOne = new Pane();
    private final Pane _colorRectOverlayTwo = new Pane();
    private Region _colorRectIndicator = new Region();
    private final Region _colorBarIndicator = new Region();
    private Pane _newColorRect = new Pane();
    
    private DoubleProperty _hue = new SimpleDoubleProperty(-1);
    private DoubleProperty _sat = new SimpleDoubleProperty(-1);
    private DoubleProperty _bright = new SimpleDoubleProperty(-1);
    
    private DoubleProperty _alpha = new SimpleDoubleProperty(100) {
        @Override
        protected void invalidated() {
            SetCustomColor(new Color(GetCustomColor().getRed(), GetCustomColor().getGreen(), GetCustomColor().getBlue(), CLAMP(_alpha.get() / 100)));
        }
    };
    
    public ColorChooserBase() {
        super(5);
        super.setPadding(new Insets(10));
        
        VBox box = new VBox();
        box.setStyle("-fx-spacing: 0.75em; -fx-pref-height: 16.666667em; -fx-alignment: top-left; -fx-fill-height: true; -fx-max-width: 16.666667em;");
        
        this.CustomColorProperty.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                _colorChanged();
            }
        });
        
        this._colorRectIndicator.setStyle("-fx-background-color: null; -fx-border-color: white; -fx-border-radius: 0.4166667em; -fx-translate-x: -0.4166667em; -fx-translate-y: -0.4166667em; -fx-pref-width: 0.833333em; -fx-pref-height: 0.833333em; -fx-effect: dropshadow(three-pass-box, black, 2, 0.0, 0, 1);");
        this._colorRectIndicator.setManaged(false);
        this._colorRectIndicator.setMouseTransparent(true);
        this._colorRectIndicator.setCache(true);
        
        
        final Pane colorRectOpacityContainer = new StackPane();
        
        this._colorRect.setStyle("-fx-min-width: 16.666667em; -fx-min-height: 16.666667em;");
        this._colorRect.setBackground(new Background(new BackgroundImage(new Image(this.getClass().getResourceAsStream("images/pattern-transparent.png")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        
        Pane colorRectHue = new Pane();
        
        colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(_hue);
            }
            
            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(Color.hsb(_hue.getValue(), 1.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });
        
        this._colorRectOverlayOne.setStyle("-fx-min-width: 16.666667em; -fx-min-height: 16.666667em;");
        this._colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(
                        0, 0, 1, 0,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))), 
                CornerRadii.EMPTY, Insets.EMPTY)));
        
        EventHandler<MouseEvent> rectMouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final double x = event.getX();
                final double y = event.getY();
                
                _sat.set(CLAMP(x / _colorRect.getWidth()) * 100);
                _bright.set(100 - (CLAMP(y / _colorRect.getHeight()) * 100));
                
                _updateHSBColor();
            }
        };
        
        this._colorRectOverlayTwo.setStyle("-fx-min-width: 16.666667em; -fx-min-height: 16.666667em;");
        this._colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(
                        0, 0, 0, 1,
                        true,
                        CycleMethod.NO_CYCLE, 
                new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))), 
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        
        this._colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        this._colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);
        
        Pane colorRectBlackBorder = new Pane();
        colorRectBlackBorder.setMouseTransparent(true);
        colorRectBlackBorder.setStyle("-fx-min-width: 16.666667em; -fx-min-height: 16.666667em; -fx-border-color: derive(#ececec, -20%);");
        
        this._colorBar.setStyle("-fx-min-height: 1.666667em; -fx-min-width: 16.666667em; -fx-max-height: 1.666667em; -fx-border-color: derive(#ececec, -20%);");
        this._colorBar.setBackground(new Background(new BackgroundFill(_CREATE_HUE_GRADIENT(), CornerRadii.EMPTY, Insets.EMPTY)));
        
        this._colorBarIndicator.setStyle("-fx-border-radius: 0.333333em; -fx-border-color: white; -fx-effect: dropshadow(three-pass-box, black, 2, 0.0, 0, 1); -fx-pref-height: 2em; -fx-pref-width: 0.833333em; -fx-translate-y: -0.1666667em; -fx-translate-x: -0.4166667em;");
        this._colorBarIndicator.setMouseTransparent(true);
        this._colorBarIndicator.setCache(true);
        
        this._colorRectIndicator.layoutXProperty().bind(this._sat.divide(100).multiply(this._colorRect.widthProperty()));
        this._colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, this._bright.divide(100)).multiply(this._colorRect.heightProperty()));
        this._colorBarIndicator.layoutXProperty().bind(this._hue.divide(360).multiply(this._colorBar.widthProperty()));
        colorRectOpacityContainer.opacityProperty().bind(this._alpha.divide(100));
        
        EventHandler<MouseEvent> barMouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final double x = event.getX();
                _hue.set(CLAMP(x / _colorRect.getWidth()) * 360);
                _updateHSBColor();
            }
        };
        
        this._colorBar.setOnMouseDragged(barMouseHandler);
        this._colorBar.setOnMousePressed(barMouseHandler);
        
        this._newColorRect.setStyle("-fx-min-width: 10.666667em; -fx-min-height: 1.75em; -fx-pref-width: 10.666667em; -fx-pref-height: 1.75em; -fx-border-color: derive(#ececec, -20%);");
        
        this._newColorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                bind(CustomColorProperty);
            } 
            
            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(CustomColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });
        
        this._colorBar.getChildren().setAll(this._colorBarIndicator);
        colorRectOpacityContainer.getChildren().setAll(colorRectHue, this._colorRectOverlayOne, this._colorRectOverlayTwo);   
        this._colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, this._colorRectIndicator);
        VBox.setVgrow(this._colorRect, Priority.SOMETIMES);
        box.getChildren().addAll(this._colorBar, this._colorRect, this._newColorRect);
        
        this.getChildren().add(box);
        
        if (this._currentColorProperty.get() == null) {
            this._currentColorProperty.set(Color.TRANSPARENT);
        }
        
        this._updateValues();
    }
    
    public static double CLAMP(double value) {
        return (value < 0) ? 0 : ((value > 1) ? 1 : value);
    }
    
    public void SetCustomColor(Color color) {
        this.CustomColorProperty.set(color);
    }
    
    public Color GetCustomColor() {
        return this.CustomColorProperty.get();
    }
    
    private void _colorChanged() {
        this._hue.set(this.GetCustomColor().getHue());
        this._sat.set(this.GetCustomColor().getSaturation() * 100);
        this._bright.set(this.GetCustomColor().getBrightness() * 100);
    }
    
    private void _updateHSBColor() {
        Color newColor = Color.hsb(this._hue.get(), CLAMP(this._sat.get() / 100), CLAMP(this._bright.get() / 100), CLAMP(this._alpha.get() / 100));
        SetCustomColor(newColor);
    }
    
    private static LinearGradient _CREATE_HUE_GRADIENT() {
        double offset;
        
        Stop[] stops = new Stop[255];
        
        for (int x = 0; x < 255; x++) {
            offset = (double) ((1.0 / 255) * x);
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        
        return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }
    
    private Color _getCurrentColor() {
        return this._currentColorProperty.get();
    }
    
    private void _updateValues() {
        this._hue.set(this._getCurrentColor().getHue());
        this._sat.set(this._getCurrentColor().getSaturation() * 100);
        this._bright.set(this._getCurrentColor().getBrightness() * 100);
        this._alpha.set(this._getCurrentColor().getOpacity() * 100);
        
        this.SetCustomColor(Color.hsb(this._hue.get(), CLAMP(this._sat.get() / 100), CLAMP(this._bright.get() / 100), CLAMP(this._alpha.get()/100)));
    }
    
    @Override 
    protected void layoutChildren() {
        super.layoutChildren();            
        this._colorRectIndicator.autosize();
    }
}
