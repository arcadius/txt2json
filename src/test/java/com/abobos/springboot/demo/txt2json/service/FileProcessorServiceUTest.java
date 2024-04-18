package com.abobos.springboot.demo.txt2json.service;

import static com.abobos.springboot.demo.txt2json.AssertOutcome.assertOutcome;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class FileProcessorServiceUTest {

    private static FileProcessorService serv;

    @BeforeAll
    static void beforeAll() {
        serv = new FileProcessorService(new OutcomeLineItemService(new OutcomeItemService()));
    }

    @Test
    void testParseGoodData() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1 
                                
                3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5 
                                
                1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3
                """;

        final List<OutcomeLineItem> res = serv.parse(new BufferedReader(new StringReader(inputFile)), true);

        assertThat(res, hasSize(3));

        final OutcomeLineItem first = res.get(0);
        final OutcomeLineItem second = res.get(1);
        final OutcomeLineItem third = res.get(2);

        assertOutcome(first,
                "John Smith", "Rides A Bike",
                12.1D);

        assertOutcome(second,
                "Mike Smith", "Drives an SUV", 95.5D);

        assertOutcome(third,
                "Jenny Walters", "Rides A Scooter",
                15.3D);

    }

    @Test
    void testParseTooFewColumns() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith| 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 1 '18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|': Expecting 7 fields but got 3"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseTooFewColumnsNoValidation() throws IOException {
        final String inputFile = """
                1X1D14|John Smith| 
                """;

        final List<OutcomeLineItem> res = serv.parse(new BufferedReader(new StringReader(inputFile)), false);
        assertThat(res, hasSize(1));
        assertOutcome(res.get(0), null, null, null);
    }

    @Test
    void testParseTooManyColumns() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1|xxx
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 1 '18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1|xxx': Expecting 7 fields but got 8"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseTooManyColumnsNoValidation() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1|xxx
                """;

        final List<OutcomeLineItem> res = serv.parse(new BufferedReader(new StringReader(inputFile)), false);

        assertThat(res, hasSize(1));

        assertOutcome(res.get(0), "John Smith", "Rides A Bike", 12.1);
    }

    @Test
    void testParseInvalidUuid() {
        final String inputFile = """
                                
                18148426-89e1-11ee-b9d1-0242ac120002XX|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 2: Cannot convert '18148426-89e1-11ee-b9d1-0242ac120002XX' into a valid uuid"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseInvalidUuidNoValidation() throws IOException {
        final String inputFile = """
                                
                18148426-89e1-11ee-b9d1-0242ac120002XX|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1 
                """;
        final List<OutcomeLineItem> res = serv.parse(new BufferedReader(new StringReader(inputFile)), false);

        assertOutcome(res.get(0), "John Smith", "Rides A Bike", 12.1);

    }

    @Test
    void testParseMissingUuid() throws IOException {
        final String inputFile = """
                                
                  |1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 2: Cannot convert '' into a valid uuid"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseMissingId() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002| |John Smith|Likes Apricots|Rides A Bike|6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert '' into a valid id"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseMissingName() throws IOException {
        final String inputFile = """
                                
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|  |Likes Apricots|Rides A Bike|6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 2: Cannot convert '' into a valid name"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    @Test
    void testParseMissingLikes() throws IOException {
        final String inputFile = """
                                
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|  |Rides A Bike|6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 2: Cannot convert '' into a valid likes"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseEmptyTransportation() {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|     |6.2|12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert '' into a valid transport"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


    @Test
    void testParseInvalidTopSpeed() throws IOException {
        final String inputFile = """
                                
                                
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|1xx2.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 3: Cannot convert '1xx2.1' into a valid topSpeed"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testParseInvalidTopSpeedNoValidationSouldReturnNullTopSpeed() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|1xx2.1 
                """;

        final List<OutcomeLineItem> resp = serv.parse(new BufferedReader(new StringReader(inputFile)), false);

        assertOutcome(resp.get(0), "John Smith", "Rides A Bike", null);

    }


    @Test
    void testParseInvalidAvgSpeed() throws IOException {
        final String inputFile = """
                18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike||12.1 
                """;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            serv.parse(new BufferedReader(new StringReader(inputFile)), true);
        });

        assertThat(exception.getReason(), is("Error at line # 1: Cannot convert '' into a valid avgSpeed"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }


}