# TrueSVG
A simple library to draw Svg from file with JavaFX platform

##Usage

```java
import javafx.scene.layout.StackPane;
import zunayedhassan.TrueSVG.Svg;

Svg svg = new Svg("filename.svg");

StackPane pane = new StackPane();
pane.getChildren().add(svg);
```

![Screenshot](https://raw.githubusercontent.com/zunayedhassan/TrueSVG/master/screenshot.png)
