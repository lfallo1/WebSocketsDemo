package com.lancefallon.services;

import com.lancefallon.repositories.TranscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranscriberService {

    @Autowired
    private TranscriberRepository transcriberRepository;

    //TODO add method to get by channel

}