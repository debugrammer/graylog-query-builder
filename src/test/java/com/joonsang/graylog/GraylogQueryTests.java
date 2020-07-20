package com.joonsang.graylog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GraylogQueryTests {

    @Test
    void init() {
        GraylogQuery query = GraylogQuery.builder();

        String expect = "";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void term() {
        GraylogQuery query = GraylogQuery.builder()
            .term("ssh");

        String expect = "\"ssh\"";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void fuzzTerm() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzTerm("ssh");

        String expect = "\"ssh\"~";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void fuzzTermWithDistance() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzTerm("ssh", 3);

        String expect = "\"ssh\"~3";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void exists() {
        GraylogQuery query = GraylogQuery.builder()
            .exists("type");

        String expect = "_exists_:type";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void field() {
        GraylogQuery query = GraylogQuery.builder()
            .field("type", "ssh");

        String expect = "type:\"ssh\"";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void numericField() {
        GraylogQuery query = GraylogQuery.builder()
            .field("http_response_code", 500);

        String expect = "http_response_code:500";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void rangeField() {
        GraylogQuery query = GraylogQuery.builder()
            .field("http_response_code", ">", 500);

        String expect = "http_response_code:>500";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void fuzzField() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzField("type", "ssh");

        String expect = "type:\"ssh\"~";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void fuzzFieldWithDistance() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzField("type", "ssh", 3);

        String expect = "type:\"ssh\"~3";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void range() {
        GraylogQuery query = GraylogQuery.builder()
            .range("http_response_code", "[", 500, 504, "}");

        String expect = "http_response_code:[500 TO 504}";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void dateRange() {
        GraylogQuery query = GraylogQuery.builder()
            .range("timestamp", "{", "2019-07-23 09:53:08.175", "2019-07-23 09:53:08.575", "]");

        String expect = "timestamp:{\"2019-07-23 09:53:08.175\" TO \"2019-07-23 09:53:08.575\"]";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void raw() {
        GraylogQuery query = GraylogQuery.builder()
            .raw("/ethernet[0-9]+/");

        String expect = "/ethernet[0-9]+/";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void not() {
        GraylogQuery query = GraylogQuery.builder()
            .not().exists("type");

        String expect = "NOT _exists_:type";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void and() {
        GraylogQuery query = GraylogQuery.builder()
            .term("cat")
            .and()
            .term("dog");

        String expect = "\"cat\" AND \"dog\"";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void or() {
        GraylogQuery query = GraylogQuery.builder()
            .term("cat")
            .or()
            .term("dog");

        String expect = "\"cat\" OR \"dog\"";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void parentheses() {
        GraylogQuery query = GraylogQuery.builder()
            .openParen()
                .term("ssh login")
                .and()
                .openParen()
                    .field("source", "example.org")
                    .or()
                    .field("source", "another.example.org")
                .closeParen()
            .closeParen()
            .or()
            .exists("always_find_me");

        String expect = "( \"ssh login\" AND ( source:\"example.org\" OR source:\"another.example.org\" ) ) OR _exists_:always_find_me";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void prepend() {
        GraylogQuery prepend = GraylogQuery.builder()
            .not().exists("type");

        GraylogQuery query = GraylogQuery.builder(prepend)
            .and().term("ssh");

        String expect = "NOT _exists_:type AND \"ssh\"";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void append() {
        GraylogQuery append = GraylogQuery.builder()
            .or().exists("type");

        GraylogQuery query = GraylogQuery.builder()
            .term("ssh")
            .append(append);

        String expect = "\"ssh\" OR _exists_:type";

        assertThat(query.build()).isEqualTo(expect);
    }

    @Test
    void escaping() {
        GraylogQuery query = GraylogQuery.builder()
            .field("content_type", "application/json")
            .and()
            .field("response_body", "{\"nickname\": \"[*test] John Doe\", \"message\": \"hello?\"}");

        String expect = "content_type:\"application\\/json\" AND response_body:\"\\{\\\"nickname\\\"\\: \\\"\\[\\*test\\] John Doe\\\", \\\"message\\\"\\: \\\"hello\\?\\\"\\}\"";

        assertThat(query.build()).isEqualTo(expect);
    }
}
