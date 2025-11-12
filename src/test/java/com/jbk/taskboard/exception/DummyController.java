package com.jbk.taskboard.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Dummy controller used in ApiExceptionHandlerTest to trigger exceptions.
 */
@Profile("webmvc-test")
@RestController
@RequestMapping("/dummy")
public class DummyController {

    /**
     * Throw NotFoundException
     * 
     * @return
     */
    @GetMapping("/notfound")
    public String notFound() {
        throw new NotFoundException("User not found");
    }

    /**
     * Validate request body to trigger MethodArgumentNotValidException
     * 
     * @param body
     * @return
     */
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String validateBody(@Valid @RequestBody CreatePayload body) {
        return "ok";
    }

    /**
     * Trigger MethodArgumentTypeMismatchException
     * 
     * @param size
     * @return
     */
    @GetMapping("/type-mismatch")
    public String typeMismatch(@RequestParam int size) {
        return "ok:" + size;
    }

    /**
     * Throw BusinessRuleException
     * 
     * @return
     */
    @GetMapping("/conflict")
    public String conflict() {
        throw new BusinessRuleException("Duplicate email");
    }

    /**
     * Payload class for body validation
     */
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
