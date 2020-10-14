package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados;

import br.ufrj.dcc.comp2.ple.lista_3.criacaoArquivos.PaginaWeb;
import br.ufrj.dcc.comp2.ple.lista_3.criacaoArquivos.Ranking;
import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators.DataComparator;


import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ManipulacaoDados {

    private static final String path = "./src/br/ufrj/dcc/comp2/ple/lista_3/manipulacao_dados/caso.csv.gz";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Scanner sc = new Scanner(System.in);

    private String cidade = null;
    private String estado = null;
    private PaginaWeb paginaWeb;
    private List<Dado> dadosParaRankings = new ArrayList<>();
    private List<Dado> dadosParaTaxaDeCrescimento = new ArrayList<>();
    private List<Dado> listaDadoGrafico = new ArrayList<>();
    private List<Dado> totalPais = new ArrayList<>();
    private Dado anterior = null;


    public ManipulacaoDados() {
        int dados = lerEntrada();
        long start = System.currentTimeMillis();
        if (criarListas()) {
            if (dados != 0)
                paginaWeb = new PaginaWeb(listaDadoGrafico);
            else
                paginaWeb = new PaginaWeb(totalPais);

        }
        Ranking.criarRankings(dadosParaRankings, dadosParaTaxaDeCrescimento);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.println("Tudo foi executado em " + (float) elapsed / 1000.0 + " segundos.");
    }

    public int lerEntrada() {
        System.out.println("Digite a cidade  e depois a sigla do estado separados por barra '//':");

        ArrayList<String> lista = new ArrayList<String>(Arrays.asList(sc.nextLine().split("//")));
        if (lista.size() > 1) {
            estado = lista.get(1);
            cidade = lista.get(0);
        } else if (lista.size() == 1 && lista.get(0).length() > 0) {
            estado = lista.get(0);
        } else {
            return 0;
        }

        return lista.size();
    }

    public boolean criarListas() {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(path))) {
            Scanner scannerZip = new Scanner(gzipInputStream);
            return lerLista(scannerZip);
        } catch (IOException e) {
            System.out.println(e + " Erro ao abrir ou fechar arquivo para criar listas.");
            System.exit(0);
            return false;
        } catch (RuntimeException e) {
            System.out.println(e + " Sem permissão para abrir o arquivo para criar listas.");
            System.exit(0);
            return false;
        }
    }

    private boolean lerLista(Scanner scannerZip) {
        while (scannerZip.hasNextLine()) {
            String[] aux = scannerZip.nextLine().split(",");
            if (aux[0].equals("date"))
                continue;
            criarListasRanking(aux);
            criaListaPaginaWeb(aux);
        }
        if (this.listaDadoGrafico.size() == 0 && estado != null) {
            System.out.println("Não foram encontrados lugares com os dados passados");
            return false;
        } else {
            if (cidade == null && estado == null) criaListaParaBrazil();
            return true;
        }
    }

    private void criarListasRanking(String[] aux) {
        if (aux[3].equals("city")) {
            Dado dadoAuxliar = lerDado(aux);

            if (dadoAuxliar == null)
                return;
            if (aux[7].equals("True"))
                dadosParaRankings.add(dadoAuxliar);
            if (("2020-09-01".equals(dateFormat.format(dadoAuxliar.getData()))
                    || "2020-10-01".equals(dateFormat.format(dadoAuxliar.getData())))){
                    dadosParaTaxaDeCrescimento.add(dadoAuxliar);
            }

        }

    }

    private void criaListaPaginaWeb(String[] aux) {
        Dado dadoAuxiliar = lerDado(aux);
        if (dadoAuxiliar == null)
            return;
        if (cidade == null && estado == null) {
            if (aux[3].equals("state")) {
                listaDadoGrafico.add(dadoAuxiliar);
            }
        } else if ((aux[1].equals(estado) && cidade == null && aux[3].equals("state"))
                || (aux[1].equals(estado) && aux[2].equals(cidade) && aux[3].equals("city"))) {
            listaDadoGrafico.add(dadoAuxiliar);

        }
    }

    public void criaListaParaBrazil() {
        try {
            DataComparator dataComparator = new DataComparator();
            Collections.sort(listaDadoGrafico, dataComparator);

            String firstDate = null;
            String lastDate = null;


            int totalCasos = 0, totalMortes = 0;
            Date dataAnterior = null;
            Date date = null;
            for (Dado dado : listaDadoGrafico) {
                date = dado.getData();

                if (firstDate == null) {
                    firstDate = dateFormat.format(date);
                    lastDate = dateFormat.format(date);
                    dataAnterior = (Date) date.clone();
                    totalCasos += dado.getCasos();
                    totalMortes += dado.getMortes();
                } else {
                    if (date.compareTo(dataAnterior) != 0) {
                        totalPais.add(new Dado(dataAnterior, "", "", totalCasos, totalMortes, 0));
                        totalCasos = totalMortes = 0;
                        dataAnterior = (Date) date.clone();
                    }
                    totalCasos += dado.getCasos();
                    totalMortes += dado.getMortes();
                }

                if (date.after(dateFormat.parse(lastDate))) {
                    lastDate = dateFormat.format(date);
                }
            }

            totalPais.add(new Dado(dataAnterior, "", "", totalCasos, totalMortes, 0));

        } catch (ParseException e) {
            System.out.println(e + " Dados Brasil");
        }
    }


    private Dado lerDado(String[] aux) {
        int populacao = 0, casos = 0, mortes = 0;
        if (!aux[8].isEmpty()) {
            populacao = Integer.parseInt(aux[8]);
        }
        if (!aux[9].isEmpty()) {
            populacao = Integer.parseInt(aux[9]);
        }
        if (!aux[4].isEmpty()) {
            casos = Integer.parseInt(aux[4]);
        }
        if (!aux[5].isEmpty()) {
            mortes = Integer.parseInt(aux[5]);
        }
        try {
            Date data = dateFormat.parse(aux[0]);
            String estado = aux[1];
            String cidade = aux[2];
            if (casos > 0 && populacao > 0) {
                return new Dado(data, estado, cidade, casos, mortes, populacao);

            }
        } catch (ParseException e) {
            System.out.println(e + " Ao tentar converter data");
        }
        return null;
    }
}
