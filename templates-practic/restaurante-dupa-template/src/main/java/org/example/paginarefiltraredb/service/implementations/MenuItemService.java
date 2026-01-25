package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.MenuItemDbRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.*;

public class MenuItemService extends BaseService<Integer, MenuItem> {

    public MenuItemService(MenuItemDbRepository repository, Validator<MenuItem> validator) {
        super(repository, validator);
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        findAll().forEach(items::add);
        return items;
    }

    public Map<String, List<MenuItem>> getMenuGroupedByCategory() {
        Map<String, List<MenuItem>> groupedMenu = new LinkedHashMap<>();

        for (MenuItem item : findAll()) {
            String category = item.getCategory();
            groupedMenu.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
        }

        return groupedMenu;
    }
}
