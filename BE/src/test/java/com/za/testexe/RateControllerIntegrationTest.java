package com.za.testexe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.za.testexe.model.entity.RateEntity;
import com.za.testexe.repository.RateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RateRepository rateRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String dbName = "rate-test-" + UUID.randomUUID();
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:file:" + dbName + "?mode=memory&cache=shared");
        registry.add("spring.datasource.driver-class-name", () -> "org.sqlite.JDBC");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "1");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "1");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.community.dialect.SQLiteDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @BeforeEach
    void cleanRateTable() {
        rateRepository.deleteAll();
    }

    @Test
    void shouldCreateAnnualeWithValidMese() throws Exception {
        Map<String, Object> payload = basePayload();
        payload.put("periodo", "Annuale");
        payload.put("mese", "Marzo");

        mockMvc.perform(post("/api/rate/salva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.periodo").value("Annuale"))
                .andExpect(jsonPath("$.data.mese").value("Marzo"))
                .andExpect(jsonPath("$.data.attivo").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenAnnualeWithoutMese() throws Exception {
        Map<String, Object> payload = basePayload();
        payload.put("periodo", "Annuale");
        payload.put("mese", null);

        mockMvc.perform(post("/api/rate/salva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldForceMeseNullWhenPeriodoIsNotAnnuale() throws Exception {
        Map<String, Object> payload = basePayload();
        payload.put("periodo", "Inizio mese");
        payload.put("mese", "Giugno");

        mockMvc.perform(post("/api/rate/salva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.periodo").value("Inizio mese"))
                .andExpect(jsonPath("$.data.mese").value(nullValue()));
    }

    @Test
    void shouldToggleAttivoTrueAndFalse() throws Exception {
        Integer idRate = createRate("Annuale", "Settembre", true, "Rata Toggle");

        mockMvc.perform(patch("/api/rate/attivo/{idRate}", idRate)
                        .param("attivo", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        RateEntity afterDisable = rateRepository.findById(idRate).orElseThrow();
        assertEquals(Boolean.FALSE, afterDisable.getAttivo());

        mockMvc.perform(patch("/api/rate/attivo/{idRate}", idRate)
                        .param("attivo", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        RateEntity afterEnable = rateRepository.findById(idRate).orElseThrow();
        assertEquals(Boolean.TRUE, afterEnable.getAttivo());
    }

    @Test
    void listShouldIncludeMeseAndAttivoFields() throws Exception {
        Integer annualeId = createRate("Annuale", "Dicembre", false, "Rata Annuale Lista");
        Integer inizioMeseId = createRate("Inizio mese", "Aprile", true, "Rata Inizio Lista");

        MvcResult result = mockMvc.perform(get("/api/rate/lista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode data = root.path("data");

        JsonNode annuale = findById(data, annualeId);
        assertNotNull(annuale);
        assertEquals("Dicembre", annuale.path("mese").asText());
        assertEquals(false, annuale.path("attivo").asBoolean());

        JsonNode inizioMese = findById(data, inizioMeseId);
        assertNotNull(inizioMese);
        assertEquals(true, inizioMese.path("attivo").asBoolean());
        assertEquals(true, inizioMese.path("mese").isNull());
    }

    private Integer createRate(String periodo, String mese, Boolean attivo, String descrizione) throws Exception {
        Map<String, Object> payload = basePayload();
        payload.put("periodo", periodo);
        payload.put("mese", mese);
        payload.put("attivo", attivo);
        payload.put("descrizione", descrizione);

        MvcResult result = mockMvc.perform(post("/api/rate/salva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("idRate").asInt();
    }

    private JsonNode findById(JsonNode arrayNode, Integer idRate) {
        for (JsonNode node : arrayNode) {
            if (node.path("idRate").asInt() == idRate) {
                return node;
            }
        }
        return null;
    }

    private Map<String, Object> basePayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idRate", null);
        payload.put("descrizione", "Rata Test");
        payload.put("euro", BigDecimal.valueOf(123.45));
        payload.put("nrRate", 1);
        payload.put("nrRateMax", 12);
        payload.put("maxValore", BigDecimal.valueOf(1000));
        payload.put("periodo", "Vicino stipendio");
        payload.put("mese", null);
        payload.put("attivo", null);
        return payload;
    }
}

