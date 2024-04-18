package com.abobos.springboot.demo.txt2json.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OutcomeLineItemServiceUTest {

    private static final String GOOD_LINE = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1";
    private static final String TOO_MANY_FIELDS_ON_LINE = GOOD_LINE + "|aaaaa";
    private static final String SINGLE_FIELD_LINE = "18148426-89e1-11ee-b9d1-0242ac120002|";

    @Mock
    private OutcomeItemService outcomeItemService;

    @InjectMocks
    private OutcomeLineItemService service;


    @Test
    void buildOutcomeLineItemForGoodLine() {
        OutcomeLineItem lineItem = service.buildOutcomeLineItem(GOOD_LINE, 1, true);
        assertThat(lineItem, is(notNullValue()));

        verifyAllCallsAreOk(true);
    }

    @Test
    void buildOutcomeLineItemShouldThrowsExceptionWhenTooManyFieldsAndValidationIsEnabled() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.buildOutcomeLineItem(TOO_MANY_FIELDS_ON_LINE, 1, true);
        });

        assertThat(exception.getReason(), is("Error at line # 1 '18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1|aaaaa': Expecting 7 fields but got 8"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        verifyNoMoreInteractions(outcomeItemService);
    }

    @Test
    void buildOutcomeLineItemShouldCreateLineItemWhenTooManyFieldsAndValidationIsDisabled() {
        OutcomeLineItem lineItem = service.buildOutcomeLineItem(TOO_MANY_FIELDS_ON_LINE, 1, false);
        assertThat(lineItem, is(notNullValue()));
        verifyAllCallsAreOk(false);
    }


    @Test
    void buildOutcomeLineItemShouldThrowsExceptionWhenSingleFieldAndValidationIsEnabled() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.buildOutcomeLineItem(SINGLE_FIELD_LINE, 1, true);
        });

        assertThat(exception.getReason(), is("Error at line # 1 '18148426-89e1-11ee-b9d1-0242ac120002|': Expecting 7 fields but got 1"));
        assertThat(exception.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        verifyNoMoreInteractions(outcomeItemService);
    }

    @Test
    void buildOutcomeLineItemShouldCreateLineItemWhenSingleFieldLineAndValidationIsDisabled() {
        OutcomeLineItem lineItem = service.buildOutcomeLineItem(SINGLE_FIELD_LINE, 1, false);
        assertThat(lineItem, is(notNullValue()));
        verify(outcomeItemService).extractUuid("uuid", 1L, "18148426-89e1-11ee-b9d1-0242ac120002", false);
        verifyNoMoreInteractions(outcomeItemService);
    }


    private void verifyAllCallsAreOk(final boolean validateFile) {
        verify(outcomeItemService).extractUuid("uuid", 1L, "18148426-89e1-11ee-b9d1-0242ac120002", validateFile);
        verify(outcomeItemService).extractString("id", 1L, "1X1D14", validateFile);
        verify(outcomeItemService).extractString("name", 1L, "John Smith", validateFile);
        verify(outcomeItemService).extractString("likes", 1L, "Likes Apricots", validateFile);
        verify(outcomeItemService).extractString("transport", 1L, "Rides A Bike", validateFile);
        verify(outcomeItemService).extractDouble("avgSpeed", 1L, "6.2", validateFile);
        verify(outcomeItemService).extractDouble("topSpeed", 1L, "12.1", validateFile);

        verifyNoMoreInteractions(outcomeItemService);
    }
}