package com.lancefallon.config;

import com.lancefallon.config.models.GitInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired
    private GitInfo gitInfo;

    public AppProperties() {
    }

    public GitInfo getGitInfo() {
        return gitInfo;
    }

    public void setGitInfo(GitInfo gitInfo) {
        this.gitInfo = gitInfo;
    }
}
