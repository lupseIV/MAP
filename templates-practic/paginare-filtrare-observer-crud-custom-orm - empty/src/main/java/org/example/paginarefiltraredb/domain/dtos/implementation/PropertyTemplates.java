package org.example.paginarefiltraredb.domain.dtos.implementation;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.example.paginarefiltraredb.customORM.annotations.models.EntityTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PropertyTemplates {
    // ==========================================
    // JAVAFX PROPERTY TEMPLATES (For Copy-Paste)
    // ==========================================

    // String
    public SimpleStringProperty descriptionProp = new SimpleStringProperty();

    // Long (Wrapper - use ObjectProperty for null support)
    public SimpleObjectProperty<Long> idProp = new SimpleObjectProperty<>();
    // Long (Primitive - use LongProperty)
    public SimpleLongProperty idPrimitiveProp = new SimpleLongProperty();

    // Integer (Wrapper)
    public SimpleObjectProperty<Integer> countObjectProp = new SimpleObjectProperty<>();
    // int (Primitive)
    public SimpleIntegerProperty agePrimitiveProp = new SimpleIntegerProperty();

    // Double (Wrapper)
    public SimpleObjectProperty<Double> scoreObjectProp = new SimpleObjectProperty<>();
    // double (Primitive)
    public SimpleDoubleProperty weightPrimitiveProp = new SimpleDoubleProperty();

    // Boolean (Wrapper)
    public SimpleObjectProperty<Boolean> isActiveObjectProp = new SimpleObjectProperty<>();
    // boolean (Primitive)
    public SimpleBooleanProperty isFlaggedPrimitiveProp = new SimpleBooleanProperty();

    // BigDecimal
    public SimpleObjectProperty<BigDecimal> priceProp = new SimpleObjectProperty<>();

    // LocalDate
    public SimpleObjectProperty<LocalDate> birthDateProp = new SimpleObjectProperty<>();

    // LocalDateTime
    public SimpleObjectProperty<LocalDateTime> createdAtProp = new SimpleObjectProperty<>();

    // UUID
    public SimpleObjectProperty<UUID> uuidProp = new SimpleObjectProperty<>();

    // Enum
    public SimpleObjectProperty<EntityTemplate.MyEnum> statusProp = new SimpleObjectProperty<>();

    // Byte Array
    public SimpleObjectProperty<byte[]> rawDataProp = new SimpleObjectProperty<>();

    // Relationships (Single Object)
    public SimpleObjectProperty<EntityTemplate.ParentEntity> parentProp = new SimpleObjectProperty<>();

    // Relationships (List - requires ObservableList)
    public SimpleListProperty<EntityTemplate.ChildEntity> childrenProp = new SimpleListProperty<>(FXCollections.observableArrayList());

}
