package org.example.paginarefiltraredb.gui.util.form;

import java.util.Collections;
import java.util.List;

public class FormField {
    private final String key;
    private final String label;
    private final FieldType type;
    private final Object initialValue;
    private final boolean isEditable;
    private final List<String> options; // For CHOICE and RADIO

    // Private Constructor (Use Static Factory methods below)
    private FormField(String key, String label, FieldType type, Object initialValue, boolean isEditable, List<String> options) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.initialValue = initialValue;
        this.isEditable = isEditable;
        this.options = (options == null) ? Collections.emptyList() : options;
    }

    // --- FACTORY METHODS (Use these to create fields) ---

    // 1. Simple Text / Password / TextArea
    public static FormField text(String key, String label, String initialValue) {
        return new FormField(key, label, FieldType.TEXT, initialValue, true, null);
    }
    public static FormField password(String key, String label) {
        return new FormField(key, label, FieldType.PASSWORD, "", true, null);
    }
    public static FormField textArea(String key, String label, String initialValue) {
        return new FormField(key, label, FieldType.TEXTAREA, initialValue, true, null);
    }

    // 2. Numbers
    public static FormField integer(String key, String label, Integer initialValue) {
        return new FormField(key, label, FieldType.NUMBER, initialValue, true, null);
    }
    public static FormField decimal(String key, String label, Double initialValue) {
        return new FormField(key, label, FieldType.DECIMAL, initialValue, true, null);
    }

    // 3. Boolean (Checkbox)
    public static FormField checkbox(String key, String label, boolean initialValue) {
        return new FormField(key, label, FieldType.CHECKBOX, initialValue, true, null);
    }

    // 4. Selections (Dropdowns & Radios)
    public static FormField choice(String key, String label, List<String> options, String initialSelection) {
        return new FormField(key, label, FieldType.CHOICE, initialSelection, true, options);
    }
    public static FormField radio(String key, String label, List<String> options, String initialSelection) {
        return new FormField(key, label, FieldType.RADIO, initialSelection, true, options);
    }

    // 5. Special Types
    public static FormField date(String key, String label, Object initialDate) {
        return new FormField(key, label, FieldType.DATE, initialDate, true, null);
    }
    public static FormField color(String key, String label, String initialHex) {
        return new FormField(key, label, FieldType.COLOR, initialHex, true, null);
    }
    public static FormField file(String key, String label, String initialPath) {
        return new FormField(key, label, FieldType.FILE, initialPath, true, null);
    }

    // 6. Read-Only Helper (e.g. showing ID)
    public static FormField readOnly(String key, String label, Object value) {
        return new FormField(key, label, FieldType.TEXT, value, false, null);
    }

    // --- Getters ---
    public String getKey() { return key; }
    public String getLabel() { return label; }
    public FieldType getType() { return type; }
    public Object getInitialValue() { return initialValue; }
    public boolean isEditable() { return isEditable; }
    public List<String> getOptions() { return options; }
}
