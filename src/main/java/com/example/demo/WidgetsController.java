package com.example.demo;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;
import com.example.demo.widgetstorages.WidgetStorageWithPagingAndFiltering;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/widgets")
public class WidgetsController {

    private final WidgetStorageWithPagingAndFiltering widgetStorage;

    WidgetsController() {
        widgetStorage = new WidgetStorageWithPagingAndFiltering();
    } // TODO: DI

    @PostMapping
    public Widget create(@RequestBody WidgetCreateRequest widgetCreateRequest) {
        try {
            return widgetStorage.create(widgetCreateRequest);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public Widget read(@PathVariable("id") UUID id) {
        Widget widget = widgetStorage.read(id);
        if (widget == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return widget;
    }

    @GetMapping
    public List<Widget> read(@RequestParam(required = false) Integer skip,
                             @RequestParam(required = false) Integer take,
                             @RequestParam(required = false) Integer leftBound,
                             @RequestParam(required = false) Integer rightBound,
                             @RequestParam(required = false) Integer upperBound,
                             @RequestParam(required = false) Integer lowerBound) {
        try {
            return widgetStorage.read(skip, take, leftBound, rightBound, upperBound, lowerBound);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}")
    public Widget update(@PathVariable("id") UUID id, @RequestBody WidgetUpdateRequest widgetUpdateRequest) {
        try {
            Widget widget = widgetStorage.update(id, widgetUpdateRequest);
            if (widget == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return widget;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        Widget widget = widgetStorage.delete(id);
        if (widget == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}

