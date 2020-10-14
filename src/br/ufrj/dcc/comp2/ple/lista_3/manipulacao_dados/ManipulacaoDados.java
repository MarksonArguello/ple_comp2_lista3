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

/**
 * Classe responsável por manipular os dados.
 * <p>
 *     Nessa classe lemos a entrada do usuário e criamos listas para criar os rankings e o gráfico
 * </p>
 */
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


    /**
     * Construtor da classe ManipulacaoDados
     *
     * <p>
     *     Nesse método chamamos os métodos para ler a entrada do usuário, criar a lista para o gráfico de acordo
     *     com o que o usuário digitou e chamamos a classe PaaginaWeb se foram achados dados sobre o lugar que o
     *     usuário digitou. Além disso chamados a classe Ranking para a criação dos 5 arquivos de ranking.
     * </p>
     */
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

    /**
     * Lê a entrada do usuário e retorna o número dados na entrada.
     *
     * <p>
     *     Lê a entrada e retorna 2 se o usuário digitou estado e cidade, 1 se o usuário digitou apenas estado e
     *     0 se não digitou nada. A separação de estado e cidade é feita atráves do //.
     * </p>
     *
     * @return Número de dados digitados pelo usuário.
     */
    private int lerEntrada() {
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

    /**
     * Método responável por abrir o arquivo.
     * <p>
     *     Esse método abre o arquivo que será usado para os dados, caso consiga abrir chama o método lerLista().
     *     Retorna true se conseguiu ler pelo menos um arquivo e false caso não tenha lido nenhum dado.
     * </p>
     * @return booleano indicando se conseguiu ler pelo menos um dado correspondente a entrada do usuário.
     */
    private boolean criarListas() {
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

    /**
     * Método que transorma as linhas do arquivo em um array de String
     * <p>
     *     Esse método transorma as linhas do arquivo em um array de String e passa esse array como argumento para
     *     as classes que criaram as listas para a pagina web e os rankings.
     * </p>
     * @param scannerZip scanner do arquivo
     * @return booleano indicando se conseguiu ler pelo menos um dado correspondente a entrada do usuário.
     */
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

    /**
     * Enche as listas para os rankings com dados.
     *
     * <p>
     *     Lê os dados e enche duas listas, a dadosParaTaxaDeCrescimento para o ranking com as cidades com maior
     *     taxa de crescimento de casos (verifica de 01/09/2020 até 01/10/2020 apenas) e outra lista chamada
     *     dadosParaRankings que é usada para os outros arquivos.
     * </p>
     * @param aux Array de String com os dados da cidade/estado
     */
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

    /**
     * Enche as listas para a pagina web com dados.
     *
     * <p>
     *     lê os dados e guarda na lista os dados compativeis com a entrada do usuário.
     * </p>
     * @param aux Array de String com os dados da cidade/estado
     */
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

    /**
     * Enche a lista totalPais  com os dados do Brasil para o gráfico na pagina web.
     * <p>
     *     Esse método foi criado pois quando queremos saber o dados do Brasil inteiro precisamos pegar os casos
     *     e as mortes de cada estado e soma-los de acordo com suas datas.
     * </p>
     */
    private void criaListaParaBrazil() {
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

    /**
     * Lê os dados do array de String e passa para um objeto do tipo Dado.
     * @param aux Array de String com os dados da cidade/estado
     * @return Um objeto do tipo Dado com os dados da linha do array de String.
     */
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
