Getting Started: Accessing Relational Data with Spring
=========================================

This Getting Started guide will walk you through the process of accessing relational data with Spring.
To help you get started, we've provided an initial project structure for you in GitHub:

```sh
$ git clone https://github.com/springframework-meta/gs-relational-data-access.git
```

Before we can write the REST endpoint itself, there's some initial project setup that's required. Or, you can skip straight to the [fun part]().

Selecting Dependencies
----------------------
The sample in this Getting Started Guide will leverage Spring's core data-access modules and the H2, in-memory, embedded database. 

  - com.h2database:h2:1.3.168 
  - org.springframework:spring-orm:3.2.2.RELEASE

Click here for details on how to map these dependencies to your specific build tool.

Invoking REST services with the RestTemplate
----------------------------
Spring provides a convenient template class called the `JdbcTemplate`. The `JdbcTemplate` makes working with relational, SQL-databases through JDBC a trivial affair. When you look at most JDBC code, it's mired in resource accquisition, connection accquisition, exception handling and general error checking code that is wholey unrelated to what the code is trying to acheive. The `JdbcTemplate` takes care of all of that for you. All you have to do is focus on the task at hand.


```java
package hello;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class Main {

    public static class Customer {
        private String firstName, lastName;
        private long id;

        public Customer(long id, String f, String l) {
            this.firstName = f;
            this.lastName = l;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Customer{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", id=" + id +
                    '}';
        }

        // getters & setters omitted for brevity
    }


    public static void main(String args[]) throws Throwable {
        // simple DS for test (not for production!)
        SimpleDriverDataSource simpleDriverDataSource = new SimpleDriverDataSource();
        simpleDriverDataSource.setDriverClass(org.h2.Driver.class);
        simpleDriverDataSource.setUsername("sa");
        simpleDriverDataSource.setUrl("jdbc:h2:mem");
        simpleDriverDataSource.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);

        // install some DDL
        jdbcTemplate.execute("drop table customers if exists");
        jdbcTemplate.execute("create table customers(id serial, first_name varchar(255), last_name varchar(255))");

        // install some records
        String[] names = "John,Woo;George,Lopez;Josh,Bloch;James,Batters".split(";");
        for (String n : names) {
            String[] firstAndLast = n.split(",");
            String fn = firstAndLast[0],
                    ln = firstAndLast[1];
            jdbcTemplate.update("INSERT INTO customers(first_name,last_name) values(?,?)", new Object[]{fn, ln});
        }

        // query for the records
        Collection<Customer> customersCollection = jdbcTemplate.query(
                "select * from customers where first_name = ?",
                new Object[]{"Josh"},
                new RowMapper<Customer>() {
                    @Override
                    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"));
                    }
                });

        System.out.println("Iterating through the customer records in the DB where the first_name = 'Josh'");
        for (Customer customer : customersCollection)
            System.out.println(customer);

    }
}

```

This example sets up a JDBC `DataSource` using the Spring class `SimpleDriverDataSource` (this class is **not** intended for production!). Then, we use that to construct a `JdbcTemplate` instance. For more on DatSources, see [this link]().

Once we have our configured `JdbcTemplate`, it's easy to then start making calls to the database. 

First, we install some DDL using the `JdbcTemplate#execute` method.

Then, we install some records in our newly created table using `JdbcTemplate#update`. The first argument to the method call is the query string, the last argument (the array of `Object`s) holds the variables to be substituted into the query where the "`?`" characters are.

Finally we use the `JdbcTemplate#query` method to search our table for records matching our criteria. We again use the "`?`" arguments to parameterize the query, passing in the actual values when we make the call. The last argument in the `JdbcTemplate#query` method is an instance of `RowMapper<T>`, which we provide. Spring's done 90% of the work, but it can't possibly know what we want it to do with the result set data. So, we provide a `RowMapper<T>` instance that Spring will call for each record, aggregate the results, and then give back to us as a collection. 

Building and Running the Client
--------------------------------------
To invoke the code and see the results of the search, simply run it from the command line, like this:

```sh
$ gradle run
```
	
This will compile the `main` method and then run it.


Next Steps
----------
Congratulations! You have just developed a simple JDBC client using Spring.  

There's more to building and working with JDBC and datastores in general than is covered here. You may want to continue your exploration of Spring and REST with the following Getting Started guides:

* **Consuming REST Services on Android**
* Handling POST, PUT, and GET requests in REST endpoints
* Creating self-describing APIs with HATEOAS
* Securing a REST endpoint with HTTP Basic
* Securing a REST endpoint with OAuth
* Consuming REST APIs
* Testing REST APIs


