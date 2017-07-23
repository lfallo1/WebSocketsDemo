package com.lancefallon.repositories;

import com.lancefallon.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class ChannelRepository extends JdbcDaoSupport {

    private static final String FIND_BY_TRANSCRIBER = "select c.* from channel c\n" +
            "inner join user_transcribe_channel utc on c.channel_id = utc.channel_id\n" +
            "where utc.username = ?";

    private static final String FIND_ALL = "select * from channel";

    RowMapper<Channel> CHANNEL_ROW_MAPPER = (rs, rownum) -> {
        Channel channel = new Channel();
        channel.setChannelId(rs.getInt("channel_id"));
        channel.setName(rs.getString("name"));
        return channel;
    };

    public ChannelRepository(@Autowired DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<Channel> findAll() {
        return this.getJdbcTemplate().query(FIND_ALL, CHANNEL_ROW_MAPPER);
    }

    public List<Channel> findByTranscriber(String username) {
        return this.getJdbcTemplate().query(FIND_BY_TRANSCRIBER, CHANNEL_ROW_MAPPER);
    }
}
