package org.example.paginarefiltraredb.customORM.annotations.models;

import org.example.paginarefiltraredb.customORM.annotations.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// 1. CLASS LEVEL ANNOTATION
// Defines the table name. If omitted, the simple class name is used.
@DbTable(name = "my_table_name")
public class EntityTemplate {

    // ==========================================
    // PRIMARY KEY
    // ==========================================

    // Standard Auto-Increment ID (Long maps to BIGSERIAL)
    @DbId
    private Long id;

    // ==========================================
    // TEXT & STRINGS
    // ==========================================

    // Standard String (VARCHAR(255), Nullable by default)
    @DbColumn(name = "description")
    private String description;

    // String with UNIQUE constraint (e.g., username, code)
    @DbColumn(unique = true)
    private String uniqueCode;

    // String that cannot be NULL
    @DbColumn(nullable = false)
    private String mandatoryField;

    // "Email" heuristic: SchemaGenerator automatically marks this UNIQUE if name contains "email"
    @DbColumn(name = "user_email")
    private String email;

    // ==========================================
    // NUMBERS (Primitives & Wrappers)
    // ==========================================

    // Integer Wrapper (Nullable INTEGER)
    @DbColumn(name = "count_val")
    private Integer countObject;

    // int Primitive (Automatically NOT NULL in SQL because primitives can't be null)
    @DbColumn(name = "age_val")
    private int agePrimitive;

    // Double Wrapper (Nullable DOUBLE PRECISION)
    @DbColumn(name = "score_val")
    private Double scoreObject;

    // double Primitive (Automatically NOT NULL)
    @DbColumn(name = "weight_val")
    private double weightPrimitive;

    // Big Decimal (DECIMAL(19, 2) - Good for Money)
    @DbColumn(name = "price")
    private BigDecimal price;

    // ==========================================
    // BOOLEANS
    // ==========================================

    // Boolean Wrapper (Nullable BOOLEAN)
    @DbColumn(name = "is_active_obj")
    private Boolean isActiveObject;

    // boolean Primitive (Automatically NOT NULL)
    @DbColumn(name = "is_flagged_prim")
    private boolean isFlaggedPrimitive;

    // ==========================================
    // DATES & SPECIAL TYPES
    // ==========================================

    // Date only (SQL DATE)
    @DbColumn(name = "birth_date")
    private LocalDate birthDate;

    // Date and Time (SQL TIMESTAMP)
    @DbColumn(name = "created_at")
    private LocalDateTime createdAt;

    // UUID (SQL UUID)
    @DbColumn(name = "identifier_uuid")
    private UUID uuid;

    // Enum (Saved as VARCHAR(50))
    @DbColumn(name = "status_enum")
    private MyEnum status;

    // Byte Array (SQL BYTEA - for images/blobs)
    @DbColumn(name = "image_data")
    private byte[] rawData;

    // ==========================================
    // RELATIONSHIPS
    // ==========================================

    // 1. Many-To-One (Creates a Foreign Key column here)
    // Use this for the "Child" side of a relationship
    @DbManyToOne
    private ParentEntity parent;

    // 2. One-To-One (Creates a Foreign Key column here)
    @DbOneToOne
    private ProfileEntity profile;

    // 3. One-To-Many (Inverse side - NO COLUMN created in this table)
    // This is skipped by SchemaGenerator but usually marks a List
    @DbOneToMany
    private List<ChildEntity> children;

    // 4. Many-To-Many (Creates a separate JOIN TABLE)
    @DbManyToMany
    private List<TagEntity> tags;

    // ==========================================
    // DUMMY ENUM & CLASSES FOR TEMPLATE
    // ==========================================
    public enum MyEnum { ACTIVE, INACTIVE }
    public static class ParentEntity {}
    public static class ProfileEntity {}
    public static class ChildEntity {}
    public static class TagEntity {}
}