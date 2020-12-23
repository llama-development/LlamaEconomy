package net.lldv.llamaeconomy.components.universalclient.data;

import java.util.Set;

/**
 * @author LlamaDevelopment
 * @project UniversalClient
 * @website http://llamadevelopment.net/
 */
public class UDocumentSet {

    private final Set<UDocument> results;

    public UDocumentSet(Set<UDocument> results) {
        this.results = results;
    }

    public Set<UDocument> getAll() {
        return results;
    }

    public UDocument first() {
        return results.iterator().hasNext() ? results.iterator().next() : null;
    }

}
