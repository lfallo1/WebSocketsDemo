package com.lancefallon.webservice;

import com.lancefallon.config.AppProperties;
import com.lancefallon.config.models.GitInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigWebService {

    @Autowired
    private AppProperties appProperties;

    @RequestMapping(value = "/gitinfo", method = RequestMethod.GET)
    public ResponseEntity<GitInfo> gitInformation() {
        return new ResponseEntity<>(appProperties.getGitInfo(), HttpStatus.OK);
    }
}
