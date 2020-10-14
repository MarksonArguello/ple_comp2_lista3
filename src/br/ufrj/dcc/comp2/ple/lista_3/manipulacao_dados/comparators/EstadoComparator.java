package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.Dado;

import java.util.Comparator;

public class EstadoComparator implements Comparator<Dado> {
    @Override
    public int compare(Dado o1, Dado o2) {
        return o1.getEstado().compareTo(o2.getEstado());
    }
}
