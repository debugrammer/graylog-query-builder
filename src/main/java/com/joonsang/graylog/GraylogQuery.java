package com.joonsang.graylog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Graylog Query Builder
 *
 * <p>See the Graylog documentation article on <a href=
 * "https://docs.graylog.org/en/latest/pages/queries.html">Graylog Query</a>.
 * </p>
 *
 * @author debugrammer
 * @since 1.0.0
 */
public class GraylogQuery {

    private static final String EXISTS = "_exists_";

    private static final String COLON = ":";

    private static final String TO = "TO";

    private static final String NOT = "NOT";

    private static final String AND = "AND";

    private static final String OR = "OR";

    private static final String OPEN_PARENTHESIS = "(";

    private static final String CLOSE_PARENTHESIS = ")";

    private static final String TILDE = "~";

    private List<String> queries;

    private GraylogQuery() {
        this.queries = new ArrayList<>();
    }

    private GraylogQuery(List<String> queries) {
        this.queries = new ArrayList<>(queries);
    }

    public static GraylogQuery builder() {
        return new GraylogQuery();
    }

    public static GraylogQuery builder(GraylogQuery query) {
        return new GraylogQuery(query.queries);
    }

    public GraylogQuery append(GraylogQuery query) {
        queries.addAll(query.queries);

        return this;
    }

    /**
     * Messages that include the term or phrase.
     * @param value term or phrase
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery term(String value) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(sanitize(value));

        return this;
    }

    /**
     * Fuzziness with default distance.
     * Messages that include similar term or phrase.
     * @param value term or phrase
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery fuzzTerm(String value) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(sanitize(value) + TILDE);

        return this;
    }

    /**
     * Fuzziness with custom distance.
     * Messages that include similar term or phrase.
     * @param value term or phrase
     * @param distance Damerau-Levenshtein distance
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery fuzzTerm(String value, int distance) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(sanitize(value) + TILDE + distance);

        return this;
    }

    /**
     * Messages that have the field.
     * @param field field name
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery exists(String field) {
        queries.add(EXISTS + COLON + field);

        return this;
    }

    /**
     * Messages where the field includes the term or phrase.
     * @param field field name
     * @param value term or phrase
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery field(String field, String value) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(field + COLON + sanitize(value));

        return this;
    }

    /**
     * Messages where the field includes the number.
     * @param field field name
     * @param value number
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery field(String field, int value) {
        queries.add(field + COLON + value);

        return this;
    }

    /**
     * One side unbounded range query.
     * Messages where the field satisfies the condition.
     * @param field field name
     * @param operator range operator
     * @param value number
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery field(String field, String operator, int value) {
        queries.add(field + COLON + operator + value);

        return this;
    }

    /**
     * Fuzziness with default distance.
     * Messages where the field includes similar term or phrase.
     * @param field field name
     * @param value number
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery fuzzField(String field, String value) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(field + COLON + sanitize(value) + TILDE);

        return this;
    }

    /**
     * Fuzziness with custom distance.
     * Messages where the field includes similar term or phrase.
     * @param field field name
     * @param value number
     * @param distance Damerau-Levenshtein distance
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery fuzzField(String field, String value, int distance) {
        if (StringUtils.isEmpty(value)) {
            removeEndingConj();

            return this;
        }

        queries.add(field + COLON + sanitize(value) + TILDE + distance);

        return this;
    }

    /**
     * Range query.
     * Ranges in square brackets are inclusive, curly brackets are exclusive and can even be combined.
     * @param field field name
     * @param fromBracket from bracket
     * @param from from number
     * @param to to number
     * @param toBracket to bracket
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery range(String field, String fromBracket, int from, int to, String toBracket) {
        queries.add(field + COLON + fromBracket + from + StringUtils.SPACE + TO + StringUtils.SPACE + to + toBracket);

        return this;
    }

    /**
     * Date range query.
     * The dates needs to be UTC.
     * @param field field name
     * @param fromBracket from bracket
     * @param from from date
     * @param to to date
     * @param toBracket to bracket
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery range(String field, String fromBracket, String from, String to, String toBracket) {
        from = "\"" + from + "\"";
        to = "\"" + to + "\"";

        queries.add(field + COLON + fromBracket + from + StringUtils.SPACE + TO + StringUtils.SPACE + to + toBracket);

        return this;
    }

    /**
     * Raw query.
     * @param raw raw Graylog query
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery raw(String raw) {
        queries.add(raw);

        return this;
    }

    /**
     * NOT expression.
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery not() {
        queries.add(NOT);

        return this;
    }

    /**
     * AND expression.
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery and() {
        queries.add(AND);

        return this;
    }

    /**
     * OR expression.
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery or() {
        queries.add(OR);

        return this;
    }

    /**
     * Open parenthesis.
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery openParen() {
        queries.add(OPEN_PARENTHESIS);

        return this;
    }

    /**
     * Close parenthesis.
     * @return GraylogQuery - used to chain calls
     * @since 1.0.0
     */
    public GraylogQuery closeParen() {
        queries.add(CLOSE_PARENTHESIS);

        return this;
    }

    /**
     * Completed Graylog query.
     * @return completed Graylog query
     * @since 1.0.0
     */
    public String build() {
        removeStartingConj();

        return StringUtils.join(queries, StringUtils.SPACE);
    }

    /**
     * Remove the conjunction at the end.
     * @since 1.0.0
     */
    private void removeEndingConj() {
        if (queries.isEmpty()) {
            return;
        }

        List<String> conjunctions = List.of(AND, OR, NOT);
        int lastIndex = queries.size() - 1;
        String lastQuery = queries.get(lastIndex);

        if (conjunctions.contains(lastQuery)) {
            queries.remove(lastIndex);
        }
    }

    /**
     * Remove the starting conjunction.
     * @since 1.0.0
     */
    private void removeStartingConj() {
        if (queries.isEmpty()) {
            return;
        }

        List<String> conjunctions = List.of(AND, OR);
        int firstIndex = 0;
        String firstQuery = queries.get(firstIndex);

        if (conjunctions.contains(firstQuery)) {
            queries.remove(firstIndex);
        }

        if (queries.size() == 1 && firstQuery.contains(NOT)) {
            queries.remove(firstIndex);
        }
    }

    /**
     * Sanitize string value.
     * @since 1.0.0
     */
    private String sanitize(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        return "\"" + escape(value) + "\"";
    }

    /**
     * Escape input text as specified on Graylog docs.
     * @since 1.0.0
     */
    private String escape(String input) {
        final String[] metaCharacters = {
            "\\", "&", "|", ":", "/", "+", "-", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?"
        };

        for (String meta: metaCharacters) {
            input = input.replace(meta, "\\" + meta);
        }

        return input;
    }
}
