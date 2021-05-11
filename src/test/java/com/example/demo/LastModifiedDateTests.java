package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;
import com.example.demo.widgetstorages.IWidgetStorage;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;

public class LastModifiedDateTests {
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
    public void shouldSetLastModifiedDateWhenCreate() {
        WidgetCreateRequest widgetCreateRequest = new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        Widget widget = widgetStorage.create(widgetCreateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldUpdateLastModifiedDateOnChangeX(boolean same) {
        WidgetUpdateRequest widgetUpdateRequest = new WidgetUpdateRequest() {{
            setX(same ? 1 : 2);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.update(widget.getId(), widgetUpdateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldUpdateLastModifiedDateOnChangeY(boolean same)  {
        WidgetUpdateRequest widgetUpdateRequest = new WidgetUpdateRequest() {{
            setY(same ? 1 : 2);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.update(widget.getId(), widgetUpdateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldUpdateLastModifiedDateOnChangeZ(boolean same)  {
        WidgetUpdateRequest widgetUpdateRequest = new WidgetUpdateRequest() {{
            setZ(same ? 1 : 2);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.update(widget.getId(), widgetUpdateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldUpdateLastModifiedDateOnChangeWidth(boolean same)  {
        WidgetUpdateRequest widgetUpdateRequest = new WidgetUpdateRequest() {{
            setWidth(same ? 1 : 2);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.update(widget.getId(), widgetUpdateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldUpdateLastModifiedDateOnChangeHeight(boolean same)  {
        WidgetUpdateRequest widgetUpdateRequest = new WidgetUpdateRequest() {{
            setHeight(same ? 1 : 2);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.update(widget.getId(), widgetUpdateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @Test
    public void shouldUpdateLastModifiedDateOnShift()  {
        WidgetCreateRequest widgetCreateRequest = new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }};

        ZonedDateTime before = ZonedDateTime.now();
        widgetStorage.create(widgetCreateRequest);
        ZonedDateTime after = ZonedDateTime.now();

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @Test
    public void shouldNotUpdateLastModifiedDateWhenOtherUpperShifted() {
        ZonedDateTime dateBeforeShift = widget.getLastModifiedDate();
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
            setZ(2);
            setWidth(1);
            setHeight(1);
        }});

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isEqualTo(dateBeforeShift);
    }

    @Test
    public void shouldNotUpdateLastModifiedDateWhenOtherLowerShifted() {
        @SuppressWarnings("SpellCheckingInspection")
        Widget unshiftedWidget = widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(3);
            setWidth(1);
            setHeight(1);
        }});
        ZonedDateTime dateBeforeShift = unshiftedWidget.getLastModifiedDate();

        widgetStorage.create(new WidgetCreateRequest() {{
            setX(1);
            setY(1);
            setZ(1);
            setWidth(1);
            setHeight(1);
        }});

        Widget readWidget = widgetStorage.read(unshiftedWidget.getId());
        assertThat(readWidget.getLastModifiedDate()).isEqualTo(dateBeforeShift);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void shouldNotUpdateLastModifiedDateOnReading(boolean readAll) {
        ZonedDateTime dateBeforeReading = widget.getLastModifiedDate();

        if (readAll)
            widgetStorage.read();
        else
            widgetStorage.read(widget.getId());

        Widget readWidget = widgetStorage.read(widget.getId());
        assertThat(readWidget.getLastModifiedDate()).isEqualTo(dateBeforeReading);
    }
}
