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