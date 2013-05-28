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