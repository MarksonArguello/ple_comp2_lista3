package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.Dado;

import java.util.Comparator;

/**
 * Classe que representa um comparador de taxa de crescimento
 */
public class TaxaDeCrescimentoComparator implements Comparator<Dado> {
    @Override
    public int compare(Dado c1, Dado c2) {
        if (c1.getTaxaDeCrescimento() < c2.getTaxaDeCrescimento())
            return -1;
        if (c1.getTaxaDeCrescimento() > c2.getTaxaDeCrescimento())
            return 1;
        return 0;
    }
}
