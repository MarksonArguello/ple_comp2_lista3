package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados;

import java.util.Date;

public class Dado implements Cloneable{
    private Date data;
    private String estado;
    private String cidade;
    private int casos;
    private int mortes;
    private int populacao;
    private double casosPor100k = 0;
    private double mortesPorCasos = 0;
    private double taxaDeCrescimento = 0;


    public Dado(Date data, String estado, String cidade, int casos, int mortes, int populacao) {
        this.data = data;
        this.estado = estado;
        this.cidade = cidade;
        this.casos = casos;
        this.mortes = mortes;
        this.populacao = populacao;
        if (this.populacao > 0)this.casosPor100k = (float)casos / (float) populacao * 100000;
        if (this.casos > 0)this.mortesPorCasos = (double) mortes / (double) casos;
    }

    public Dado(Date data, String cidade, double taxaDeCrescimento) {
        this.data = data;
        this.cidade = cidade;
        this.taxaDeCrescimento = taxaDeCrescimento;
    }

    @Override
    public Dado clone() {
        return (Dado) this.clone();
    }

    public Date getData() {
        return data;
    }

    public String getEstado() {
        return estado;
    }

    public int getCasos() {
        return casos;
    }

    public int getMortes() {
        return mortes;
    }

    public int getPopulacao() {
        return populacao;
    }

    public double getCasosPor100k() {
        return casosPor100k;
    }

    public double getMortesPorCasos() {
        return mortesPorCasos;
    }

    public String getCidade() {
        return cidade;
    }

    public double getTaxaDeCrescimento() {
        return taxaDeCrescimento;
    }
}
