package org.example.paginarefiltraredb.repository.database;

public interface DatabaseCRUD<ID, E> {
    void saveToDatabase(E entity) ;
    void deleteFromDatabase(ID id) ;
    void updateFromDatabase(E entity) ;
}