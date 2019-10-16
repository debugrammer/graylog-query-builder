package com.debugrammer.graylog;

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
     * Messages that include the term or phrase
     *
     * <pre>
     *   term("ssh") = "ssh"
     * </pre>
     *
     * @param value term or phrase
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
     * Fuzziness with default distance
     * Messages that include similar term or phrase
     *
     * <pre>
     *   fuzzTerm("ssh logni") = "ssh logni"~
     * </pre>
     *
     * @param value term or phrase
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
     * Fuzziness with custom distance
     * Messages that include similar term or phrase
     *
     * <pre>
     *   fuzzTerm("ssh logni", 1) = "ssh logni"~1
     * </pre>
     *
     * @param value term or phrase
     * @param distance Damerau-Levenshtein distance
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
     * Messages that have the field
     *
     * <pre>
     *   exists("type") = _exists_:type
     * </pre>
     *
     * @param field field name
     * @since 1.0.0
     */
    public GraylogQuery exists(String field) {
        queries.add(EXISTS + COLON + field);

        return this;
    }

    /**
     * Messages where the field includes the term or phrase
     *
     * <pre>
     *   field("type", "ssh") = type:"ssh"
     * </pre>
     *
     * @param field field name
     * @param value term or phrase
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
     * Messages where the field includes the number
     *
     * <pre>
     *   field("http_response_code", 500) = http_response_code:500
     * </pre>
     *
     * @param field field name
     * @param value number
     * @since 1.0.0
     */
    public GraylogQuery field(String field, int value) {
        queries.add(field + COLON + value);

        return this;
    }

    /**
     * One side unbounded range query
     * Messages where the field satisfies the condition
     *
     * <pre>
     *   field("http_response_code", ">", 500) = http_response_code:>500
     * </pre>
     *
     * @param field field name
     * @param operator range operator
     * @param value number
     * @since 1.0.0
     */
    public GraylogQuery field(String field, String operator, int value) {
        queries.add(field + COLON + operator + value);

        return this;
    }

    /**
     * Fuzziness with default distance
     * Messages where the field includes similar term or phrase
     *
     * <pre>
     *   fuzzField("source", "example.org") = source:"example.org"~
     * </pre>
     *
     * @param field field name
     * @param value number
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
     * Fuzziness with custom distance
     * Messages where the field includes similar term or phrase
     *
     * <pre>
     *   fuzzField("source", "example.org", 1) = source:"example.org"~1
     * </pre>
     *
     * @param field field name
     * @param value number
     * @param distance Damerau-Levenshtein distance
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
     * Range query
     * Ranges in square brackets are inclusive, curly brackets are exclusive and can even be combined
     *
     * <pre>
     *   range("http_response_code", "[", 500, 504, "]") = http_response_code:[500 TO 504]
     *   range("bytes", "{", 0, 64, "]") = bytes:{0 TO 64]
     * </pre>
     *
     * @param field field name
     * @param fromBracket from bracket
     * @param from from number
     * @param to to number
     * @param toBracket to bracket
     */
    public GraylogQuery range(String field, String fromBracket, int from, int to, String toBracket) {
        queries.add(field + COLON + fromBracket + from + StringUtils.SPACE + TO + StringUtils.SPACE + to + toBracket);

        return this;
    }

    /**
     * Date range query
     * The dates needs to be UTC
     *
     * <pre>
     *   range("timestamp", "[", "2019-07-23 09:53:08.175", "2019-07-23 09:53:08.575", "]")
     *     = timestamp:["2019-07-23 09:53:08.175" TO "2019-07-23 09:53:08.575"]
     * </pre>
     *
     * @param field field name
     * @param fromBracket from bracket
     * @param from from date
     * @param to to date
     * @param toBracket to bracket
     * @since 1.0.0
     */
    public GraylogQuery range(String field, String fromBracket, String from, String to, String toBracket) {
        from = "\"" + from + "\"";
        to = "\"" + to + "\"";

        queries.add(field + COLON + fromBracket + from + StringUtils.SPACE + TO + StringUtils.SPACE + to + toBracket);

        return this;
    }

    /**
     * Raw query
     *
     * <pre>
     *   raw("/ethernet[0-9]+/") = /ethernet[0-9]+/
     *   raw("type:(ssh OR login)") = type:(ssh OR login)
     * </pre>
     *
     * @param raw raw Graylog query
     * @since 1.0.0
     */
    public GraylogQuery raw(String raw) {
        queries.add(raw);

        return this;
    }

    /**
     * NOT expression
     * @since 1.0.0
     */
    public GraylogQuery not() {
        queries.add(NOT);

        return this;
    }

    /**
     * AND expression
     * @since 1.0.0
     */
    public GraylogQuery and() {
        queries.add(AND);

        return this;
    }

    /**
     * OR expression
     * @since 1.0.0
     */
    public GraylogQuery or() {
        queries.add(OR);

        return this;
    }

    /**
     * Open parenthesis
     * @since 1.0.0
     */
    public GraylogQuery openParen() {
        queries.add(OPEN_PARENTHESIS);

        return this;
    }

    /**
     * Close parenthesis
     * @since 1.0.0
     */
    public GraylogQuery closeParen() {
        queries.add(CLOSE_PARENTHESIS);

        return this;
    }

    /**
     * Completed Graylog query
     * @since 1.0.0
     */
    public String build() {
        removeStartingConj();

        return StringUtils.join(queries, StringUtils.SPACE);
    }

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

    private String sanitize(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        return "\"" + escape(value) + "\"";
    }

    private String escape(String input) {
        final String[] metaCharacters = {
            "&", "|", ":", "\\", "/", "+", "-", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?"
        };

        for (String meta: metaCharacters) {
            if (input.contains(meta)) {
                input = input.replace(meta, "\\" + meta);
            }
        }

        return input;
    }
}
