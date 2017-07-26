package com.lancefallon.config;

import com.lancefallon.config.models.GitInfo;
import com.lancefallon.domain.Channel;
import com.lancefallon.services.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AppProperties {

    private List<Channel> channels;

    @Autowired
    private GitInfo gitInfo;

    public AppProperties(@Autowired ChannelService channelService) {
        this.channels = channelService.findAllWithTranscribers();
    }

    public GitInfo getGitInfo() {
        return gitInfo;
    }

    public void setGitInfo(GitInfo gitInfo) {
        this.gitInfo = gitInfo;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
