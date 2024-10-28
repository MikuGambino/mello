package ru.sstu.Mello.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.sstu.Mello.service.ImageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/image/account/{name}")
    public byte[] getImg(@PathVariable(value = "name") String name) throws IOException {
        return imageService.getImg(name);
    }
}