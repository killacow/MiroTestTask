package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class CreatingTests {
    private IWidgetStorage widgetStorage;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    }

    @Test
    public void shouldSetFieldsValues() {
        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(2);
            setZ(3);
            setWidth(4);
            setHeight(5);
        }});

        assertThat(widget.getId()).isNotNull();
        assertThat(widget.getX()).isEqualTo(1);
        assertThat(widget.getY()).isEqualTo(2);
        assertThat(widget.getZ()).isEqualTo(3);
        assertThat(widget.getWidth()).isEqualTo(4);
        assertThat(widget.getHeight()).isEqualTo(5);
    }

    @Test
    public void shouldPutWidgetOnTopWhenZIsNotSpecified() {
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(-10);
            setWidth(1);
            setHeight(1);
        }});

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setWidth(2);
            setHeight(2);
        }});

        assertThat(widget.getZ()).isEqualTo(-9);
    }

    @Test
    public void shouldPutWidgetOnTopWhenZIsNotSpecifiedAndThereIsZGap() {
        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }});

        widgetStorage.create(new WidgetCreateRequest() {{
            setX(3);
            setY(3);
            setZ(3);
            setWidth(3);
            setHeight(3);
        }});

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setWidth(2);
            setHeight(2);
        }});

        assertThat(widget.getZ()).isEqualTo(4);
    }

    @Test
    public void shouldShiftWidgetsWithSameAndGreaterZ_testTaskForABackendDeveloperExample1() {
        WidgetStorageHelper.addWidgets(widgetStorage, 1, 3);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(2);
            setWidth(2);
            setHeight(2);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(widget.getId());
        assertThat(result.get(1).getZ()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(3);
        assertThat(result.get(3).getId()).isEqualTo(ids.get(2));
        assertThat(result.get(3).getZ()).isEqualTo(4);
    }

    @Test
    public void shouldNotShiftWidgetsWithGreaterZIfThereIsGap_testTaskForABackendDeveloperExample2() {
        WidgetStorageHelper.addWidgets(widgetStorage, 1, 1);
        WidgetStorageHelper.addWidgets(widgetStorage, 5, 2);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(2);
            setWidth(2);
            setHeight(2);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(widget.getId());
        assertThat(result.get(1).getZ()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(5);
        assertThat(result.get(3).getId()).isEqualTo(ids.get(2));
        assertThat(result.get(3).getZ()).isEqualTo(6);
    }

    @Test
    public void shouldShiftOnlyWidgetWithSameZIfThereIsGapAfter_testTaskForABackendDeveloperExample3() {
        WidgetStorageHelper.addWidgets(widgetStorage, 1, 2);
        WidgetStorageHelper.addWidgets(widgetStorage, 4, 1);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(2);
            setWidth(2);
            setHeight(2);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(widget.getId());
        assertThat(result.get(1).getZ()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(3);
        assertThat(result.get(3).getId()).isEqualTo(ids.get(2));
        assertThat(result.get(3).getZ()).isEqualTo(4);
    }

    @Test
    public void shouldAllowGapFromBelow() {
        WidgetStorageHelper.addWidgets(widgetStorage, 1, 2);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(-1);
            setWidth(2);
            setHeight(2);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(widget.getId());
        assertThat(result.get(0).getZ()).isEqualTo(-1);
        assertThat(result.get(1).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(1).getZ()).isEqualTo(1);
        assertThat(result.get(2).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(2).getZ()).isEqualTo(2);
    }

    @Test
    public void shouldAllowGapFromAbove() {
        WidgetStorageHelper.addWidgets(widgetStorage, 1, 2);
        List<UUID> ids = widgetStorage.read().stream().map(Widget::getId).collect(Collectors.toList());

        Widget widget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(2);
            setY(2);
            setZ(4);
            setWidth(2);
            setHeight(2);
        }});

        List<Widget> result = widgetStorage.read();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(ids.get(0));
        assertThat(result.get(0).getZ()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(ids.get(1));
        assertThat(result.get(1).getZ()).isEqualTo(2);
        assertThat(result.get(2).getId()).isEqualTo(widget.getId());
        assertThat(result.get(2).getZ()).isEqualTo(4);
    }
}
