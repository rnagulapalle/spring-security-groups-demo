package com.demo.spring.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

/**
 * This class extends Spring class to add password change functionality, but
 * spring already provides a class that gives CRUD operations on users, which
 * is: org.springframework.security.privisioning.JdbcUserDetailsManager - this could be used instead.
 * 
 * @author rnagulapalle
 */
public class CustomJdbcDaoImpl extends JdbcDaoImpl implements IChangePassword {

	private Logger logger = Logger.getLogger(CustomJdbcDaoImpl.class);
	
	@Autowired
	private SaltSource saltSource;
	@Autowired PasswordEncoder passwordEncoder;

	public void changePassword(String username, String password) {
		logger.debug("changePassword - begin - updating user password for user: " + username);
		
		UserDetails user = loadUserByUsername(username);
		String encodedPassword = passwordEncoder.encodePassword(password, saltSource.getSalt(user));
		
		JdbcTemplate template = getJdbcTemplate();
		template.update("UPDATE USERS SET PASSWORD =  ? WHERE USERNAME = ?", encodedPassword, username);

		logger.debug("changePassword - end");
	}

	/**
	 * Override superclass method for test purpose only to see that actually
	 * this service is running
	 */
	@Override
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		logger.debug("...loadUserAuthorities...");
		return super.loadUserAuthorities(username);
	}

	@Override
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		logger.debug("...loadGroupAuthorities...");
		return super.loadGroupAuthorities(username);
	}
	
	@Override
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		logger.debug("createUserDetails, username=" + username);
		String returnUsername = userFromUserQuery.getUsername();

		if (!isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}
		return new SaltedUser(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities, ((SaltedUser) userFromUserQuery).getSalt());
	}

	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		logger.debug("loadUsersByUsername, username=" + username);
		return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] { username }, new RowMapper<UserDetails>() {
			public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				String username = rs.getString(1);
				String password = rs.getString(2);
				boolean enabled = rs.getBoolean(3);
				String salt = rs.getString(4);
				return new SaltedUser(username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES, salt);
			}
		});
	}
}