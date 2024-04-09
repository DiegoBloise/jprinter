module jprinter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.desktop;
    requires java.logging;
    requires com.dustinredmond.fxtrayicon;

    opens com.dbl.jprinter to javafx.fxml;
    exports com.dbl.jprinter;
}