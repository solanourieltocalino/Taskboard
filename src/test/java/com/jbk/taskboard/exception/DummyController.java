package com.jbk.taskboard.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Profile("webmvc-test")
@RestController
@RequestMapping("/dummy")
public class DummyController {

    @GetMapping("/notfound")
    public String notFound() {
        throw new NotFoundException("User not found");
    }

    // Throw MethodArgumentNotValidException
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validateBody(@Valid @RequestBody CreatePayload body) {
        return "ok";
    }

    // Throw MethodArgumentTypeMismatchException automatically
    @GetMapping("/type-mismatch")
    public String typeMismatch(@RequestParam int size) {
        return "ok:" + size;
    }

    @GetMapping("/conflict")
    public String conflict() {
        throw new BusinessRuleException("Duplicate email");
    }

    // Minimum payload to validate @NotBlank
    public static class CreatePayload {
        @NotBlank(message = "name is required")
        public String name;

        public CreatePayload() {
        }

        public CreatePayload(String name) {
            this.name = name;
        }
    }
}
