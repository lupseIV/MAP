package org.example.paginarefiltraredb.domain.dtos;

import org.example.paginarefiltraredb.repository.paging.util.paging.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DtoUtils {

    /**
     * Generic method that transforms a Page of Entities into a List of DTOs.
     * * @param page   The page object containing entities (e.g., Client).
     *
     * @param mapper The mapping logic (usually a DTO constructor reference).
     * @param <E>    The Entity type (Input).
     * @param <D>    The DTO type (Output).
     * @return A List of DTOs ready for the GUI.
     */
    public static <E, D> List<D> fromPage(Page<E> page, Function<E, D> mapper) {
        return StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .map(mapper) // Applies the transformation
                .collect(Collectors.toList());
    }

    public static <E, D> List<D> fromIterable(Iterable<E> elem, Function<E,D> mapper){
        return StreamSupport.stream(elem.spliterator(), false)
                .map(mapper)
                .collect(Collectors.toList());
    }
}