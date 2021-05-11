package com.example.demo.widgetstorages;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface IWidgetStorage {
    Widget create(WidgetCreateRequest widgetCreateRequest);
    Widget read(UUID id);
    List<Widget> read();
    Widget update(UUID id, WidgetUpdateRequest widgetUpdateRequest);
    Widget delete(UUID id);
}