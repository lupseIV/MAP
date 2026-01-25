package org.example.paginarefiltraredb.customORM.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SchemaGenerator {

    public static String generateCreateSQL(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        StringBuilder joinTablesSql = new StringBuilder(); // Buffer for M:N tables

        String tableName = getTableName(entityClass);
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        List<String> columnDefs = new ArrayList<>();
        List<Field> allFields = getAllFields(entityClass);

        for (Field field : allFields) {
            field.setAccessible(true);

            // SKIP logic
            if (field.isAnnotationPresent(DbOneToMany.class)) {
                // 1:N is handled by the child, but we add a comment for clarity
                sql.append("\t-- OneToMany: ").append(field.getName()).append(" (Managed by child)\n");
                continue;
            }

            //skip static fields
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            // JOIN TABLE logic (M:N)
            if (field.isAnnotationPresent(DbManyToMany.class)) {
                Class<?> targetEntity = getListGenericType(field);
                if (targetEntity != null) {
                    String targetTable = getTableName(targetEntity);
                    String joinTableName = tableName + "_" + targetTable;

                    joinTablesSql.append("\nCREATE TABLE IF NOT EXISTS ").append(joinTableName).append(" (\n")
                            .append("\t").append(tableName).append("_id BIGINT REFERENCES ").append(tableName).append("(id),\n")
                            .append("\t").append(targetTable).append("_id BIGINT REFERENCES ").append(targetTable).append("(id),\n")
                            .append("\tPRIMARY KEY (").append(tableName).append("_id, ").append(targetTable).append("_id)\n")
                            .append(");\n");
                }
                continue;
            }

            // COLUMN GENERATION
            String colName = getColumnName(field);
            String colType = getSqlType(field, entityClass);
            String constraints = getConstraints(field, colName);

            // Handle Relations (1:1, N:1) - They become Foreign Keys
            if (field.isAnnotationPresent(DbOneToOne.class) || field.isAnnotationPresent(DbManyToOne.class)) {
                Class<?> targetEntity = field.getType();
                String targetTable = getTableName(targetEntity);
                colName = field.getName() + "_id";
                colType = "BIGINT"; // FK is usually ID
                constraints += " REFERENCES " + targetTable + "(id)";

                if (field.isAnnotationPresent(DbOneToOne.class)) {
                    constraints += " UNIQUE";
                }
            }

            columnDefs.add("\t" + colName + " " + colType + constraints);
        }

        sql.append(String.join(",\n", columnDefs));
        sql.append("\n);");

        // Append any generated Join Tables (M:N)
        sql.append(joinTablesSql);

        return sql.toString();
    }

    // --- Helpers ---

    private static String resolvePrimaryKeyType(Class<?> entityClass) {
        Class<?> current = entityClass;

        // 1. Walk up the parent classes until we find Entity<ID>
        while (current != null && current != Object.class) {
            Type superType = current.getGenericSuperclass();

            // Check if this parent is "Entity<Something>"
            if (superType instanceof ParameterizedType paramType) {
                // Check if the raw class is your base Entity class
                // Note: Use the actual class name "Entity" or compare class objects
                if (paramType.getRawType().getTypeName().endsWith("Entity")) {

                    // Get the <ID> argument (the first one: [0])
                    Type idType = paramType.getActualTypeArguments()[0];

                    // Map the Java Type to SQL
                    if (idType == Long.class) return "BIGSERIAL PRIMARY KEY";
                    if (idType == Integer.class) return "SERIAL PRIMARY KEY";
                    if (idType.getTypeName().equals("java.util.UUID")) return "UUID PRIMARY KEY";
                    if (idType == String.class) return "VARCHAR(255) PRIMARY KEY";
                }
            }
            current = current.getSuperclass();
        }

        // Fallback if we can't detect it
        return "BIGSERIAL PRIMARY KEY";
    }

    private static String getTableName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(DbTable.class)) {
            String name = clazz.getAnnotation(DbTable.class).name();
            if (!name.isEmpty()) return name;
        }
        return clazz.getSimpleName().toLowerCase();
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(DbColumn.class)) {
            String name = field.getAnnotation(DbColumn.class).name();
            if (!name.isEmpty()) return name;
        }
        return field.getName(); // Strategy: camelCase becomes database column name
    }

    private static Class<?> getListGenericType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType pt) {
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }

    // Recursively get fields from superclasses
    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    // Map Java types to PostgreSQL Types
    private static String getSqlType(Field field,Class<?> entityClass) {
        Class<?> type = field.getType();

        // PK Logic
        if (field.isAnnotationPresent(DbId.class) || field.getName().equalsIgnoreCase("id")) {
            return resolvePrimaryKeyType(entityClass);
        }

        if (type == int.class || type == Integer.class) return "INT";
        if (type == long.class || type == Long.class) return "BIGINT";
        if (type == double.class || type == Double.class) return "DOUBLE PRECISION";
        if (type == boolean.class || type == Boolean.class) return "BOOLEAN";
        if (type == String.class) return "VARCHAR(255)";
        if (type == LocalDate.class) return "DATE";
        if (type == LocalDateTime.class) return "TIMESTAMP";
        if (type == BigDecimal.class) return "DECIMAL(19, 2)";
        if (type == UUID.class) return "UUID";
        if (type == byte[].class) return "BYTEA";
        if (type.isEnum()) return "VARCHAR(50)";

        return "VARCHAR(255)"; // Fallback
    }

    private static String getConstraints(Field field, String colName) {
        StringBuilder sb = new StringBuilder();
        Class<?> type = field.getType();
        boolean isPrimitive = type.isPrimitive();

        // 1. Nullability
        boolean isNullable = true;
        if (isPrimitive) isNullable = false; // Primitives cannot be null
        if (field.isAnnotationPresent(DbColumn.class)) {
            isNullable = field.getAnnotation(DbColumn.class).nullable();
        }
        // PKs are never null, but SERIAL handles that.
        if (!isNullable && !colName.equalsIgnoreCase("id")) {
            sb.append(" NOT NULL");
        }

        // 2. Uniqueness (Explicit OR Heuristic)
        boolean isUnique = false;
        if (field.isAnnotationPresent(DbColumn.class) && field.getAnnotation(DbColumn.class).unique()) {
            isUnique = true;
        }
        // Heuristics
        String lowerName = colName.toLowerCase();
        if (lowerName.contains("email") || lowerName.contains("isbn") ||
                lowerName.contains("cnp") || lowerName.equals("username")) {
            isUnique = true;
        }

        if (isUnique) sb.append(" UNIQUE");

        return sb.toString();
    }
}