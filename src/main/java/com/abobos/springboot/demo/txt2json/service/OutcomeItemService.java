package com.abobos.springboot.demo.txt2json.service;

import static java.lang.String.format;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OutcomeItemService {

    UUID extractUuid(final String fieldName, final long lineNumber, final String input, final boolean validateField) {
        final String clean = input.trim();
        try {
            return UUID.fromString(clean);
        } catch (Exception e) {
            if (validateField) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        format("Error at line # %s: Cannot convert '%s' into a valid %s", lineNumber, clean, fieldName));
            }
        }

        return null;
    }

    Double extractDouble(final String fieldName, final long lineNumber, final String input, final boolean enableFileValidation) {
        final String clean = input.trim();
        try {
            return Double.valueOf(clean);
        } catch (Exception e) {
            if (enableFileValidation) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        format("Error at line # %s: Cannot convert '%s' into a valid %s", lineNumber, clean, fieldName));
            }
        }

        return null;
    }

    String extractString(final String fieldName, final long lineNumber, final String input, final boolean enableFileValidation) {
        final String clean = input.trim();
        if (clean.isEmpty()) {

            if (enableFileValidation) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        format("Error at line # %s: Cannot convert '%s' into a valid %s", lineNumber, clean, fieldName));
            }

            return null;
        }

        return clean;
    }
}
