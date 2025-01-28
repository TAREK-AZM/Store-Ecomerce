package org.store.api.repository;

import org.store.api.entity.Facture;
import org.store.api.entity.FacturesWrapper;
import org.store.api.util.XmlUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FactureRepository {
    private static final String FACTURES_FILE = "src/main/resources/data/factures.xml";
    private static final String FACTURES_XSD = "src/main/resources/data/factures.xsd";

    // Read all factures from the XML file
    public List<Facture> findAll() throws Exception {
        FacturesWrapper wrapper = XmlUtil.readXml(FACTURES_FILE, FacturesWrapper.class, FACTURES_XSD);
        return wrapper != null ? wrapper.getFactures() : new ArrayList<>();
    }

    // Find a facture by ID
    public Optional<Facture> findById(Long id) throws Exception {
        return findAll().stream()
                .filter(facture -> facture.getId().equals(id))
                .findFirst();
    }

    public void save(Facture facture) throws Exception {
        List<Facture> factures = findAll();

        // If factures is null, initialize it as an empty list
        if (factures == null) {
            factures = new ArrayList<>();
        }
        // Check if the list is empty (no existing factures in the list)
        if (factures.isEmpty()) {
            // If empty, just add the new facture
            factures.add(facture);
        } else {
            // Otherwise, remove any existing facture with the same ID and add the new one
            factures.removeIf(f -> f.getId().equals(facture.getId())); // Remove existing facture if it exists
            factures.add(facture); // Add the new/updated facture
        }

        // Wrap the factures list into a FacturesWrapper and write it to the XML
        FacturesWrapper wrapper = new FacturesWrapper();
        wrapper.setFactures(factures);
        XmlUtil.writeXml(FACTURES_FILE, wrapper, FACTURES_XSD);
    }


    // Delete a facture by ID
    public void deleteById(Long id) throws Exception {
        List<Facture> factures = findAll();
        factures.removeIf(facture -> facture.getId().equals(id));
        FacturesWrapper wrapper = new FacturesWrapper();
        wrapper.setFactures(factures);
        XmlUtil.writeXml(FACTURES_FILE, wrapper, FACTURES_XSD);
    }

    // Find factures by user ID
    public List<Facture> findFacturesByUserId(Long userId) throws Exception {
        return findAll().stream()
                .filter(facture -> facture.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}