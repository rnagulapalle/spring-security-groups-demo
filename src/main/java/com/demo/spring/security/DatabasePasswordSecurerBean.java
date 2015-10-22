package com.demo.spring.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 
 * @author rnagulapalle
 *
 */
public class DatabasePasswordSecurerBean extends JdbcDaoSupport {
	@Autowired
	private SaltSource saltSource;
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Logger logger = Logger.getLogger(DatabasePasswordSecurerBean.class);

	public void secureDatabase() {
		getJdbcTemplate().query("select username,password from users", new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				String username = rs.getString(1);
				String password = rs.getString(2);
				UserDetails user = userDetailsService.loadUserByUsername(username);
				String encodedPassword = passwordEncoder.encodePassword(password, saltSource.getSalt(user));
				getJdbcTemplate().update("update users set password = ? where username = ?", encodedPassword, username);
				logger.debug("updated password for username " + username + " old/new: " + password + "/" + encodedPassword);
			}
		});
	}
}