package com.lancefallon.repositories;

import com.lancefallon.config.security.CustomUserDetails;
import com.lancefallon.domain.Channel;
import com.lancefallon.domain.UserTranscribeChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class UserTranscribeChannelRepository extends JdbcDaoSupport {

    RowMapper<UserTranscribeChannel> USER_TRANSCRIBE_ROW_MAPPER = (rs, rownum) -> {
        UserTranscribeChannel userTranscribeChannel = new UserTranscribeChannel();
        Channel channel = new Channel();
        channel.setChannelId(rs.getInt("channel_id"));

        CustomUserDetails user = new CustomUserDetails();
        user.setUsername(rs.getString("username"));

        userTranscribeChannel.setUserTranscribeId(rs.getInt("user_transcribe_id"));
        userTranscribeChannel.setChannel(channel);
        userTranscribeChannel.setUser(user);

        return userTranscribeChannel;
    };

    public UserTranscribeChannelRepository(@Autowired DataSource dataSource) {
        setDataSource(dataSource);
    }

    public UserTranscribeChannel getUserTranscribeChannelByUser(String username) {
        return this.getJdbcTemplate().queryForObject("select * from user_transcribe_channel where username = ?", USER_TRANSCRIBE_ROW_MAPPER);
    }
}
