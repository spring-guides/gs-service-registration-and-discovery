<#assign project_id="gs-relational-data-access">
This guide walks you through the process of accessing relational data with Spring.

What you'll build
-----------------

You'll build an application using Spring's `JdbcTemplate` to access data stored in a relational database.

What you'll need
----------------

 - About 15 minutes
 - <@prereq_editor_jdk_buildtools/>


## <@how_to_complete_this_guide jump_ahead='Create a Customer object'/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>

### Create a Gradle build file

    <@snippet path="build.gradle" prefix="initial"/>

<@bootstrap_starter_pom_disclaimer/>


<a name="initial"></a>
Create a Customer object
------------------------
The simple data access logic you will work with below below manages first and last names of customers. To represent this data at the application level, create a `Customer` class.

    <@snippet path="src/main/java/hello/Customer.java" prefix="complete"/>


Store and retrieve data
-----------------------

Spring provides a template class called `JdbcTemplate` that makes it easy to work with SQL relational databases and JDBC. Most JDBC code is mired in resource acquisition, connection management, exception handling, and general error checking that is wholly unrelated to what the code is meant to achieve. The `JdbcTemplate` takes care of all of that for you. All you have to do is focus on the task at hand.

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

In this example you set up a JDBC [`DataSource`]() using Spring's handy `SimpleDriverDataSource`. Then, you use the `DataSource` to construct a `JdbcTemplate` instance. 

> **Note:** `SimpleDriverDataSource` is a convenience class and **not** intended for production.

After you configure `JdbcTemplate`, it's easy to start making calls to the database. 

First, you install some DDL using `JdbcTemplate`'s `execute` method.

Then, you install some records in your newly created table using `JdbcTemplate`'s `update` method. The first argument to the method call is the query string, the last argument (the array of `Object`s) holds the variables to be substituted into the query where the "`?`" characters are.

> **Note:** Using `?` for arguments avoids [SQL injection attacks](http://en.wikipedia.org/wiki/SQL_injection) by instructing JDBC to bind variables.

Finally you use the `query` method to search your table for records matching the criteria. You again use the "`?`" arguments to create parameters for the query, passing in the actual values when you make the call. The last argument in the `query` method is an instance of `RowMapper<T>`, which you provide. Spring's done 90% of the work, but it can't know what you want it to do with the result set data. So, you provide a `RowMapper<T>` instance that Spring will call for each record, aggregate the results, and return as a collection. 

## <@build_an_executable_jar_with_gradle/>


<@run_the_application_with_gradle/>

You should see the following output:

```sh
Creating tables
Inserting customer record for John Woo
Inserting customer record for Jeff Dean
Inserting customer record for Josh Bloch
Inserting customer record for Josh Long
Querying for customer records where first_name = 'Josh':
Customer[id=3, firstName='Josh', lastName='Bloch']
Customer[id=4, firstName='Josh', lastName='Long']
```


Summary
-------
Congrats! You've just used Spring to develop a simple JDBC client. There's more to building and working with JDBC and data stores in general than is covered here, but this should provide a good start.
