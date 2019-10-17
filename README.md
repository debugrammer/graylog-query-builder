# Graylog Query Builder
> [Graylog Query](https://docs.graylog.org/en/latest/pages/queries.html) Builder especially useful for working with [Graylog REST API](https://docs.graylog.org/en/latest/pages/configuration/rest_api.html).

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
### Basic Statements
#### 1. Term
Messages that include the term or phrase.

**Usage**
```
GraylogQuery.builder()
    .term("ssh")
    .build();
```
**Output:**
```
"ssh"
```

#### 2. Fuzz Term
Messages that include similar term or phrase.

##### 2.1. Fuzziness with default distance
**Usage**
```
GraylogQuery.builder()
    .fuzzTerm("ssh logni")
    .build();
```
**Output:**
```
"ssh logni"~
```

##### 2.2. Fuzziness with custom distance
**Usage**
```
GraylogQuery.builder()
    .fuzzTerm("ssh logni", 1)
    .build();
```
**Output:**
```
"ssh logni"~1
```

#### 3. Exists
Messages that have the field.

**Usage**
```
GraylogQuery.builder()
    .exists("type")
    .build();
```
**Output:**
```
_exists_:type
```

#### 4. Field

##### 4.1. Field (String)
Messages where the field includes the term or phrase.

**Usage**
```
GraylogQuery.builder()
    .field("type", "ssh")
    .build();
```
**Output:**
```
type:"ssh"
```

##### 4.2. Field (Numeric)
Messages where the field includes the number.

**Usage**
```
GraylogQuery.builder()
    .field("http_response_code", 500)
    .build();
```
**Output:**
```
http_response_code:500
```

##### 4.3. One side unbounded range query
Messages where the field satisfies the condition.

**Usage**
```
GraylogQuery.builder()
    .field("http_response_code", ">", 500)
    .build();
```
**Output:**
```
http_response_code:>500
```

#### 5. Fuzz Field
Messages where the field includes similar term or phrase.

##### 5.1. Fuzziness with default distance
**Usage**
```
GraylogQuery.builder()
    .fuzzField("source", "example.org")
    .build();
```
**Output:**
```
source:"example.org"~
```

##### 5.2. Fuzziness with custom distance
**Usage**
```
GraylogQuery.builder()
    .fuzzField("source", "example.org", 1)
    .build();
```
**Output:**
```
source:"example.org"~1
```

#### 6. Range

##### 6.1. Range query
Ranges in square brackets are inclusive, curly brackets are exclusive and can even be combined.

**Usage**
```
GraylogQuery.builder()
    .range("bytes", "{", 0, 64, "]")
    .build();
```
**Output:**
```
bytes:{0 TO 64]
```

##### 6.2. Date range query
The dates needs to be UTC

**Usage**
```
GraylogQuery.builder()
    .range("timestamp", "[", "2019-07-23 09:53:08.175", "2019-07-23 09:53:08.575", "]")
    .build();
```
**Output:**
```
timestamp:["2019-07-23 09:53:08.175" TO "2019-07-23 09:53:08.575"]
```

#### 6. Raw
Raw query.

**Usage**
```
GraylogQuery.builder()
    .raw("/ethernet[0-9]+/") = /ethernet[0-9]+/
    .build();
```
**Output:**
```
/ethernet[0-9]+/
```
