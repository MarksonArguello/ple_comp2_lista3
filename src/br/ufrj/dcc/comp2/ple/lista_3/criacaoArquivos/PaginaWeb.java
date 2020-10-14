package br.ufrj.dcc.comp2.ple.lista_3.criacaoArquivos;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.Dado;
import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators.DataComparator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável por criar a página web com o gráfico
 */
public class PaginaWeb {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String path = "./src/br/ufrj/dcc/comp2/ple/lista_3/criacaoArquivos/";
    private List<Dado> listaDadoGrafico;
    private String dataInicial;
    private String dataFinal;

    /**
     * Construtor da classe
     * <p>
     *     Ordena a lista com os dados de acordo com a data chama o método criarPagina.
     * </p>
     * @param listaDadoGrafico lista com os dados necessários para a criação do gráfico
     */
    public PaginaWeb(List<Dado> listaDadoGrafico) {
        this.listaDadoGrafico = listaDadoGrafico;
        DataComparator dataComparator = new DataComparator();
        Collections.sort(listaDadoGrafico, dataComparator);
        dataInicial = dateFormat.format(listaDadoGrafico.get(0).getData());
        dataFinal = dateFormat.format(listaDadoGrafico.get(listaDadoGrafico.size()-1).getData());
        criarPagina();
    }


    /**
     * Cria a página com o html e insere os dados da lista no arquivo.
     */
    public void criarPagina() {
        try (FileInputStream fileInputStream = new FileInputStream(new File(path + "template_grafico.html"));
             FileOutputStream fileOutputStream = new FileOutputStream(path + "grafico.html")) {



            Scanner scannerHtml = new Scanner(fileInputStream);
            PrintStream out = new PrintStream(fileOutputStream);
            StringBuilder stringBuilder = new StringBuilder();


            while (scannerHtml.hasNextLine()) {

                String linha = scannerHtml.nextLine();
                out.println(linha);
                if(linha.trim().equals("var items = [")) {
                    for (int indice = 0; indice < listaDadoGrafico.size(); indice++) {
                        Dado dado = listaDadoGrafico.get(indice);
                        String data = dateFormat.format(dado.getData());
                        String dadosCasos;

                        stringBuilder.append("{x: '").append(data).append("', y: ").append(dado.getCasos()).append(", group: \"Casos\"},\n");
                        stringBuilder.append("{x: '").append(data).append("', y: ").append(dado.getMortes()).append(", group: \"Mortes\"}");
                        dadosCasos = (stringBuilder.toString());
                        stringBuilder.setLength(0);
                        if (indice == listaDadoGrafico.size() - 1) {
                            out.println(dadosCasos);
                        } else {
                            out.println(dadosCasos + ",");
                        }
                    }
                    scannerHtml.nextLine();
                } else if (linha.trim().equals("var options = {")) {
                    out.println("start: '"+dataInicial+"',");
                    out.println("end: '"+dataFinal+"',");
                    scannerHtml.nextLine();
                    scannerHtml.nextLine();
                }
            }
            System.out.println("Página gerada com sucesso");
        } catch (IOException e) {
            System.out.println( e + " Erro ao abrir ou fechar arquivo para criar a página web.");
        } catch (RuntimeException e) {
            System.out.println(e + " ao  abrir o arquivo para criar a página web.");
        }
    }
}
