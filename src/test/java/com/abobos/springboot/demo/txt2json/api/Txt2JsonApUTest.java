package com.abobos.springboot.demo.txt2json.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import com.abobos.springboot.demo.txt2json.service.FileProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class Txt2JsonApUTest {

    @Mock
    private FileProcessorService fileProcessorService;


    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private Txt2JsonApi txt2JsonApi;


    @Test
    void givenAllOk_thenTxt2jsonReturnsOk() throws IOException {
        final ResponseEntity<?> response = txt2JsonApi.txt2json(multipartFile, true);

        verify(fileProcessorService).parse(multipartFile, true);
        assertThat(response, is(notNullValue()));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentDisposition().getFilename(), is("Outcome.json"));
        assertThat(response.getHeaders().getContentDisposition().getType(), is("attachment"));

        verifyNoMoreInteractions(fileProcessorService,multipartFile);
    }
}