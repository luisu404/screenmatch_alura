package com.luisu404.screenmatch;

import com.luisu404.screenmatch.model.DatosEpisodio;
import com.luisu404.screenmatch.model.DatosSerie;
import com.luisu404.screenmatch.model.DatosTemporadas;
import com.luisu404.screenmatch.principal.EjemploStreams;
import com.luisu404.screenmatch.principal.Principal;
import com.luisu404.screenmatch.repository.ISerieRepository;
import com.luisu404.screenmatch.service.ConsultaGoogleGemini;
import com.luisu404.screenmatch.service.ConsumoAPI;
import com.luisu404.screenmatch.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	@Autowired
    private ISerieRepository repository;
    public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {

        Principal principal = new Principal(repository);
        principal.mostrarMenu();


    }
}
