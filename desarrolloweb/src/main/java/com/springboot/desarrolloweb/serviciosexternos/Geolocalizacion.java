package com.springboot.desarrolloweb.serviciosexternos;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Geolocalizacion {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene información de distrito y ciudad usando coordenadas geográficas
     * Utiliza la API de Nominatim (OpenStreetMap) que es gratuita
     */
    public LocationInfo getLocationInfo(double lat, double lon) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://nominatim.openstreetmap.org/reverse")
                    .queryParam("format", "json")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("addressdetails", 1)
                    .queryParam("accept-language", "es")
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                JsonNode address = root.get("address");

                if (address != null) {
                    String distrito = extractDistrito(address);
                    String ciudad = extractCiudad(address);

                    log.info("Ubicación encontrada - Distrito: {}, Ciudad: {}", distrito, ciudad);
                    return new LocationInfo(distrito, ciudad);
                }
            }
        } catch (Exception e) {
            log.error("Error al obtener información de ubicación para lat: {}, lon: {}", lat, lon, e);
        }

        return new LocationInfo("Desconocido", "Desconocido");
    }

    private String extractDistrito(JsonNode address) {
        // En Perú, el distrito puede venir en diferentes campos
        String[] distritoFields = {
                "suburb", "neighbourhood", "city_district",
                "district", "municipality", "quarter"
        };

        for (String field : distritoFields) {
            JsonNode node = address.get(field);
            if (node != null && !node.asText().isEmpty()) {
                return node.asText();
            }
        }

        return "Desconocido";
    }

    private String extractCiudad(JsonNode address) {
        // Campos donde puede venir la ciudad
        String[] ciudadFields = {
                "region"
        };

        for (String field : ciudadFields) {
            JsonNode node = address.get(field);

            if (node != null && !node.asText().isEmpty()) {
                log.info("ciudad: {}", node.asText());
                return node.asText();
            }
        }

        return "Desconocido";
    }

    /**
     * Clase interna para encapsular la información de ubicación
     */
    public static class LocationInfo {
        private final String distrito;
        private final String ciudad;

        public LocationInfo(String distrito, String ciudad) {
            this.distrito = distrito;
            this.ciudad = ciudad;
        }

        public String getDistrito() {
            return distrito;
        }

        public String getCiudad() {
            return ciudad;
        }

        @Override
        public String toString() {
            return String.format("Distrito: %s, Ciudad: %s", distrito, ciudad);
        }
    }
}