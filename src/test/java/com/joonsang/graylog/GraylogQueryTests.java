package com.joonsang.graylog;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraylogQueryTests {

    @Test
    public void TC_001_INIT() {
        GraylogQuery query = GraylogQuery.builder();

        String expect = "";

        assertThat(query.build())
            .as("TC_001_INIT")
            .isEqualTo(expect);
    }

    @Test
    public void TC_002_TERM() {
        GraylogQuery query = GraylogQuery.builder()
            .term("ssh");

        String expect = "\"ssh\"";

        assertThat(query.build())
            .as("TC_002_TERM")
            .isEqualTo(expect);
    }

    @Test
    public void TC_003_FUZZ_TERM() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzTerm("ssh");

        String expect = "\"ssh\"~";

        assertThat(query.build())
            .as("TC_003_FUZZ_TERM")
            .isEqualTo(expect);
    }

    @Test
    public void TC_004_FUZZ_TERM_WITH_DISTANCE() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzTerm("ssh", 3);

        String expect = "\"ssh\"~3";

        assertThat(query.build())
            .as("TC_004_FUZZ_TERM_WITH_DISTANCE")
            .isEqualTo(expect);
    }

    @Test
    public void TC_005_EXISTS() {
        GraylogQuery query = GraylogQuery.builder()
            .exists("type");

        String expect = "_exists_:type";

        assertThat(query.build())
            .as("TC_005_EXISTS")
            .isEqualTo(expect);
    }

    @Test
    public void TC_006_FIELD() {
        GraylogQuery query = GraylogQuery.builder()
            .field("type", "ssh");

        String expect = "type:\"ssh\"";

        assertThat(query.build())
            .as("TC_006_FIELD")
            .isEqualTo(expect);
    }

    @Test
    public void TC_007_NUMERIC_FIELD() {
        GraylogQuery query = GraylogQuery.builder()
            .field("http_response_code", 500);

        String expect = "http_response_code:500";

        assertThat(query.build())
            .as("TC_007_NUMERIC_FIELD")
            .isEqualTo(expect);
    }

    @Test
    public void TC_008_RANGE_FIELD() {
        GraylogQuery query = GraylogQuery.builder()
            .field("http_response_code", ">", 500);

        String expect = "http_response_code:>500";

        assertThat(query.build())
            .as("TC_008_RANGE_FIELD")
            .isEqualTo(expect);
    }

    @Test
    public void TC_009_FUZZ_FIELD() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzField("type", "ssh");

        String expect = "type:\"ssh\"~";

        assertThat(query.build())
            .as("TC_009_FUZZ_FIELD")
            .isEqualTo(expect);
    }

    @Test
    public void TC_010_FUZZ_FIELD_WITH_DISTANCE() {
        GraylogQuery query = GraylogQuery.builder()
            .fuzzField("type", "ssh", 3);

        String expect = "type:\"ssh\"~3";

        assertThat(query.build())
            .as("TC_010_FUZZ_FIELD_WITH_DISTANCE")
            .isEqualTo(expect);
    }

    @Test
    public void TC_011_RANGE() {
        GraylogQuery query = GraylogQuery.builder()
            .range("http_response_code", "[", 500, 504, "}");

        String expect = "http_response_code:[500 TO 504}";

        assertThat(query.build())
                .as("TC_011_RANGE")
                .isEqualTo(expect);
    }

    @Test
    public void TC_012_DATE_RANGE() {
        GraylogQuery query = GraylogQuery.builder()
            .range("timestamp", "{", "2019-07-23 09:53:08.175", "2019-07-23 09:53:08.575", "]");

        String expect = "timestamp:{\"2019-07-23 09:53:08.175\" TO \"2019-07-23 09:53:08.575\"]";

        assertThat(query.build())
            .as("TC_012_DATE_RANGE")
            .isEqualTo(expect);
    }

    @Test
    public void TC_013_RAW() {
        GraylogQuery query = GraylogQuery.builder()
            .raw("/ethernet[0-9]+/");

        String expect = "/ethernet[0-9]+/";

        assertThat(query.build())
            .as("TC_013_RAW")
            .isEqualTo(expect);
    }

    @Test
    public void TC_014_NOT() {
        GraylogQuery query = GraylogQuery.builder()
            .not().exists("type");

        String expect = "NOT _exists_:type";

        assertThat(query.build())
            .as("TC_014_NOT")
            .isEqualTo(expect);
    }

    @Test
    public void TC_015_AND() {
        GraylogQuery query = GraylogQuery.builder()
            .term("cat")
            .and()
            .term("dog");

        String expect = "\"cat\" AND \"dog\"";

        assertThat(query.build())
            .as("TC_015_AND")
            .isEqualTo(expect);
    }

    @Test
    public void TC_016_OR() {
        GraylogQuery query = GraylogQuery.builder()
            .term("cat")
            .or()
            .term("dog");

        String expect = "\"cat\" OR \"dog\"";

        assertThat(query.build())
            .as("TC_016_OR")
            .isEqualTo(expect);
    }

    @Test
    public void TC_017_PARENTHESES() {
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

        assertThat(query.build())
            .as("TC_017_PARENTHESES")
            .isEqualTo(expect);
    }

    @Test
    public void TC_018_PREPEND() {
        GraylogQuery prepend = GraylogQuery.builder()
            .not().exists("type");

        GraylogQuery query = GraylogQuery.builder(prepend)
            .and().term("ssh");

        String expect = "NOT _exists_:type AND \"ssh\"";

        assertThat(query.build())
            .as("TC_018_PREPEND")
            .isEqualTo(expect);
    }

    @Test
    public void TC_019_APPEND() {
        GraylogQuery append = GraylogQuery.builder()
            .or().exists("type");

        GraylogQuery query = GraylogQuery.builder()
            .term("ssh")
            .append(append);

        String expect = "\"ssh\" OR _exists_:type";

        assertThat(query.build())
            .as("TC_019_APPEND")
            .isEqualTo(expect);
    }

    @Test
    public void TC_020_ESCAPING() {
        GraylogQuery query = GraylogQuery.builder()
            .field("content_type", "application/json")
            .and()
            .field("response_body", "{\"nickname\": \"[*test] John Doe\", \"message\": \"hello?\"}");

        String expect = "content_type:\"application\\/json\" AND response_body:\"\\{\\\"nickname\\\"\\\\: \\\"\\[\\*test\\] John Doe\\\", \\\"message\\\"\\\\: \\\"hello\\?\\\"\\}\"";

        assertThat(query.build())
            .as("TC_020_SANITIZE")
            .isEqualTo(expect);
    }
}
