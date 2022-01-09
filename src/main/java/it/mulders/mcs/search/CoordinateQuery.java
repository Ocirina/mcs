package it.mulders.mcs.search;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.DEFAULT_MAX_SEARCH_RESULTS;

public record CoordinateQuery (
        String groupId,
        String artifactId,
        String version,
        Integer searchLimit
) implements SearchQuery {
    public CoordinateQuery(final String groupId, final String artifactId) {
        this(groupId, artifactId, null, DEFAULT_MAX_SEARCH_RESULTS);
    }

    public CoordinateQuery(final String groupId, final String artifactId, final String version) {
        this(groupId, artifactId, version, DEFAULT_MAX_SEARCH_RESULTS);
    }

    public SearchQuery withLimit(final Integer limit) {
        if (limit != null) {
            return new CoordinateQuery(groupId, artifactId, version, limit);
        } else {
            return this;
        }
    }

    @Override
    public String toSolrQuery() {
        String query;
        if (version == null) {
            query = String.format("g:%s AND a:%s", groupId, artifactId);
        } else {
            query = String.format("g:%s AND a:%s AND v:%s", groupId, artifactId, version);
        }

        return String.format("q=%s&core=gav&start=%d&rows=%d",
                URLEncoder.encode(query, StandardCharsets.UTF_8), 0, searchLimit());
    }
}
