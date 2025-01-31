package org.store.api.util;

import java.util.List;
import java.util.function.Function;

public class IdGenerator {

    /**
     * Generates a unique ID for any entity based on the highest existing ID.
     * @param items The list of existing entities.
     * @param idExtractor A method reference to extract the ID from an entity.
     * @param <T> The entity type.
     * @return A unique ID.
     */
    public static <T> Long generateUniqueId(List<T> items, Function<T, Long> idExtractor) {
        if (items.isEmpty()) {
            return 1L; // Start with ID 1 if no entities exist
        }

        return items.stream()
                .mapToLong(idExtractor::apply)
                .max()
                .orElse(0L) + 1;
    }
}
