module jprinter {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.desktop;
    requires java.logging;
    requires transitive com.dustinredmond.fxtrayicon;

    opens com.dbl.jprinter to javafx.fxml;
    exports com.dbl.jprinter;
}