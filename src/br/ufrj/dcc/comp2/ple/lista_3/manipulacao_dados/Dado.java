package br.ufrj.dcc.comp2.ple.lista_3.manipulacao_dados;

import java.util.Date;

/**
 * Classe que representa um dado contendo data, estado, cidade, casos, mortes, populacao.
 */
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

    /**
     * Construtor da classe Dado
     * @param data data dos dados
     * @param estado
     * @param cidade
     * @param casos
     * @param mortes
     * @param populacao
     */
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

    /**
     * Construtor da classe Dado quando queremos apenas a taxa de crescimento da cidade.
     * @param data
     * @param cidade
     * @param taxaDeCrescimento
     */
    public Dado(Date data, String cidade, double taxaDeCrescimento) {
        this.data = data;
        this.cidade = cidade;
        this.taxaDeCrescimento = taxaDeCrescimento;
    }

    /**
     * Retorna um objeto com os mesmos atributos
     * @return Clone do dado.
     */
    @Override
    public Dado clone() {
        return (Dado) this.clone();
    }

    /**
     *
     * @return data
     */
    public Date getData() {
        return data;
    }

    /**
     *
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     *
     * @return casos
     */
    public int getCasos() {
        return casos;
    }

    /**
     *
     * @return mortes
     */
    public int getMortes() {
        return mortes;
    }

    /**
     *
     * @return populacao
     */
    public int getPopulacao() {
        return populacao;
    }

    /**
     *
     * @return casos por 100 mil habitantes
     */
    public double getCasosPor100k() {
        return casosPor100k;
    }

    /**
     *
     * @return mortes por casos
     */
    public double getMortesPorCasos() {
        return mortesPorCasos;
    }

    /**
     *
     * @return cidade
     */
    public String getCidade() {
        return cidade;
    }

    /**
     *
     * @return taxa de crescimento
     */
    public double getTaxaDeCrescimento() {
        return taxaDeCrescimento;
    }
}
