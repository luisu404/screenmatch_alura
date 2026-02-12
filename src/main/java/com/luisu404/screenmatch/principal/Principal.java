package com.luisu404.screenmatch.principal;

import com.luisu404.screenmatch.model.DatosEpisodio;
import com.luisu404.screenmatch.model.DatosSerie;
import com.luisu404.screenmatch.model.DatosTemporadas;
import com.luisu404.screenmatch.model.Episodio;
import com.luisu404.screenmatch.service.ConsumoAPI;
import com.luisu404.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
        Scanner lectura = new Scanner(System.in);
        private ConsumoAPI apiService = new ConsumoAPI();
        private final String URL_BASE = "https://www.omdbapi.com/?";
        private final String API_KEY = "44a03c17";
        private ConvierteDatos conversorDatos = new ConvierteDatos();

    public void mostrarMenu(){
        System.out.println("Escribe el nombre de la serie");
        var nombreSerie = lectura.nextLine();

        //Buscando los datos generales de la serie
        var jsonSerie = apiService.obtenerDatos(URL_BASE+"t=" + nombreSerie.replace(" ", "+") + "&apikey=" + API_KEY);
        var datosSerie = conversorDatos.obtenerDatos(jsonSerie, DatosSerie.class);
        System.out.println(datosSerie);

        //Buscando los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datosSerie.totalTemporadas();i++){
            var jsonTemporadas = apiService.obtenerDatos(URL_BASE+"t="+nombreSerie.replace(" ","+")+"&Season="+i+"&apikey=" + API_KEY);
            var datosTemporadas = conversorDatos.obtenerDatos(jsonTemporadas,DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        //temporadas.forEach(System.out::println);

        //Mostrar solo el título de los episodios por temporadas
        /*for (int i = 0; i < datosSerie.totalTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }*/

        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));


        //Convertir todas las informaciones en una lista de DatosEpisodio

        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t ->t.episodios().stream())
                .collect(Collectors.toList());

        //Top 5 episodios
//        System.out.println("TOP 5 EPISODIOS");
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e-> System.out.println("Primero: Filtro (N/A)"+e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e-> System.out.println("Segundo: Ordenación (M>m)"+e))
//                .map(e->e.titulo().toUpperCase())
//                .peek(e-> System.out.println("Tercero: Mayúsculas (m>M)"+e))
//                .limit(5)
//                .forEach(System.out::println);

        //Convirtiendo los datos a una lista de tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t-> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(),d)))
                .collect(Collectors.toList());

        //episodios.forEach(System.out::println);



        //Busqueda de episodio a partir de x año
//        System.out.println("Escribe el año a partir del cual desea ver los episodios");
//        var fecha = lectura.nextInt();
//        lectura.nextLine();

        //LocalDate fechaBusqueda = LocalDate.of(fecha,1,1);

//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e->e.getFechaLanzamiento() != null & e.getFechaLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                            "Temporada" + e.getTemporada()+
//                                    "Episodio"+e.getTitulo()+
//                                    "Fecha de lanzamiento" + e.getFechaLanzamiento().format(dtf)
//                ));


        //Buscar episodio por un pedazo de titulo

//        System.out.println("Escribe el titulo o pezado de titulo");
//        var pezadoTitulo = lectura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pezadoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episodio encontrado");
//            System.out.println("Los datos son: "+ episodioBuscado.get());
//        }
//        else {
//            System.out.println("Episoido no encontrado");
//             }



    Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
            .filter(e-> e.getEvaluacion()>0.0)
            .collect(Collectors.groupingBy(Episodio::getTemporada,
                    Collectors.averagingDouble(Episodio::getEvaluacion)));

        System.out.println(evaluacionesPorTemporada);
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e-> e.getEvaluacion()>0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de evaluaciones: " + est.getAverage());
        System.out.println("Episodio mejor evaluado: " + est.getMax());
        System.out.println("Episodio peor evaluado: " + est.getMin());


    }
}
