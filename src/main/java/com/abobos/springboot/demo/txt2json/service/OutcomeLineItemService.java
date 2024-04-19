package com.abobos.springboot.demo.txt2json.service;

import static java.lang.String.format;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OutcomeLineItemService {

    private static final int UUID_INDEX = 0;
    private static final int ID_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int LIKES_INDEX = 3;
    private static final int TRANSPORT_INDEX = 4;
    private static final int AVG_SPEED_INDEX = 5;
    private static final int TOP_SPEED_INDEX = 6;

    private static final int LINE_FIELD_COUNT = 7;

    Logger logger = LoggerFactory.getLogger(OutcomeLineItemService.class);

    private final OutcomeItemService outcomeItemService;

    @Autowired
    public OutcomeLineItemService(OutcomeItemService outcomeItemService) {
        this.outcomeItemService = outcomeItemService;
    }

    OutcomeLineItem buildOutcomeLineItem(String fileLine, long lineNumber, final boolean enableFileValidation) {

        final String[] cells = fileLine.trim().split("\\|");
        if (cells.length != LINE_FIELD_COUNT && enableFileValidation) {
            // bad line
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, format("Error at line # %s '%s': Expecting 7 fields but got %s", lineNumber, fileLine, cells.length));
        }

        final OutcomeLineItem outcomeLineItem = new OutcomeLineItem();

        for (int idx = 0; idx < cells.length; idx++) {

            switch (idx) {

                case UUID_INDEX:
                    outcomeItemService.extractUuid("uuid", lineNumber, cells[idx], enableFileValidation);
                    break;

                case ID_INDEX:
                    outcomeItemService.extractString("id", lineNumber, cells[idx], enableFileValidation);
                    break;

                case NAME_INDEX:
                    outcomeLineItem.setName( outcomeItemService.extractString("name", lineNumber, cells[idx], enableFileValidation));
                    break;

                case LIKES_INDEX:
                    outcomeItemService.extractString("likes", lineNumber, cells[idx], enableFileValidation);
                    break;

                case TRANSPORT_INDEX:
                    outcomeLineItem.setTransport( outcomeItemService.extractString("transport", lineNumber, cells[idx], enableFileValidation));
                    break;

                case AVG_SPEED_INDEX:
                    outcomeItemService.extractDouble("avgSpeed", lineNumber, cells[idx], enableFileValidation);
                    break;

                case TOP_SPEED_INDEX:
                    outcomeLineItem.setTopSpeed( outcomeItemService.extractDouble("topSpeed", lineNumber, cells[idx], enableFileValidation));
                    break;

                default:
                    logger.warn("Unexpected field index {}", idx);
            }
        }

        return outcomeLineItem;
    }


}
