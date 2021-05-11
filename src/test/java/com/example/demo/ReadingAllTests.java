package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ReadingAllTests {
    private IWidgetStorage widgetStorage;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    }

    @Test
    public void shouldReadOrderedByZ() {
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(2);
            setWidth(1);
            setHeight(1);
        }});
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }});
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(3);
            setWidth(1);
            setHeight(1);
        }});

        List<Widget> readWidgets = widgetStorage.read();

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getZ()).isEqualTo(1);
        assertThat(readWidgets.get(1).getZ()).isEqualTo(2);
        assertThat(readWidgets.get(2).getZ()).isEqualTo(3);
    }

    @Test
    public void shouldReturnEmptyCollectionIfThereAreNoWidgets() {
        List<Widget> readWidgets = widgetStorage.read();

        assertThat(readWidgets).isEmpty();
    }
}
