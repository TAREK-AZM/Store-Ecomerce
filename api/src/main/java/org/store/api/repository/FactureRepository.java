package org.store.api.repository;

import org.store.api.entity.Facture;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FactureRepository {
    private List<Facture> factures = new ArrayList<>();

    public List<Facture> findAll() {
        return new ArrayList<>(factures);
    }

    public Optional<Facture> findById(Long id) {
        return factures.stream()
                .filter(facture -> facture.getId().equals(id))
                .findFirst();
    }

    public void save(Facture facture) {
        factures.removeIf(f -> f.getId().equals(facture.getId()));
        factures.add(facture);
    }

    public void deleteById(Long id) {
        factures.removeIf(facture -> facture.getId().equals(id));
    }

    public List<Facture> findByUserId(Long userId) {
        return factures.stream()
                .filter(facture -> facture.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}