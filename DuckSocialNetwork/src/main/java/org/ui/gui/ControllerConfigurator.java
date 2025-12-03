package org.ui.gui;

@FunctionalInterface
interface ControllerConfigurator {
    void configure(Object controller);
}