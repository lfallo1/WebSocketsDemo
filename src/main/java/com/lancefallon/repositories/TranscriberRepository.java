package com.lancefallon.repositories;

import com.lancefallon.config.security.CustomUserDetails;
import com.lancefallon.domain.Channel;
import com.lancefallon.domain.Transcriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class TranscriberRepository extends JdbcDaoSupport {

    RowMapper<Transcriber> USER_TRANSCRIBE_ROW_MAPPER = (rs, rownum) -> {
        Transcriber transcriber = new Transcriber();
        Channel channel = new Channel();
        channel.setChannelId(rs.getInt("channel_id"));

        CustomUserDetails user = new CustomUserDetails();
        user.setUsername(rs.getString("username"));

        transcriber.setTranscribeId(rs.getInt("transcribe_id"));
        transcriber.setChannel(channel);
        transcriber.setUser(user);

        return transcriber;
    };

    public TranscriberRepository(@Autowired DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Transcriber> findAll() {
        return this.getJdbcTemplate().query("select * from user_transcribe_channel", USER_TRANSCRIBE_ROW_MAPPER);
    }

    public List<Transcriber> findByChannel(String channel) {
        return this.getJdbcTemplate().query("select * from user_transcribe_channel where channel = ?", new Object[]{channel}, USER_TRANSCRIBE_ROW_MAPPER);
    }
}
