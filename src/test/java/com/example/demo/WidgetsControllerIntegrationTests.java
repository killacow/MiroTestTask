package com.example.demo;

import static org.assertj.core.api.Assertions.*;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo.models.Widget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SuppressWarnings({"unused", "ConstantConditions"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WidgetsControllerIntegrationTests {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        template.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        this.base = new URL("http://localhost:" + port + "/widgets");
    }

    @Test
    public void shouldCreate() {
        ZonedDateTime before = ZonedDateTime.now();
        ResponseEntity<Widget> response = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 100;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class);
        ZonedDateTime after = ZonedDateTime.now();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget widget = response.getBody();
        assertThat(widget.getId()).isNotNull();
        assertThat(widget.getX()).isEqualTo(1);
        assertThat(widget.getY()).isEqualTo(2);
        assertThat(widget.getZ()).isEqualTo(100);
        assertThat(widget.getWidth()).isEqualTo(4);
        assertThat(widget.getHeight()).isEqualTo(5);
        assertThat(widget.getLastModifiedDate()).isBetween(before, after);
    }

    @Test
    public void shouldCreateWithoutZ() {
        ZonedDateTime before = ZonedDateTime.now();
        ResponseEntity<Widget> response = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class);
        ZonedDateTime after = ZonedDateTime.now();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget widget = response.getBody();
        assertThat(widget.getId()).isNotNull();
        assertThat(widget.getX()).isEqualTo(1);
        assertThat(widget.getY()).isEqualTo(2);
        assertThat(widget.getWidth()).isEqualTo(4);
        assertThat(widget.getHeight()).isEqualTo(5);
        assertThat(widget.getLastModifiedDate()).isBetween(before, after);
    }

    @Test
    public void shouldReadOne() {
        Widget createdWidget = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 200;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget> response = template.getForEntity(base.toString() + "/" + createdWidget.getId().toString(), Widget.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget readWidget = response.getBody();
        assertThat(readWidget).usingRecursiveComparison().isEqualTo(createdWidget);
    }

    @Test
    public void shouldReturn404WhenReadingNotExisting() {
        template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 200;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget> response = template.getForEntity(base.toString() + "/" + UUID.randomUUID(), Widget.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReadAll() {
        Widget createdWidget = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 200;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget[]> response = template.getForEntity(base.toString(), Widget[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget[] readWidgets = response.getBody();
        List<Widget> widgetsWithCreatedId = Arrays.stream(readWidgets).filter(widget -> widget.getId().equals(createdWidget.getId())).collect(Collectors.toList());
        assertThat(widgetsWithCreatedId).hasSize(1);
        assertThat(widgetsWithCreatedId.get(0)).usingRecursiveComparison().isEqualTo(createdWidget);
    }

    @Test
    public void shouldUpdate() {
        Widget createdWidget = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 300;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ZonedDateTime before = ZonedDateTime.now();
        ResponseEntity<Widget> response = template.exchange(base.toString() + "/" + createdWidget.getId().toString(), HttpMethod.PATCH, new HttpEntity<>(new Object() {
            public final int x = 10;
            public final int y = 20;
            public final int z = 2000;
            public final int width = 40;
            public final int height = 50;
        }), Widget.class);
        ZonedDateTime after = ZonedDateTime.now();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget updatedWidget = response.getBody();
        assertThat(updatedWidget.getId()).isEqualTo(createdWidget.getId());
        assertThat(updatedWidget.getX()).isEqualTo(10);
        assertThat(updatedWidget.getY()).isEqualTo(20);
        assertThat(updatedWidget.getZ()).isEqualTo(2000);
        assertThat(updatedWidget.getWidth()).isEqualTo(40);
        assertThat(updatedWidget.getHeight()).isEqualTo(50);
        assertThat(updatedWidget.getLastModifiedDate()).isBetween(before, after);
    }

    @Test
    public void shouldReturn404WhenUpdatingNotExisting() {
        template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 300;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget> response = template.exchange(base.toString() + "/" + UUID.randomUUID(), HttpMethod.PATCH, new HttpEntity<>(new Object() {
            public final int x = 10;
            public final int y = 20;
            public final int z = 2000;
            public final int width = 40;
            public final int height = 50;
        }), Widget.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldDelete() {
        Widget createdWidget = template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 400;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget> response = template.exchange(base.toString() + "/" + createdWidget.getId().toString(), HttpMethod.DELETE, null, Widget.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Widget[] foundWidgets = template.getForObject(base.toString(), Widget[].class);
        assertThat(foundWidgets).noneMatch(widget -> widget.getId().equals(createdWidget.getId()));
    }

    @Test
    public void shouldReturn404WhenDeletingNotExisting() {
        template.postForEntity(base.toString(), new Object() {
            public final int x = 1;
            public final int y = 2;
            public final int z = 400;
            public final int width = 4;
            public final int height = 5;
        }, Widget.class).getBody();

        ResponseEntity<Widget> response = template.exchange(base.toString() + "/" + UUID.randomUUID(), HttpMethod.DELETE, null, Widget.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
