package com.abobos.springboot.demo.txt2json.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class OutcomeItemServiceUTest {

    private OutcomeItemService service;

    @BeforeEach
    void beforeEach() {
        service = new OutcomeItemService();
    }

    @Test
    void extractUuidIsOk() {
        final UUID actual = service.extractUuid("uuid", 1, "    1afb6f5d-a7c2-4311-a92d-974f3180ff5e  ", true);
        assertThat(actual.toString(), is("1afb6f5d-a7c2-4311-a92d-974f3180ff5e"));
    }

    @Test
    void extractUuidThrowsExceptionWhenUuidIsInvalid() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.extractUuid("uuid", 1, " XXX1afb6f5d-a7c2-4311-a92d-974f3180ff5e", true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert 'XXX1afb6f5d-a7c2-4311-a92d-974f3180ff5e' into a valid uuid"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void extractUuidReturnsNullWhenUuidIsInvalidAndValidationIsDisabled() {
        UUID actual = service.extractUuid("uuid", 1, " XXX1afb6f5d-a7c2-4311-a92d-974f3180ff5e", false);
        assertThat(actual, is(nullValue()));
    }

    @Test
    void extractDoubleIsOk() {
        final Double actual = service.extractDouble("topSpeed", 1, "    5.5  ", true);
        assertThat(actual, is(5.5D));
    }

    @Test
    void extractDoubleThrowsExceptionWhenDoubleIsInvalid() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.extractUuid("topSpeed", 1, " YY55YY", true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert 'YY55YY' into a valid topSpeed"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void extractDoubleReturnsNullWhenDoubleIsInvalidAndValidationIsDisabled() {
        UUID actual = service.extractUuid("topSpeed", 1, " Y55Y", false);
        assertThat(actual, is(nullValue()));
    }


    @Test
    void extractStringIsOk() {
        final String actual = service.extractString("name", 1, "    Joe Smith  ", true);
        assertThat(actual, is("Joe Smith"));
    }

    @Test
    void extractStringThrowsExceptionWhenStringIsEmpty() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.extractUuid("name", 1, " ", true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert '' into a valid name"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void extractStringReturnsNullWhenStringIsEmptyAndValidationIsDisabled() {
        UUID actual = service.extractUuid("name", 1, " ", false);
        assertThat(actual, is(nullValue()));
    }
}