package com.telemetry.urlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenRequest {
    
    private String url;
    private String customCode;  // Opcional: permite al usuario especificar un código personalizado
}
