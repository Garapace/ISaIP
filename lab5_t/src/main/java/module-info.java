module org.is_authorization {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires jdk.compiler;

    opens org.is_authorization to javafx.fxml;
    exports org.is_authorization;
    exports org.is_authorization.controllers;
    opens org.is_authorization.controllers to javafx.fxml;
}