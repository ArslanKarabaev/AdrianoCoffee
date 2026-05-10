package com.example.AdrianoCoffee.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.isBlank()) return text;
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://api.mymemory.translated.net/get")
                    .queryParam("q", text)
                    .queryParam("langpair", sourceLang + "|" + targetLang)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            String translated = root.path("responseData").path("translatedText").asText();

            // Если перевод не получен — возвращаем оригинал
            if (translated == null || translated.isBlank() || translated.equals("null")) {
                return text;
            }
            return translated;

        } catch (Exception e) {
            System.err.println("Ошибка перевода: " + e.getMessage());
            return text; // fallback — оригинал
        }
    }

    public void translateMenu(com.example.AdrianoCoffee.Entity.Menu menu) {
        String name = menu.getName();
        String desc = menu.getDescription();

        // Переводим с русского
        menu.setNameEn(translate(name, "ru", "en"));
        menu.setNameKg(translate(name, "ru", "ky"));
        if (desc != null && !desc.isBlank()) {
            menu.setDescriptionEn(translate(desc, "ru", "en"));
            menu.setDescriptionKg(translate(desc, "ru", "ky"));
        }
    }
}