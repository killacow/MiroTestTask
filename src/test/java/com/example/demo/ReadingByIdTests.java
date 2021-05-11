package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class ReadingByIdTests {
    private IWidgetStorage widgetStorage;
    private Widget widget;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
        widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }});
    }

    @Test
    public void shouldRead() {
        Widget readWidget = widgetStorage.read(widget.getId());

        assertThat(readWidget).usingRecursiveComparison().isEqualTo(widget);
    }

    @Test
    public void shouldReturnNullIfWidgetDoesNotExist() {
        Widget readWidget = widgetStorage.read(UUID.randomUUID());

        assertThat(readWidget).isNull();
    }
}
