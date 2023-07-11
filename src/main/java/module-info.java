module br.com.don {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.dustinredmond.fxtrayicon;

    opens br.com.don to javafx.fxml;
    exports br.com.don;
}
