package com.demo.spring.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
/**
 * 
 * @author rnagulapalle
 *
 */
public class DatabaseQueryBean extends JdbcDaoSupport {

	public String query(String username) {
		List<RememberMeToken> rememberMeTokens = getJdbcTemplate().query("select username, series, token, last_used from persistent_logins where username = ?", new String[] { username }, new RowMapper<RememberMeToken>() {
			public RememberMeToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				String username = rs.getString(1);
				String series = rs.getString(2);
				String token = rs.getString(3);
				Timestamp lastUsed = rs.getTimestamp(4);
				return new RememberMeToken(username, series, token, lastUsed);
			}
		});
		System.out.println("query result size = " + rememberMeTokens.size() );
		if (rememberMeTokens.size() != 0) {
			return rememberMeTokens.get(0).toString();
		}
		return null;
	}

	class RememberMeToken {
		private String username;
		private String series;
		private String token;
		private Timestamp lastUsed;

		RememberMeToken(String username, String series, String token, Timestamp lastUsed) {
			this.username = username;
			this.series = series;
			this.token = token;
			this.lastUsed = lastUsed;
		}

		public String getUsername() {
			return username;
		}

		public String getSeries() {
			return series;
		}

		public String getToken() {
			return token;
		}

		public Timestamp getLastUsed() {
			return lastUsed;
		}

		@Override
		public String toString() {
			return "username: " + username + ", series: " + series + ", token: "+token + ", last used: " + lastUsed;
		}
	}
}