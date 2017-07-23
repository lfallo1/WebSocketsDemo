package com.lancefallon.webservice;

import com.lancefallon.domain.Channel;
import com.lancefallon.services.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/channel")
public class ChannelWebService {

    @Autowired
    private ChannelService channelService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Channel>> loadChannels() {
        return new ResponseEntity<>(this.channelService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('TRANSCRIBER')")
    public ResponseEntity<List<Channel>> loadChannels(Principal auth) {
        return new ResponseEntity<>(this.channelService.findAllWithTranscribers(), HttpStatus.OK);
    }

}
