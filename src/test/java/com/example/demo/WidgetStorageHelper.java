package com.example.demo;

import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;

import java.util.stream.IntStream;

public class WidgetStorageHelper {
    public static void addWidgets(IWidgetStorage widgetStorage, int fromZ, int number) {
        addWidgetsFromTo(widgetStorage, fromZ, fromZ + number - 1);
    }

    public static void addWidgetsFromTo(IWidgetStorage widgetStorage, int fromZ, int toZ) {
        IntStream.range(fromZ, toZ + 1)
                .forEach(
                        z -> widgetStorage.create(new WidgetCreateRequest() {{
                            setX(z);
                            setY(z);
                            setZ(z);
                            setWidth(1);
                            setHeight(1);
                        }}));
    }
}
