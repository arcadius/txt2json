package com.abobos.springboot.demo.txt2json.api;

import java.io.IOException;
import java.util.List;

import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import com.abobos.springboot.demo.txt2json.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class Txt2JsonApi {

    @Autowired
    private FileProcessorService fileProcessorService;

    @RequestMapping(value = "txt2json", method = RequestMethod.POST)
    public ResponseEntity<?> txt2json(@RequestParam("file") MultipartFile multipartFile,
                                      @RequestHeader(name = "enableFileValidation", required = false, defaultValue = "true") boolean enableFileValidation) throws IOException {

        final List<OutcomeLineItem> res = fileProcessorService.parse(multipartFile, enableFileValidation);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(
                "Content-Disposition",
                "attachment; filename=\"Outcome.json\"");

        return ResponseEntity.ok().headers(headers).body(res);
    }

}
