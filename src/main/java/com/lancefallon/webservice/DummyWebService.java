package com.lancefallon.webservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dummy")
public class DummyWebService {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Void> dummGet() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
