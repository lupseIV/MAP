package org.example.paginarefiltraredb.gui.util.form;

public enum FieldType {
    // Basic Text
    TEXT,           // Single line text (TextField)
    PASSWORD,       // Masked text (PasswordField)
    TEXTAREA,       // Multi-line text (TextArea) - Good for descriptions/addresses

    // Numbers
    NUMBER,         // Integers only
    DECIMAL,        // Floating point numbers (Double)

    // Selection
    CHECKBOX,       // Boolean true/false
    CHOICE,         // Dropdown (ComboBox) - Good for long lists
    RADIO,          // Radio Buttons - Good for short lists (e.g. Male/Female)

    // Complex Types
    DATE,           // DatePicker
    COLOR,          // ColorPicker
    FILE            // FileChooser (Path as String)
}