package org.example.paginarefiltraredb;

@FunctionalInterface
public interface ControllerConfigurator {
    void configure(Object controller);
}