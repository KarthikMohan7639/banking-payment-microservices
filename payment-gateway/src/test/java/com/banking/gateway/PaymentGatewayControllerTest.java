package com.banking.gateway;

import com.banking.gateway.controller.PaymentGatewayController;
import com.banking.gateway.dto.ChargeRequest;
import com.banking.gateway.dto.ChargeResponse;
import com.banking.gateway.entity.TransactionStatus;
import com.banking.gateway.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentGatewayController.class)
class PaymentGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentGatewayService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChargeEndpoint() throws Exception {
        ChargeRequest request = ChargeRequest.builder()
                .idempotencyKey("KEY-XYZ")
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .build();

        ChargeResponse response = new ChargeResponse("GW-REF-12345", TransactionStatus.SUCCESS);

        when(service.charge(any(ChargeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/gateway/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gatewayRef").value("GW-REF-12345"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
