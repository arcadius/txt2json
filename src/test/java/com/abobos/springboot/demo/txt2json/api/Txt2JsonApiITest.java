package com.abobos.springboot.demo.txt2json.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import com.abobos.springboot.demo.txt2json.AssertOutcome;
import com.abobos.springboot.demo.txt2json.Txt2JsonApplication;
import com.abobos.springboot.demo.txt2json.interceptor.AccessLogger;
import com.abobos.springboot.demo.txt2json.model.OutcomeLineItem;
import com.abobos.springboot.demo.txt2json.persistence.entity.AccessLog;
import com.abobos.springboot.demo.txt2json.persistence.repository.AccessLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Txt2JsonApplication.class)
@EnableWireMock({
        @ConfigureWireMock(name = "geoIpService", property = "app.geoIpApiBaseUrl")
})
class Txt2JsonApiITest {

    @LocalServerPort
    protected int port;

    @InjectWireMock("geoIpService")
    private WireMockServer wiremock;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Test
    void givenThatEntryFileIsOkAndCountryIsGbAndOrganisationIsBt_thenGoodJsonResponseFileShouldBeReturned() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));

        uploadFileAndCheckOk("EntryFile.txt", true, "John Smith", "Rides A Bike", 12.1D);
    }


    @Test
    void givenThatEntryFileIsEmptyAndCountryIsGbAndOrganisationIsBt_thenEmptyJsonResponseFileShouldBeReturned() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));

        final File fi = getOneFile("EntryFile-empty.txt");

        final ResponseEntity<String> response = postFileEnableFileValidation(fi);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentDisposition().getFilename(), is("Outcome.json"));
        assertThat(response.getHeaders().getContentDisposition().getType(), is("attachment"));
        assertThat(valueOf(response.getHeaders().getContentType()), is("application/json"));
        assertThat(response.getBody(), is("[]"));
    }

    @Test
    void givenThatEntryFileIsOKAndCountryIsUsa_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("US", "BT")));


        uploadAndCheckForbidden("Country US is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileIsOkAndCountryIsChina_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("CN", "BT")));


        uploadAndCheckForbidden("Country CN is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileIsOkAndCountryIsSpain_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("ES", "BT")));


        uploadAndCheckForbidden("Country ES is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileIsOkAndOrganisationIsAws_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "AMAZON")));


        uploadAndCheckForbidden("Organisation AMAZON is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileIsOkAndOrganisationIsGoogle_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "GOOGLE")));


        uploadAndCheckForbidden("Organisation GOOGLE is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileIsOkAndOrganisationIsAzure_thenResponseStatusShouldBeForbidden() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "MICROSOFT")));//note that sometimes, microsoft is behind akamai

        uploadAndCheckForbidden("Organisation MICROSOFT is not allowed to access this service");
    }

    @Test
    void givenThatEntryFileHasInvalidUuidAndFileValidationIsEnabled_thenResponseStatusShouldBeBadRequest() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadAndCheckBadRequest("EntryFile-invalid-uuid.txt", "Error at line # 1: Cannot convert 'XX18148426-89e1-11ee-b9d1-0242ac120002' into a valid uuid");
    }

    @Test
    void givenThatEntryFileHasInvalidUuidAndFileValidationIsDisabled_thenResponseStatusShouldBeOk() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadFileAndCheckOk("EntryFile-invalid-uuid.txt", false, "John Smith", "Rides A Bike", 12.1D);
    }

    @Test
    void givenThatEntryFileHasEmptyNameAndFileValidationIsEnabled_thenResponseStatusShouldBeBadRequest() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadAndCheckBadRequest("EntryFile-empty-name.txt", "Error at line # 1: Cannot convert '' into a valid name");
    }

    @Test
    void givenThatEntryFileHasEmptyNameAndFileValidationIsDisabled_thenResponseStatusShouldBeOk() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadFileAndCheckOk("EntryFile-empty-name.txt", false, null, "Rides A Bike", 12.1D);
    }

    @Test
    void givenThatEntryFileHasInvalidTopSpeedAndFileValidationIsEnabled_thenResponseStatusShouldBeBadRequest() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadAndCheckBadRequest("EntryFile-invalid-top-speed.txt", "Error at line # 1: Cannot convert '1x2.1' into a valid topSpeed");
    }

    @Test
    void givenThatEntryFileHasInvalidTopSpeedAndFileValidationIsDisabled_thenResponseStatusShouldBeOk() throws IOException {

        wiremock.stubFor(get(urlPathMatching("/.*")).willReturn
                (geoIpResponseBuilder("GB", "BT")));//note that sometimes, microsoft is behind akamai

        uploadFileAndCheckOk("EntryFile-invalid-top-speed.txt", false, "John Smith", "Rides A Bike", null);
    }


    private void uploadAndCheckForbidden(final String expectedMessage) throws IOException {
        final File fi = getOneFile("EntryFile.txt");

        final ResponseEntity<String> response = postFileEnableFileValidation(fi);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(response.getHeaders().getContentDisposition().getFilename(), is(nullValue()));
        final String actualBody = response.getBody();
        assertThat(actualBody, containsString("403"));
        assertThat(actualBody, containsString("Forbidden"));
        assertThat(actualBody, containsString(expectedMessage));
        checkAccessLog(response, 403);
    }

    private void uploadFileAndCheckOk(final String fileName, final boolean enableFileValidation,
                                      final String outcomeName, final String outcomeTransport, final Double outcomeTopSpeed) throws IOException {
        final File fi = getOneFile(fileName);

        final ResponseEntity<String> response = postFile(fi, enableFileValidation);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentDisposition().getFilename(), is("Outcome.json"));
        assertThat(response.getHeaders().getContentDisposition().getType(), is("attachment"));
        assertThat(valueOf(response.getHeaders().getContentType()), is("application/json"));
        final String actualBody = response.getBody();
        assertThat(actualBody, is(notNullValue()));
        final OutcomeLineItem[] outcomeLineItems = objectMapper.readValue(actualBody, OutcomeLineItem[].class);
        assertThat(outcomeLineItems.length, is(1));
        AssertOutcome.assertOutcome(outcomeLineItems[0], outcomeName, outcomeTransport, outcomeTopSpeed);
        checkAccessLog(response, 200);
    }

    private void uploadAndCheckBadRequest(final String fileName, final String message) throws IOException {

        final File fi = getOneFile(fileName);
        final ResponseEntity<String> response = postFile(fi, true);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getHeaders().getContentDisposition().getFilename(), is(nullValue()));
        final String actualBody = response.getBody();
        assertThat(actualBody, containsString("400"));
        assertThat(actualBody, containsString("Bad Request"));
        assertThat(actualBody, containsString(message));
        checkAccessLog(response, 400);
    }

    private void checkAccessLog(final ResponseEntity<String> response, final Integer statusCode) {
        final String requestId = response.getHeaders().getFirst(AccessLogger.DEMO_REQUEST_ID);
        assertThat(requestId, is(notNullValue()));
        final UUID uuid = UUID.fromString(requestId);
        final AccessLog accessLog = accessLogRepository.findByRequestId(uuid);
        assertThat(accessLog, is(notNullValue()));
        assertThat(accessLog.getTimeLapsed(), is(notNullValue()));
        assertThat(accessLog.getRequestStartTimestamp(), is(notNullValue()));
        assertThat(accessLog.getRequestIpProvider(), is(notNullValue()));
        assertThat(accessLog.getResponseCode(), is(statusCode));
        assertThat(accessLog.getRequestUri(), is(notNullValue()));
    }

    private static HttpHeaders buildHeaders(final boolean enableFileValidation) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("enableFileValidation", valueOf(enableFileValidation));
        return headers;
    }

    private static ResponseDefinitionBuilder geoIpResponseBuilder(final String countryCode, final String isp) {
        return aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(format("""
                        {"query":"127.0.0.1","countryCode": "%s","isp": "%s"}""", countryCode, isp));
    }


    private ResponseEntity<String> postFileEnableFileValidation(final File file) throws IOException {
        return postFile(file, true);
    }

    private ResponseEntity<String> postFile(final File file, final boolean enableFileValidation) throws IOException {

        final HttpHeaders headers = buildHeaders(enableFileValidation);
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(file.getName())
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<String> fileEntity = new HttpEntity<>(Files.readString(file.toPath()), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);


        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        final String url = "http://localhost:%d/api/txt2json".formatted(port);
        return restTemplate.postForEntity(url, entity, String.class);

    }

    private static File getOneFile(final String fileName) {
        File firstFile;
        try {
            firstFile = ResourceUtils.getFile("classpath:" + fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return firstFile;
    }

}