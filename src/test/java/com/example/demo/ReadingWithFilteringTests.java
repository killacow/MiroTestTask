package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadingWithFilteringTests {

    private WidgetStorageWithPagingAndFiltering widgetStorage;
    private Widget upperLeftWidget;
    private Widget upperRightWidget;
    private Widget lowerLeftWidget;
    private Widget lowerRightWidget;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
        upperLeftWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(-10);
            setY(10);
            setZ(0);
            setWidth(9);
            setHeight(9);
        }});
        upperRightWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(10);
            setY(10);
            setZ(-50);
            setWidth(10);
            setHeight(10);
        }});
        lowerLeftWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(-10);
            setY(-10);
            setZ(50);
            setWidth(11);
            setHeight(11);
        }});
        lowerRightWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(10);
            setY(-10);
            setZ(25);
            setWidth(12);
            setHeight(12);
        }});
    }

    @Test
    public void all() {
        List<Widget> readWidgets = widgetStorage.read(-15, 16, 15, -16);

        assertThat(readWidgets).hasSize(4);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerRightWidget.getId());
        assertThat(readWidgets.get(3).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void shrinkLeft() {
        List<Widget> readWidgets = widgetStorage.read(-14, 16, 15, -16);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerRightWidget.getId());
    }

    @Test
    public void cutLeft() {
        List<Widget> readWidgets = widgetStorage.read(-13, 16, 15, -16);

        assertThat(readWidgets).hasSize(2);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(lowerRightWidget.getId());
    }

    @Test
    public void shrinkRight() {
        List<Widget> readWidgets = widgetStorage.read(-15, 15, 15, -16);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void cutRight() {
        List<Widget> readWidgets = widgetStorage.read(-15, 14, 15, -16);

        assertThat(readWidgets).hasSize(2);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void shrinkUpper() {
        List<Widget> readWidgets = widgetStorage.read(-15, 16, 14, -16);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(lowerRightWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void cutUpper() {
        List<Widget> readWidgets = widgetStorage.read(-15, 16, 13, -16);

        assertThat(readWidgets).hasSize(2);
        assertThat(readWidgets.get(0).getId()).isEqualTo(lowerRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void shrinkLower() {
        List<Widget> readWidgets = widgetStorage.read(-15, 16, 15, -15);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void cutLower() {
        List<Widget> readWidgets = widgetStorage.read(-15, 16, 15, -14);

        assertThat(readWidgets).hasSize(2);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
    }

    @Test
    public void allBig() {
        List<Widget> readWidgets = widgetStorage.read(-150, 160, 150, -160);

        assertThat(readWidgets).hasSize(4);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerRightWidget.getId());
        assertThat(readWidgets.get(3).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void none() {
        List<Widget> readWidgets = widgetStorage.read(-10, 10, 10, -10);

        assertThat(readWidgets).hasSize(0);
    }

    @Test
    public void fromTask() {
        WidgetStorageWithPagingAndFiltering widgetStorage = new WidgetStorageWithPagingAndFiltering();
        Widget firstWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(50);
            setY(50);
            setWidth(100);
            setHeight(100);
        }});
        Widget secondWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(50);
            setY(100);
            setWidth(100);
            setHeight(100);
        }});
        //noinspection unused
        Widget thirdWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(100);
            setY(100);
            setWidth(100);
            setHeight(100);
        }});

        List<Widget> readWidgets = widgetStorage.read(0, 100, 150, 0);

        assertThat(readWidgets).hasSize(2);
        assertThat(readWidgets.get(0).getId()).isEqualTo(firstWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(secondWidget.getId());
    }

    @Test
    public void shouldHandleMoving() {
        widgetStorage.update(upperRightWidget.getId(), new WidgetUpdateRequest() {{
            setX(0);
            setHeight(20);
            setY(5);
            setZ(40);
        }});

        List<Widget> readWidgets = widgetStorage.read(-15, 10, 15, -16);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperLeftWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerLeftWidget.getId());
    }

    @Test
    public void shouldHandleDeleting() {
        widgetStorage.delete(upperLeftWidget.getId());

        List<Widget> readWidgets = widgetStorage.read(-15, 16, 15, -16);

        assertThat(readWidgets).hasSize(3);
        assertThat(readWidgets.get(0).getId()).isEqualTo(upperRightWidget.getId());
        assertThat(readWidgets.get(1).getId()).isEqualTo(lowerRightWidget.getId());
        assertThat(readWidgets.get(2).getId()).isEqualTo(lowerLeftWidget.getId());
    }
}
