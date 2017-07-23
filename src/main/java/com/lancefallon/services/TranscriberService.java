package com.lancefallon.services;

import com.lancefallon.domain.Transcriber;
import com.lancefallon.repositories.TranscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class TranscriberService {

    @Autowired
    private TranscriberRepository transcriberRepository;

    public List<Transcriber> findByChannelId(Integer channelId){
        return this.transcriberRepository.findByChannel(channelId);
    }

    public Boolean isTranscriberOfChannel(Principal auth, String destination) {
        try{
            Integer channelId = Integer.parseInt(destination);
            List<Transcriber> transcribers = this.findByChannelId(channelId);
            for(Transcriber t : transcribers){
                if(t.getUser().getUsername().equals(auth.getName())){
                    return true;
                }
            }
        } catch(NumberFormatException e){
            System.out.println("Not a transcriber channel");
        }
        return false;
    }
}