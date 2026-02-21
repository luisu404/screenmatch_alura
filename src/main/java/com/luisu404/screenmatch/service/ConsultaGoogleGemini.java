package com.luisu404.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class ConsultaGoogleGemini {
    public static String obtenerTraduccion(String texto) {


                Client client = new Client.Builder()
                        .apiKey("${GOOGLE_GEMINI_APIKEY1}")
                        .build();

        String prompt = "Traduce el siguiente texto al español. " +
                "Devuelve ÚNICAMENTE el texto traducido, sin introducciones, " +
                "sin explicaciones y sin comillas: " + texto;
                try {
                    GenerateContentResponse response =
                            client.models.generateContent(
                                    "gemini-2.5-flash-lite", // modelo Gemini que desees usar
                                    prompt,
                                    null); // Se pueden agregar configuraciones. Se debe investigar.

                    if (response != null && response.text() != null) {
                        return response.text();
                    } else {
                        System.out.println("La API de Gemini no devolvió texto para la traducción.");
                        return "";
                    }

                } catch (Exception e) {
                    System.out.println("Error al traducir el texto: " + e.getMessage());
                    e.printStackTrace();
                    return "";
                }
            }



        }
