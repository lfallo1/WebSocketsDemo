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
        return this.getJdbcTemplate().query("select * from channel", CHANNEL_ROW_MAPPER);
    }

}
