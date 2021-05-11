package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadingWithPagingTests {

    private WidgetStorageWithPagingAndFiltering widgetStorage;

    @BeforeEach
    public void setUp() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    }

    @Test
    public void simple() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 50);

        List<Widget> readWidgets = widgetStorage.read(25, 20);

        assertThat(readWidgets).hasSize(20);
        for (int i = 25; i < 25 + 20; i++) {
            assertThat(readWidgets.get(i - 25).getZ()).isEqualTo(i);
        }
    }

    @Test
    public void shouldSkip0Take10WhenNotSpecified() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 50);

        List<Widget> readWidgets = widgetStorage.read();

        assertThat(readWidgets).hasSize(10);
        for (int i = 0; i < 10; i++) {
            assertThat(readWidgets.get(i).getZ()).isEqualTo(i);
        }
    }

    @Test
    public void shouldSkip0WhenNotSpecified() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 50);

        List<Widget> readWidgets = widgetStorage.read(null, 5);

        assertThat(readWidgets).hasSize(5);
        for (int i = 0; i < 5; i++) {
            assertThat(readWidgets.get(i).getZ()).isEqualTo(i);
        }
    }

    @Test
    public void shouldTake10WhenNotSpecified() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 50);

        List<Widget> readWidgets = widgetStorage.read(15, null);

        assertThat(readWidgets).hasSize(10);
        for (int i = 15; i < 15+10; i++) {
            assertThat(readWidgets.get(i-15).getZ()).isEqualTo(i);
        }
    }

    @Test
    public void shouldIgnoreZGap() {
        WidgetStorageHelper.addWidgets(widgetStorage, 0, 20);
        WidgetStorageHelper.addWidgets(widgetStorage, 25, 10);

        List<Widget> readWidgets = widgetStorage.read(15, 10);

        assertThat(readWidgets).hasSize(10);
        for (int i = 15; i < 20; i++) {
            assertThat(readWidgets.get(i - 15).getZ()).isEqualTo(i);
        }
        for (int i = 25; i < 30; i++) {
            assertThat(readWidgets.get(i - 25 + 5).getZ()).isEqualTo(i);
        }
    }
}
