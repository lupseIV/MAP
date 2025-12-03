package org.domain.dtos.filters;

import org.domain.dtos.guiDTOS.PersonGuiDTO;
import org.repository.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PersonGUIFilter implements SqlFilter {
    @Override
    public Pair<String, List<Object>> toSql() {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String sql = String.join(" and ", conditions);
        return new Pair<>(sql, params);
    }
}
