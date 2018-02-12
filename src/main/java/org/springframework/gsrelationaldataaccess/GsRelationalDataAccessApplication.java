package org.springframework.gsrelationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.gsrelationaldataaccess.domain.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class GsRelationalDataAccessApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(GsRelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GsRelationalDataAccessApplication.class, args);
	}

	@Autowired
	private	JdbcTemplate jdbcTemplate;


	@Override
	public void run(String... strings) throws Exception {
		log.info("Creating tables");

		jdbcTemplate.execute("Drop table customers if exists");
		jdbcTemplate.execute("create table customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		// Split up the array of whole names into an array of first/last names
		String[] names = "John Woo;Jeff Dean;Josh Bloch;Josh Long".split(";");
		for (String fullname : names) {
			String[] name = fullname.split(" ");
			jdbcTemplate.update("INSERT INTO customers (first_name, last_name) values(?, ?)", name[0], name[1]);
		}

		//Other way
	/*	List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name->name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name->log.info(String.format("Inserting customer record for %s, %s", name[0], name[1])));

		jdbcTemplate.batchUpdate("INSERT INTO customers (first_name, last_name) values(?, ?)", splitUpNames);
*/
		log.info("Querying for customer records where first_name = Josh");
		jdbcTemplate.query("Select * from customers where first_name = ?", new Object[]{"Josh"},
				new RowMapper<Customer>() {
					@Override
					public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
						return new Customer(resultSet.getLong("id"),
								resultSet.getString("first_name"),
								resultSet.getString("last_name"));
					}
				}).forEach(System.out::println);

		//replace with lambda
//		jdbcTemplate.query("Select * from customers where first_name = ?", new Object[]{"Josh"},
//				(resultSet, i) -> new Customer(resultSet.getLong("id"),
//                        resultSet.getString("first_name"),
//                        resultSet.getString("last_name"))).forEach(System.out::println);
	}
}
