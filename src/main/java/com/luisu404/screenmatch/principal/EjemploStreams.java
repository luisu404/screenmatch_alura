package com.luisu404.screenmatch.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EjemploStreams {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Luis","Jose","Fernando","Antonio","Pedro","Manuel");
        nombres.stream()
                .sorted()
                .limit(2)
                .filter(n->n.startsWith("F"))
                .map(n->n.toUpperCase())
                .forEach(System.out::println);
    }
}
