package com.abobos.springboot.demo.txt2json.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IpServiceUTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void getClientIpAddr() {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        final IpService service = new IpService();
        final String ip = service.getClientIpAddr(request);
        assertThat(ip, is("127.0.0.1"));

        verify(request).getRemoteAddr();
        verify(request,times(5)).getHeader(anyString());

        verifyNoMoreInteractions(request);
    }


}