package com.luisu404.screenmatch.repository;

import com.luisu404.screenmatch.model.Categoria;
import com.luisu404.screenmatch.model.Episodio;
import com.luisu404.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ISerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();

    List<Serie> findByGenero(Categoria categoria);
    //List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int cantidadTemporadas,Double evaluacion);

    @Query("select s from Serie s where s.totalTemporadas <= :cantidadTemporadas and s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemporadaYEvaluacion(int cantidadTemporadas,Double evaluacion);

    @Query("select e from Serie s join s.episodios e where e.titulo ilike %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio);

    @Query("select e from Serie s join s.episodios e where s = :serie order by e.evaluacion desc limit 5")
    List<Episodio> top5Episodios(Serie serie);

}
