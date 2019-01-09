package com.example.ryne.myapplication.Java;

import java.util.Random;

public class ProductUtils {
    public static double increasePriceItemRandomly(String price) {
        double actualPrice = Double.valueOf(price);
        int a = 30;
        int b = 20;
        int c = new Random().nextBoolean() ? a : b;
        return actualPrice * c / 100;
    }
}
