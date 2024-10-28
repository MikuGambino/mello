package ru.sstu.Mello.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Random;

@Service
public class ColorGenerator {
    public String generatePastelColor() {
        Random random = new Random();

        int red = 150 + random.nextInt(106);  // от 150 до 255
        int green = 150 + random.nextInt(106);  // от 150 до 255
        int blue = 150 + random.nextInt(106);  // от 150 до 255

        Color color = new Color(red, green, blue);
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }
}
