package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class UpdatingTests {
    private IWidgetStorage widgetStorage;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    }

    @Test
    public void shouldUpdateX() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(widget.getId(), new WidgetUpdateRequest() {{
            setX(10);
        }});

        assertThat(updatedWidget.getX()).isEqualTo(10);
        assertThat(updatedWidget.getY()).isEqualTo(2);
        assertThat(updatedWidget.getZ()).isEqualTo(3);
        assertThat(updatedWidget.getWidth()).isEqualTo(4);
        assertThat(updatedWidget.getHeight()).isEqualTo(5);
    }

    @Test
    public void shouldUpdateY() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(widget.getId(), new WidgetUpdateRequest() {{
            setY(10);
        }});

        assertThat(updatedWidget.getX()).isEqualTo(1);
        assertThat(updatedWidget.getY()).isEqualTo(10);
        assertThat(updatedWidget.getZ()).isEqualTo(3);
        assertThat(updatedWidget.getWidth()).isEqualTo(4);
        assertThat(updatedWidget.getHeight()).isEqualTo(5);
    }

    @Test
    public void shouldUpdateZ() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(widget.getId(), new WidgetUpdateRequest() {{
            setZ(10);
        }});

        assertThat(updatedWidget.getX()).isEqualTo(1);
        assertThat(updatedWidget.getY()).isEqualTo(2);
        assertThat(updatedWidget.getZ()).isEqualTo(10);
        assertThat(updatedWidget.getWidth()).isEqualTo(4);
        assertThat(updatedWidget.getHeight()).isEqualTo(5);
    }

    @Test
    public void shouldUpdateWidth() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(widget.getId(), new WidgetUpdateRequest() {{
            setWidth(10);
        }});

        assertThat(updatedWidget.getX()).isEqualTo(1);
        assertThat(updatedWidget.getY()).isEqualTo(2);
        assertThat(updatedWidget.getZ()).isEqualTo(3);
        assertThat(updatedWidget.getWidth()).isEqualTo(10);
        assertThat(updatedWidget.getHeight()).isEqualTo(5);
    }

    @Test
    public void shouldUpdateHeight() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(widget.getId(), new WidgetUpdateRequest() {{
            setHeight(10);
        }});

        assertThat(updatedWidget.getX()).isEqualTo(1);
        assertThat(updatedWidget.getY()).isEqualTo(2);
        assertThat(updatedWidget.getZ()).isEqualTo(3);
        assertThat(updatedWidget.getWidth()).isEqualTo(4);
        assertThat(updatedWidget.getHeight()).isEqualTo(10);
    }

    @Test
    public void shouldShiftUpOnIncreasingZ() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 5);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        widgetStorage.update(ids.get(1), new WidgetUpdateRequest() {{
            setZ(3);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(0);
        assertThat(result.get(1).getId()).isEqualTo(ids.get(2));
        assertThat(result.get(1).getZ()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(3);
        assertThat(result.get(3).getId()).isEqualTo(ids.get(3));
        assertThat(result.get(3).getZ()).isEqualTo(4);
        assertThat(result.get(4).getId()).isEqualTo(ids.get(4));
        assertThat(result.get(4).getZ()).isEqualTo(5);
    }

    @Test
    public void shouldShiftDownOnDecreasingZ() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 5);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        widgetStorage.update(ids.get(3), new WidgetUpdateRequest() {{
            setZ(1);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(0);
        assertThat(result.get(1).getId()).isEqualTo(ids.get(3));
        assertThat(result.get(1).getZ()).isEqualTo(1);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(2);
        assertThat(result.get(3).getId()).isEqualTo(ids.get(2));
        assertThat(result.get(3).getZ()).isEqualTo(3);
        assertThat(result.get(4).getId()).isEqualTo(ids.get(4));
        assertThat(result.get(4).getZ()).isEqualTo(4);
    }

    @Test
    public void shouldNotChangeAnythingWhenWidgetDoesNotExist() {
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        Widget updatedWidget = widgetStorage.update(UUID.randomUUID(), new WidgetUpdateRequest() {{
            setX(10);
            setY(20);
            setZ(30);
            setWidth(40);
            setHeight(50);
        }});

        assertThat(updatedWidget).isNull();
        List<Widget> readWidgets = widgetStorage.read();
        assertThat(readWidgets).hasSize(1);
        assertThat(readWidgets.get(0).getX()).isEqualTo(1);
        assertThat(readWidgets.get(0).getY()).isEqualTo(2);
        assertThat(readWidgets.get(0).getZ()).isEqualTo(3);
        assertThat(readWidgets.get(0).getWidth()).isEqualTo(4);
        assertThat(readWidgets.get(0).getHeight()).isEqualTo(5);
    }
}
