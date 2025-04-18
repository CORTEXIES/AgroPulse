package com.github.cortex.photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
public class PhotoController {

//    private final MessageBuffer<String> photIdBuffer;
    private final PhotoExchangeService photoExchangeService;

    @Autowired
    public PhotoController(
//            MessageBuffer<String> photoIdBuffer,
            PhotoExchangeService photoExchangeService
    ) {
//        this.photIdBuffer = photoIdBuffer;
        this.photoExchangeService = photoExchangeService;
    }

    public void recordPhotoId(Message message) {

    }
}