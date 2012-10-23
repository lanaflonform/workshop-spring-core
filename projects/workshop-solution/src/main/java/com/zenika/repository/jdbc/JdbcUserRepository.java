/**
 * 
 */
package com.zenika.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.zenika.domain.User;
import com.zenika.repository.UserRepository;

/**
 * @author acogoluegnes
 *
 */
public class JdbcUserRepository implements UserRepository {
	
	private final JdbcOperations tpl;
	
	private RowMapper<User> rowMapper = new UserRowMapper();
	
	public JdbcUserRepository(DataSource dataSource) {
		this.tpl = new JdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * @see com.zenika.repository.UserRepository#getByLogin(java.lang.String)
	 */
	@Override
	public User getByLogin(String login) {
		return tpl.queryForObject("select id,login,password from users where login = ?", rowMapper,login);
	}
	
	@Override
	public User create(final String login, final String password) {
		// solution simple, ne permet pas de récupérer l'identifiant généré
		// tpl.update("insert into users (login,password) values (?,?)",login,password);

		// solution plus avancée pour pouvoir récupérer l'identifiant généré
		KeyHolder keyHolder = new GeneratedKeyHolder();
		tpl.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement stmt = con.prepareStatement("insert into users (login,password) values (?,?)");
				stmt.setString(1, login);
				stmt.setString(2, password);
				return stmt;
			}
		}, keyHolder);
		return new User(keyHolder.getKey().longValue(),login,password);
	}
	
	private static class UserRowMapper implements RowMapper<User> {
		
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getLong("id"),rs.getString("login"),rs.getString("password"));
		}
		
	}

}
