package org.store.api.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "factures") // Root element for XML
public class FacturesWrapper {
    private List<Facture> factures;

    @XmlElement(name = "facture") // Each item in the list is a <category> element
    public List<Facture> getFactures() { return factures; }
    public void setFactures(List<Facture> factures) { this.factures = factures; }
}