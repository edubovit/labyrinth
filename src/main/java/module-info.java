module net.edubovit.labyrinth {

    requires java.sql;
    requires javafx.controls;
    requires javafx.swing;
    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires com.fasterxml.jackson.annotation;

    requires static lombok;

    exports net.edubovit.labyrinth;
    opens net.edubovit.labyrinth;
    exports net.edubovit.labyrinth.domain;
    opens net.edubovit.labyrinth.domain;
    exports net.edubovit.labyrinth.dto;
    opens net.edubovit.labyrinth.dto;
    exports net.edubovit.labyrinth.repository;
    opens net.edubovit.labyrinth.repository;
    exports net.edubovit.labyrinth.service;
    opens net.edubovit.labyrinth.service;
    exports net.edubovit.labyrinth.web;
    opens net.edubovit.labyrinth.web;

}
