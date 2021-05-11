package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletingTests {
    private IWidgetStorage widgetStorage;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    }

    @Test
    public void shouldDelete() {
        Widget widget =  widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }});
        Widget widgetToDelete = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(2);
            setWidth(2);
            setHeight(2);
        }});

        widgetStorage.delete(widgetToDelete.getId());

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(widget);
    }

    @Test
    public void shouldNotChangeAnythingWhenWidgetDoesNotExist() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        widgetStorage.delete(UUID.randomUUID());

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(widget);
    }
}
