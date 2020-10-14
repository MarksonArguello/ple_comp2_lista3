package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.Dado;

import java.util.Comparator;
import java.util.Date;

/**
 * Classe que representa um comparador de data
 */
public class DataComparator implements Comparator<Dado> {

    @Override
    public int compare(Dado d1, Dado d2) {
        Date data1 = d1.getData();
        Date data2 = d2.getData();

        if (data1.before(data2))
            return -1;

        if (data1.after(data2))
            return 1;
        return 0;
    }
}
