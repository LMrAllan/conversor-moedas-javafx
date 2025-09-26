package br.com.conversor;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;


//Classe responsável pela comunicação com a API de taxas de câmbio.
 
public class ApiServico {

    // Cliente HTTP para realizar as requisições. É estático para ser reutilizado.
    private static final HttpClient client = HttpClient.newHttpClient();
    // Objeto para ler as propriedades do arquivo de configuração.
    private static final Properties props = new Properties();

    // Bloco estático: este código é executado uma única vez, quando a classe é carregada.
    // Ele lê o arquivo config.properties para obter a chave da API.
    static {
        try (InputStream input = ApiServico.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Arquivo 'config.properties' não encontrado no classpath.");
            }
            props.load(input);
        } catch (Exception ex) {
            // Lança uma exceção se não conseguir carregar o arquivo, encerrando o programa.
            throw new RuntimeException("Erro ao carregar o arquivo de configuração.", ex);
        }
    }

    /**
     * Classe interna (record) para mapear a resposta JSON da API.
     * O nome 'conversion_rates' deve ser idêntico ao campo no JSON.
     */
    private record ApiResponse(Map<String, Double> conversion_rates) {}

    /**
     * Busca as taxas de câmbio mais recentes da API.
     * @return Um Mapa contendo os códigos das moedas e suas respectivas taxas.
     * @throws Exception Se ocorrer um erro de rede ou na API.
     */
    public static Map<String, Double> obterTaxasDeCambio() throws Exception {
        // Lê a chave da API do arquivo de propriedades.
        String apiKey = props.getProperty("api.key");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("A 'api.key' não foi definida no arquivo config.properties.");
        }
        
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD";

        // Constrói a requisição HTTP.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        // Envia a requisição e armazena a resposta como String.
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Verifica se a requisição foi bem-sucedida (código 200 OK).
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na chamada da API: Código " + response.statusCode());
        }

        // Usa a biblioteca Gson para converter o texto JSON na nossa classe ApiResponse.
        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);
        
        return apiResponse.conversion_rates();
    }
}