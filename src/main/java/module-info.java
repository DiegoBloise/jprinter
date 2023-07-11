module br.com.don {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens br.com.don to javafx.fxml;
    exports br.com.don;
}
