package br.ufrj.dcc.comp2.ple.lista_3.criacaoArquivos;

import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.Dado;
import br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados.comparators.DataComparator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class PaginaWeb {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String path = "./src/br/ufrj/dcc/comp2/ple/lista_3/criacaoArquivos/";
    private List<Dado> listaDadoGrafico = new ArrayList<>();
    private String dataInicial;
    private String dataFinal;

    public PaginaWeb(List<Dado> listaDadoGrafico) {
        this.listaDadoGrafico = listaDadoGrafico;
        DataComparator dataComparator = new DataComparator();
        Collections.sort(listaDadoGrafico, dataComparator);
        dataInicial = dateFormat.format(listaDadoGrafico.get(0).getData());
        dataFinal = dateFormat.format(listaDadoGrafico.get(listaDadoGrafico.size()-1).getData());
        criarPagina();
    }




    public void criarPagina() {
        try (FileInputStream fileInputStream = new FileInputStream(new File(path + "template_grafico.html"));
             FileOutputStream fileOutputStream = new FileOutputStream(path + "grafico.html")) {



            Scanner scannerHtml = new Scanner(fileInputStream);
            PrintStream out = new PrintStream(fileOutputStream);
            StringBuilder stringBuilder = new StringBuilder();


            while (scannerHtml.hasNextLine()) { //Lê o template HTML

                String linha = scannerHtml.nextLine(); // Pega a linha do template HTML
                out.println(linha);
                if(linha.trim().equals("var items = [")) { // Se não for igual a :items: escreve normalmente
                    for (int indice = 0; indice < listaDadoGrafico.size(); indice++) { //Escreve os dados no lugar de :items:
                        Dado dado = listaDadoGrafico.get(indice);
                        String data = dateFormat.format(dado.getData());
                        String dadosCasos;

                        stringBuilder.append("{x: '").append(data).append("', y: ").append(dado.getCasos()).append(", group: \"Casos\"},\n");
                        stringBuilder.append("{x: '").append(data).append("', y: ").append(dado.getMortes()).append(", group: \"Mortes\"}");
                        dadosCasos = (stringBuilder.toString());
                        stringBuilder.setLength(0);
                        if (indice == listaDadoGrafico.size() - 1) {
                            out.println(dadosCasos); // escreve no html novo
                        } else {
                            out.println(dadosCasos + ","); // escreve no html novo
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
