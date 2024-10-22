package ru.sstu.Mello.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static java.nio.file.Files.deleteIfExists;

@Service
public class ImageService {

    public byte[] getImg(@PathVariable(value = "name") String name) throws IOException {
        String path = "src/main/resources/static/image/";
        var s = new FileInputStream(path + name);
        var file = s.readAllBytes();
        s.close();
        return file;
    }

    @SneakyThrows
    public String saveImage(MultipartFile multipartFile) {
        String imagePath = "src/main/resources/static/image/";
        String name = UUID.randomUUID().toString();
        var split = Objects.requireNonNull(multipartFile.getOriginalFilename()).split("\\.");
        String fullname = name + "." + split[1];
        String path = imagePath + fullname;
        File file = new File(path);
        file.createNewFile();
        var outstream = new FileOutputStream(path);
        outstream.write(multipartFile.getBytes());
        outstream.close();
        return fullname;
    }

    @SneakyThrows
    public void deleteImage(String filename) {
        String imagePath = "src/main/resources/static/image/";
        Path path = Path.of(imagePath + filename);
        deleteIfExists(path);
    }
}
