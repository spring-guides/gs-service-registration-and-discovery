# Getting Started: Accessing Relational Data with Spring

What you'll build
-----------------

This guide walks you through the process of accessing relational data with Spring.

What you'll need
----------------

 - About 15 minutes
 - {!snippet:prereq-editor-jdk-buildtools}

## {!snippet:how-to-complete-this-guide}

<a name="scratch"></a>
Set up the project
------------------

{!snippet:build-system-intro}

{!snippet:create-directory-structure-hello}

### Create a Maven POM

{!snippet:maven-project-setup-options}

`pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-relational-data-access</artifactId>
    <version>0.1.0</version>

    <parent>
        <groupId>org.springframework.bootstrap</groupId>
        <artifactId>spring-bootstrap-starters</artifactId>
        <version>0.5.0.BUILD-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
    </dependencies>

    <!-- TODO: remove once bootstrap goes GA -->
    <repositories>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>http://repo.springsource.org/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>http://repo.springsource.org/snapshot</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
</project>
```

{!snippet:bootstrap-starter-pom-disclaimer}

<a name="initial"></a>
Creating a Customer object, setting up a database, and storing/retrieving the data
----------------------------------------------------------------------------------

Spring provides a convenient template class called the `JdbcTemplate`. It makes working with relational SQL databases through JDBC a trivial affair. When you look at most JDBC code, it's mired in resource acquisition, connection management, exception handling and general error checking code that is wholly unrelated to what the code is trying to achieve. The `JdbcTemplate` takes care of all of that for you. All you have to do is focus on the task at hand.

`src/main/java/hello/Application.java`
```java
package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class Application {

    public static class Customer {
        private long id;
        private String firstName, lastName;

        public Customer(long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return String.format(
                    "Customer[id=%d, firstName='%s', lastName='%s']",
                    id, firstName, lastName);
        }

        // getters & setters omitted for brevity
    }

    public static void main(String args[]) {
        // simple DS for test (not for production!)
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("sa");
        dataSource.setUrl("jdbc:h2:mem");
        dataSource.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        System.out.println("Creating tables");
        jdbcTemplate.execute("drop table customers if exists");
        jdbcTemplate.execute("create table customers(" +
                "id serial, first_name varchar(255), last_name varchar(255))");

        String[] names = "John Woo;Jeff Dean;Josh Bloch;Josh Long".split(";");
        for (String fullname : names) {
            String[] name = fullname.split(" ");
            System.out.printf("Inserting customer record for %s %s\n", name[0], name[1]);
            jdbcTemplate.update(
                    "INSERT INTO customers(first_name,last_name) values(?,?)",
                    name[0], name[1]);
        }

        System.out.println("Querying for customer records where first_name = 'Josh':");
        Collection<Customer> results = jdbcTemplate.query(
                "select * from customers where first_name = ?", new Object[] { "Josh" },
                new RowMapper<Customer>() {
                    @Override
                    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Customer(rs.getLong("id"), rs.getString("first_name"),
                                rs.getString("last_name"));
                    }
                });

        for (Customer customer : results)
            System.out.println(customer);
    }
}
```

This example sets up a JDBC `DataSource` using Spring's handy `SimpleDriverDataSource` (this class is **not** intended for production!). Then, we use that to construct a `JdbcTemplate` instance. For more on DatSources, see [this link]().

Once we have our configured `JdbcTemplate`, it's easy to then start making calls to the database. 

First, we install some DDL using `JdbcTemplate`'s `execute` method.

Then, we install some records in our newly created table using `JdbcTemplate`'s `update` method. The first argument to the method call is the query string, the last argument (the array of `Object`s) holds the variables to be substituted into the query where the "`?`" characters are.

> Using `?` for arguments avoids [SQL injection attacks](http://en.wikipedia.org/wiki/SQL_injection) by instructing JDBC to bind variables.

Finally we use the `query` method to search our table for records matching our criteria. We again use the "`?`" arguments to parameterize the query, passing in the actual values when we make the call. The last argument in the `query` method is an instance of `RowMapper<T>`, which we provide. Spring's done 90% of the work, but it can't possibly know what we want it to do with the result set data. So, we provide a `RowMapper<T>` instance that Spring will call for each record, aggregate the results, and then give back to us as a collection. 


## {!snippet:build-an-executable-jar}


Run the application
-------------------

Run your application with `java -jar` at the command line:

    java -jar target/gs-relational-data-access-0.1.0.jar


You should see the following output:

    Creating tables
    Inserting customer record for John Woo
    Inserting customer record for Jeff Dean
    Inserting customer record for Josh Bloch
    Inserting customer record for Josh Long
    Querying for customer records where first_name = 'Josh':
    Customer[id=3, firstName='Josh', lastName='Bloch']
    Customer[id=4, firstName='Josh', lastName='Long']


Summary
-------
Congrats! You've just developed a simple JDBC client using Spring. There's more to building and working with JDBC and data stores in general than is covered here, but this should provide a good start.
