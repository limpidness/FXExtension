module at.limpidness.fxextension {
    requires javafx.controls;
    requires javafx.fxml;


    opens at.limpidness.fxextension.layout to javafx.fxml;
    exports at.limpidness.fxextension.layout;
}