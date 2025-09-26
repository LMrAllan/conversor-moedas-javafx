package br.com.conversor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Classe que contém a lógica de negócio para a conversão de moedas.
 */
public class ConversorMoedas {

    // Mapa que armazenará as taxas de câmbio carregadas da API.
    private static Map<String, Double> taxas = new HashMap<>();

    /**
     * Chama o ApiServico para obter as taxas de câmbio e as armazena no mapa 'taxas'.
     * Este método deve ser chamado na inicialização do aplicativo.
     */
    public static void carregarTaxas() throws Exception {
        taxas = ApiServico.obterTaxasDeCambio();
        System.out.println("Taxas de câmbio carregadas com sucesso!");
    }

    /**
     * Retorna os códigos de todas as moedas disponíveis (ex: "USD", "BRL").
     */
    public static Set<String> getMoedasDisponiveis() {
        return taxas.keySet();
    }

    /**
     * Realiza a conversão de um valor entre duas moedas.
     * @param moedaDe String da moeda de origem (ex: "USD - Dólar").
     * @param moedaPara String da moeda de destino (ex: "BRL - Real").
     * @param valor O montante a ser convertido.
     * @return O valor já convertido.
     */
    public static double converter(String moedaDe, String moedaPara, double valor) {
        // Extrai apenas o código de 3 letras da moeda (ex: "USD").
        String codigoDe = moedaDe.substring(0, 3);
        String codigoPara = moedaPara.substring(0, 3);
        
        // Validação para garantir que as taxas foram carregadas e as moedas são válidas.
        if (taxas.isEmpty() || !taxas.containsKey(codigoDe) || !taxas.containsKey(codigoPara)) {
            throw new IllegalStateException("Taxas de câmbio não disponíveis para as moedas selecionadas.");
        }

        // Obtém as taxas de conversão a partir do Dólar (nossa moeda base da API).
        double taxaDe = taxas.get(codigoDe);
        double taxaPara = taxas.get(codigoPara);

        // Fórmula: primeiro converte o valor para a moeda base (USD), depois para a moeda de destino.
        double valorEmUSD = valor / taxaDe;
        return valorEmUSD * taxaPara;
    }
}