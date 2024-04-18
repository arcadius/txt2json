package com.abobos.springboot.demo.txt2json.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileProcessorService {
    private final OutcomeLineItemService outcomeItemBuilder;

    @Autowired
    public FileProcessorService(OutcomeLineItemService outcomeItemBuilder) {
        this.outcomeItemBuilder = outcomeItemBuilder;
    }

    public List<OutcomeLineItem> parse(final MultipartFile multipartFile, final boolean enableFileValidation) {
        final String fileType = multipartFile.getContentType();
        if (fileType == null || !fileType.startsWith("text/plain")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only text/plain files are accepted");
        }

        try (final BufferedReader bis = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            return parse(bis, enableFileValidation);
        } catch (IOException e) {
            throw new RuntimeException(e);//TODO
        }

    }

    protected List<OutcomeLineItem> parse(final BufferedReader reader, final boolean enableFileValidation) throws IOException {
        List<OutcomeLineItem> outcomeList = new ArrayList<>();
        int lineNumber = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber += 1;
            line = line.trim();
            if (line.isEmpty()) {
                continue;//skip empty lines
            }

            outcomeList.add(outcomeItemBuilder.buildOutcomeLineItem(line, lineNumber, enableFileValidation));
        }

        return outcomeList;
    }
}
