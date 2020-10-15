package br.ufrj.dcc.comp2.ple.lista_3.criacaoArquivos;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.*;
import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators.*;

/**
 * Classe responsável por criar os arquivos de ranking.
 */
public class Ranking {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Dado> dadosParaRankings = new ArrayList<>();
    private List<Dado> dadosParaTaxaDeCrescimento = new ArrayList<>();
    private static final String path = "./src/br/ufrj/dcc/comp2/ple/lista_3/criacaoArquivos/Rankings/";


    /**
     * Método que chama os métodos para a criação dos rankings
     * @param dadosParaRankings dados usados para rankings exceto para Taxa de Crescimento
     * @param dadosParaTaxaDeCrescimento dados usados para o ranking de Taxa de Crescimento
     */
    public static void criarRankings(List<Dado> dadosParaRankings, List<Dado> dadosParaTaxaDeCrescimento) {
        criarRankingMenoresCasosPor100Mil(dadosParaRankings);
        criarRankingMenoresMortesPorCasos(dadosParaRankings);
        criarRankingMaioresCasosPor100Mil(dadosParaRankings);
        criarRankingMaioresMortesPorCasos(dadosParaRankings);
        criarRankingMaioresTaxasDeCrescimento(dadosParaTaxaDeCrescimento);
    }

    /**
     * Recebe uma lista com os dados das cidades e cria um arquivo .tsv com as 10 cidades com as maiores taxas
     * de crescimento entre o dia 01/09/2020 até 01/10/2020
     * @param lista lista com os dados das cidades.
     */
    private static void criarRankingMaioresTaxasDeCrescimento(List<Dado> lista) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path+"maior_taxa_crescimento.tsv")) {
            CidadeComparator cidadeComparator = new CidadeComparator();
            DataComparator dataComparator = new DataComparator();
            EstadoComparator estadoComparator = new EstadoComparator();
            Collections.sort(lista, estadoComparator.thenComparing(cidadeComparator.thenComparing(dataComparator)));

            PrintStream out = new PrintStream(fileOutputStream);
            Dado dadoAnterior = null;
            List<Dado> novaLista = new ArrayList<>();

            for (Dado dado : lista) {
                if (dadoAnterior != null) {
                    if (dado.getCidade().equals(dadoAnterior.getCidade()) && dado.getEstado().equals(dadoAnterior.getEstado())) {

                        double taxa = ( (double)dado.getCasos() - dadoAnterior.getCasos() ) / (double)dadoAnterior.getCasos();
                        novaLista.add(new Dado(dado.getData(), dado.getCidade(), taxa));
                    }
                }
                dadoAnterior = dado;
            }

            TaxaDeCrescimentoComparator comparator = new TaxaDeCrescimentoComparator();
            Collections.sort(novaLista, comparator.reversed());

            int escritos = 0;
            for (Dado dado : novaLista) {
                if (escritos == 10) break;
                escritos++;
                out.println(dado.getCidade() + "\t" +  dateFormat.format(dado.getData())+ "\t" + dado.getTaxaDeCrescimento());
            }

            System.out.println("Ranking Maiores Taxas gerado  com sucesso");
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo Ranking Maiores Taxas.");
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(e + " Sem permissão para abrir o arquivo para criar o Ranking Maiores Taxas");
        }
    }

    /**
     * Recebe uma lista com os dados das cidades e cria um arquivo .tsv com as 10 cidades com as menores mortes
     * por casos.
     * @param lista lista com os dados das cidades.
     */
    private static void criarRankingMenoresMortesPorCasos(List<Dado> lista) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path+"menor_mortalidade.tsv")) {


            PrintStream out = new PrintStream(fileOutputStream);
            MortePorCasosComparator mortePorCasosComparator = new MortePorCasosComparator();
            Collections.sort(lista, mortePorCasosComparator);

            int escritos = 0;


            for (int index = 0; index < lista.size(); index++) {
                Dado dado = lista.get(index);
                if(escritos == 10) break;
                if (dado.getMortesPorCasos() != 0){
                    out.println(dado.getCidade() + "\t"  + dateFormat.format(dado.getData()) + "\t" + dado.getMortesPorCasos());
                    escritos++;
                }

            }

            System.out.println("Ranking Menores Casos Por Mortes gerado  com sucesso");
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo Ranking Menores Casos Por Mortes.");
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(e + " Sem permissão para abrir o arquivo para criar o Ranking Menores Casos Por Mortes");
        }
    }

    /**
     * Recebe uma lista com os dados das cidades e cria um arquivo .tsv com as 10 cidades com as maiores mortes
     * por casos.
     * @param lista lista com os dados das cidades.
     */
    private static void criarRankingMaioresMortesPorCasos(List<Dado> lista) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path+"maior_mortalidade.tsv")) {


            PrintStream out = new PrintStream(fileOutputStream);
            MortePorCasosComparator mortePorCasosComparator = new MortePorCasosComparator();
            Collections.sort(lista, mortePorCasosComparator.reversed());

            for (int index = 0; index < 10; index++) {
                Dado dado = lista.get(index);
                out.println(dado.getCidade() + "\t"  + dateFormat.format(dado.getData()) + "\t" + dado.getMortesPorCasos());

            }

            System.out.println("Ranking Maiores Casos Por Mortes gerado  com sucesso");
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo Ranking Maiores Casos Por Mortes.");
        } catch (RuntimeException e) {
            System.out.println(e);
            System.out.println(e + " Sem permissão para abrir o arquivo para criar o Maiores Casos Por Mortes");
        }
    }

    /**
     * Recebe uma lista com os dados das cidades e cria um arquivo .tsv com as 10 cidades com as maiores casos
     * por 100 mil habitantes.
     * @param lista lista com os dados das cidades.
     */
    private static void criarRankingMaioresCasosPor100Mil(List<Dado> lista) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path+"maior_casos_100k.tsv")) {


            PrintStream out = new PrintStream(fileOutputStream);
            CasosPor100kComparator comparator = new CasosPor100kComparator();
            Collections.sort(lista, comparator.reversed());

            for (int index = 0; index < 10; index++) {
                Dado dado = lista.get(index);
                out.println(dado.getCidade() + "\t"  + dateFormat.format(dado.getData()) + "\t" + dado.getCasosPor100k());

            }

            System.out.println("Ranking Maiores Casos Por 100 Mil gerado  com sucesso");
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo Ranking Maiores Casos Por 100 Mil.");
        } catch (RuntimeException e) {
            System.out.println(e + "Sem permissão para abrir o arquivo para criar o Ranking Maiores Casos Por 100 Mil");
        }
    }

    /**
     * Recebe uma lista com os dados das cidades e cria um arquivo .tsv com as 10 cidades com as menores mortes
     * por casos.
     * @param lista lista com os dados das cidades.
     */
    private static void criarRankingMenoresCasosPor100Mil(List<Dado> lista) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path+"menor_casos_100k.tsv")) {


            PrintStream out = new PrintStream(fileOutputStream);
            CasosPor100kComparator comparator = new CasosPor100kComparator();
            Collections.sort(lista, comparator);

            for (int index = 0; index < 10; index++) {
                Dado dado = lista.get(index);
                out.println(dado.getCidade() + "\t"  + dateFormat.format(dado.getData()) + "\t" + dado.getCasosPor100k());

            }

            System.out.println("Ranking Menores Casos Por 100 Mil gerado  com sucesso");
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo Ranking Menores Casos Por 100 Mil.");
        } catch (RuntimeException e) {
            System.out.println(e + " Sem permissão para abrir o arquivo para criar o Ranking Menores Casos Por 100 Mil");
        }
    }

}
