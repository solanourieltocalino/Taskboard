package com.jbk.taskboard.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Dummy controller used in ApiExceptionHandlerTest to trigger exceptions.
 */
@Profile("webmvc-test")
@Validated
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
     * Throw ConstraintViolationException
     * 
     * @param size
     * @return
     */
    @GetMapping("/constraint")
    public String constraint(@RequestParam(defaultValue = "20") @Positive(message = "Size must be >= 1") int size) {
        return "ok:" + size;
    }

    /**
     * Throw IllegalArgumentException
     * 
     * @return
     */
    @GetMapping("/illegal-arg")
    public String illegalArg() {
        throw new IllegalArgumentException("Bad arg");
    }

    /**
     * Throw HttpMessageNotReadableException (malformed JSON)
     * 
     * @param body
     * @return
     */
    @PostMapping(value = "/malformed-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String malformedJson(@RequestBody CreatePayload body) {
        return "ok";
    }

    /**
     * Throw MissingServletRequestParameterException
     * 
     * @param q
     * @return
     */
    @GetMapping("/missing")
    public String missing(@RequestParam String q) {
        return "ok:" + q;
    }

    /**
     * Throw HttpRequestMethodNotSupportedException
     * 
     * @return
     */
    @GetMapping("/only-get")
    public String onlyGet() {
        return "ok";
    }

    /**
     * Produce application/json content type
     * 
     * @return
     */
    @GetMapping(value = "/produce-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String produceJson() {
        return "{\"ok\":true}";
    }

    /**
     * Throw DataIntegrityViolationException
     * 
     * @return
     */
    @GetMapping("/data-integrity")
    public String dataIntegrity() {
        throw new DataIntegrityViolationException("violation");
    }

    /**
     * Throw generic RuntimeException
     * 
     * @return
     */
    @GetMapping("/generic")
    public String generic() {
        throw new RuntimeException("boom");
    }

    /**
     * Throw WebhookClientException
     * 
     * @param body
     * @return
     */
    @PostMapping(value = "/webhook-fail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String webhookFail(@RequestBody CreatePayload body) {
        throw new WebhookClientException("Failed to send webhook event");
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
