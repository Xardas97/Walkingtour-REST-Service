package com.endava.mmarko.pia.controllers;

import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

class ControllerTestUtil {
    static final MediaType JSON_CONTENT_TYPE = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8
    );
}
