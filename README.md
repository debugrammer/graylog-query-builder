# Graylog Query Builder
> [Graylog Search Query](https://docs.graylog.org/en/latest/pages/queries.html) Builder especially useful for working with [Graylog REST API](https://docs.graylog.org/en/latest/pages/configuration/rest_api.html).

[![Build Status](https://travis-ci.org/debugrammer/graylog-query-builder.svg?branch=master)](https://travis-ci.org/debugrammer/graylog-query-builder)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.joonsang.graylog/graylog-query-builder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.joonsang.graylog/graylog-query-builder)
[![Javadoc](https://javadoc-badge.appspot.com/com.joonsang.graylog/graylog-query-builder.svg?label=javadoc)](https://javadoc-badge.appspot.com/com.joonsang.graylog/graylog-query-builder)

## Getting Started
Graylog Query Builder is available at the Central Maven Repository.

**Maven**
```
<dependency>
  <groupId>com.joonsang.graylog</groupId>
  <artifactId>graylog-query-builder</artifactId>
  <version>1.0.2</version>
</dependency>
```

**Gradle**
```
implementation group: 'com.joonsang.graylog', name: 'graylog-query-builder', version: '1.0.2'
```

## Usage
```
GraylogQuery.builder()
    .field("type", "ssh")
    .and().exists("id")
    .and().openParen()
        .raw("source:(dog.org OR cat.org)")
    .closeParen()
    .and().range("http_response_code", "[", 200, 300, "]")
    .build();
```
Above code snippet generates the string below.
```
type:"ssh" AND _exists_:id AND ( source:(dog.org OR cat.org) ) AND http_response_code:[200 TO 300]
```

## Building Queries

### 1. Statements

#### 1.1. Term
Messages that include the term or phrase.

**Usage:**
```
GraylogQuery.builder()
    .term("ssh")
    .build();
```
**Output:**
```
"ssh"
```

#### 1.2. Fuzz Term
Messages that include similar term or phrase.

##### 1.2.1. Fuzziness with default distance
**Usage:**
```
GraylogQuery.builder()
    .fuzzTerm("ssh logni")
    .build();
```
**Output:**
```
"ssh logni"~
```

##### 1.2.2. Fuzziness with custom distance
**Usage:**
```
GraylogQuery.builder()
    .fuzzTerm("ssh logni", 1)
    .build();
```
**Output:**
```
"ssh logni"~1
```

#### 1.3. Exists
Messages that have the field.

**Usage:**
```
GraylogQuery.builder()
    .exists("type")
    .build();
```
**Output:**
```
_exists_:type
```

#### 1.4. Field

##### 1.4.1. Field (String)
Messages where the field includes the term or phrase.

**Usage:**
```
GraylogQuery.builder()
    .field("type", "ssh")
    .build();
```
**Output:**
```
type:"ssh"
```

##### 1.4.2. Field (Numeric)
Messages where the field includes the number.

**Usage:**
```
GraylogQuery.builder()
    .field("http_response_code", 500)
    .build();
```
**Output:**
```
http_response_code:500
```

##### 1.4.3. One side unbounded range query
Messages where the field satisfies the condition.

**Usage:**
```
GraylogQuery.builder()
    .field("http_response_code", ">", 500)
    .build();
```
**Output:**
```
http_response_code:>500
```

#### 1.5. Fuzz Field
Messages where the field includes similar term or phrase.

##### 1.5.1. Fuzziness with default distance
**Usage:**
```
GraylogQuery.builder()
    .fuzzField("source", "example.org")
    .build();
```
**Output:**
```
source:"example.org"~
```

##### 1.5.2. Fuzziness with custom distance
**Usage:**
```
GraylogQuery.builder()
    .fuzzField("source", "example.org", 1)
    .build();
```
**Output:**
```
source:"example.org"~1
```

#### 1.6. Range

##### 1.6.1. Range query
Ranges in square brackets are inclusive, curly brackets are exclusive and can even be combined.

**Usage:**
```
GraylogQuery.builder()
    .range("bytes", "{", 0, 64, "]")
    .build();
```
**Output:**
```
bytes:{0 TO 64]
```

##### 1.6.2. Date range query
The dates needs to be UTC

**Usage:**
```
GraylogQuery.builder()
    .range("timestamp", "[", "2019-07-23 09:53:08.175", "2019-07-23 09:53:08.575", "]")
    .build();
```
**Output:**
```
timestamp:["2019-07-23 09:53:08.175" TO "2019-07-23 09:53:08.575"]
```

#### 1.6. Raw
Raw query.

**Usage:**
```
GraylogQuery.builder()
    .raw("/ethernet[0-9]+/")
    .build();
```
**Output:**
```
/ethernet[0-9]+/
```

### 2. Conjunctions

#### 2.1. And
**Usage:**
```
GraylogQuery.builder()
    .term("ssh")
    .and().term("login")
    .build();
```
**Output:**
```
"ssh" AND "login"
```

#### 2.2. Or
**Usage:**
```
GraylogQuery.builder()
    .term("ssh")
    .or().term("login")
    .build();
```
**Output:**
```
"ssh" OR "login"
```

#### 2.3. Not
**Usage:**
```
GraylogQuery.builder()
    .not().exists("type")
    .build();
```
**Output:**
```
NOT _exists_:type
```

### 3. Parentheses
**Usage:**
```
GraylogQuery.builder()
    .exists("type")
    .and().openParen()
        .term("ssh")
        .or().term("login")
    .closeParen()
    .build();
```
**Output:**
```
_exists_:type AND ( "ssh" OR "login" )
```

## Advanced Usage
Sometimes you might want to compose dynamic queries by condition.

### 1. Prepend Graylog query
**Usage:**
```
GraylogQuery query = GraylogQuery.builder()
    .not().exists("type");

GraylogQuery.builder(query)
    .and().term("ssh")
    .build();
```
**Output:**
```
NOT _exists_:type AND "ssh"
```

### 2. Append Graylog query
**Usage:**
```
GraylogQuery query = GraylogQuery.builder()
    .or().exists("type");

GraylogQuery.builder()
    .term("ssh")
    .append(query)
    .build();
```
**Output:**
```
"ssh" OR _exists_:type
```

## Ports
There are other versions of the Graylog Query Builder library.
* JavaScript: https://github.com/debugrammer/js-graylog-query-builder
* PHP: https://github.com/debugrammer/php-graylog-query-builder
