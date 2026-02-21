package com.luisu404.screenmatch.principal;
import com.luisu404.screenmatch.model.*;
import com.luisu404.screenmatch.repository.ISerieRepository;
import com.luisu404.screenmatch.service.ConsumoAPI;
import com.luisu404.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {
        Scanner lectura = new Scanner(System.in);
        private ConsumoAPI apiService = new ConsumoAPI();
        private final String URL_BASE = "https://www.omdbapi.com/?t=";
        private final String API_KEY = "&apikey=44a03c17";
        private ConvierteDatos conversorDatos = new ConvierteDatos();
        private List<DatosSerie> datosSeries = new ArrayList<>();
        private ISerieRepository repository;
        private List<Serie> series;
        private Optional<Serie> serieBuscada;

    public Principal(ISerieRepository repository) {
        this.repository = repository;
    }

    public void mostrarMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1. Buscar serie
                    2. Buscar episodio
                    3. Mostrar series buscadas
                    4. Buscar serie por nombre
                    5. Buscar Top 5 Serie
                    6. Buscar por categoria
                    7. Buscar series por Temporada y Evaluacion
                    8. Buscar episodio por titulo
                    9. Top 5 Episodios por Serie
                    0. Salir
                    """;
            System.out.println(menu);
            opcion = lectura.nextInt();
            lectura.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;

                case 6:
                    buscarPorCategoria();
                    break;
                case 7:
                    buscarPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorTitulo();
                    break;
                case 9:
                    top5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicacion...");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }

    }



    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie");
        var nombreSerie = lectura.nextLine();
        var jsonSerie = apiService.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") +  API_KEY);
        System.out.println(jsonSerie);
        DatosSerie datosSerie = conversorDatos.obtenerDatos(jsonSerie, DatosSerie.class);
        return datosSerie;
    }

    private void buscarEpisodioPorSerie(){
        //DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie para ser sus episodios");
        var nombreSerie = lectura.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        List<DatosTemporadas> temporadas = null;
        Serie serieEncontrada;
        if (serie.isPresent()){
            serieEncontrada = serie.get();
            temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas();i++){
                var jsonTemporadas = apiService.obtenerDatos(URL_BASE+serieEncontrada.getTitulo().replace(" ","+")+"&Season="+i+API_KEY);
                DatosTemporadas datosTemporadas = conversorDatos.obtenerDatos(jsonTemporadas,DatosTemporadas.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d ->d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            episodios.forEach(e -> e.setSerie(serieEncontrada));
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }



    }

    private void buscarSerieWeb(){
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repository.save(serie);

        //datosSeries.add(datos);

        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
          series = repository.findAll();
//
//        new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d-> new Serie(d))
//                .collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escribe el nombre de la serie que desea buscar");
        var nombreSerie = lectura.nextLine();

        serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es: "+ serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }


    private void buscarTop5Series() {
        List<Serie> top5Series = repository.findTop5ByOrderByEvaluacionDesc();
        top5Series.forEach(s-> System.out.println("Titulo: " + s.getTitulo() + ", Evaluación: " + s.getEvaluacion()));
    }


    private void buscarPorCategoria() {
        System.out.println("Escribe el genero de la serie que desea buscar");
        var genero = lectura.nextLine();
        var categoria= Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Las serie de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarPorTemporadaYEvaluacion() {
        System.out.println("Escribe la cantidad de temporada para buscar la serie");
        var cantidadTemporadas = lectura.nextInt();
        lectura.nextLine();
        System.out.println("Tambien indique la evaluacion");
        var evaluacion = lectura.nextDouble();
        lectura.nextLine();

        //List<Serie> seriesEncontradas = repository.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(cantidadTemporadas, evaluacion);
        List<Serie> seriesEncontradas = repository.seriesPorTemporadaYEvaluacion(cantidadTemporadas, evaluacion);

        if (seriesEncontradas != null){
            System.out.println("*** Series encontradas con el filtro *** ");
            seriesEncontradas.forEach(s -> System.out.println("Titulo: " + s.getTitulo() + " Puntos de Evaluacion: " + s.getEvaluacion()));
        } else {
            System.out.println("Ningun resultado para este filtrado");
        }
    }


    private void buscarEpisodioPorTitulo() {
        System.out.println("Escribe el nombre del episodio");
        var nombreEpisodio = lectura.nextLine();

        List<Episodio> episodiosEncontrado = repository.episodiosPorNombre(nombreEpisodio);

        episodiosEncontrado.forEach(e->
                System.out.printf("Serie: %s, Titulo Episodio: %s, Temporadas: %s, Episodio: %s, Evaluación: %s \n",
                        e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
    }



    private void top5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repository.top5Episodios(serie);

            topEpisodios.forEach(e->
                    System.out.printf("Serie: %s, Titulo Episodio: %s, Temporadas: %s, Episodio: %s, Evaluación: %s \n",
                            e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
        }

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

//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t ->t.episodios().stream())
//                .collect(Collectors.toList());

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
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t-> t.episodios().stream()
//                        .map(d -> new Episodio(t.numeroTemporada(),d)))
//                .collect(Collectors.toList());
//
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


//
//    Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
//            .filter(e-> e.getEvaluacion()>0.0)
//            .collect(Collectors.groupingBy(Episodio::getTemporada,
//                    Collectors.averagingDouble(Episodio::getEvaluacion)));
//
//        System.out.println(evaluacionesPorTemporada);
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e-> e.getEvaluacion()>0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println("Media de evaluaciones: " + est.getAverage());
//        System.out.println("Episodio mejor evaluado: " + est.getMax());
//        System.out.println("Episodio peor evaluado: " + est.getMin());


    }

