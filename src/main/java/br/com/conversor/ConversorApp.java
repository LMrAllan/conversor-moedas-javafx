package br.com.conversor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Classe principal que constrói e exibe a interface gráfica (GUI) do aplicativo.
 */
public class ConversorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tenta carregar as taxas da API na inicialização.
        try {
            ConversorMoedas.carregarTaxas();
        } catch (Exception e) {
            // Se falhar (ex: sem internet), exibe um alerta e fecha o app.
            exibirAlerta("Erro de Conexão", "Não foi possível carregar as taxas. Verifique sua conexão.");
            return;
        }

        primaryStage.setTitle("Conversor de Moedas Online via API");

        // --- Construção dos Componentes da Interface ---
        Label labelDe = new Label("Converter de:");
        Label labelPara = new Label("Para:");
        Label labelValor = new Label("Valor:");
        Label labelResultado = new Label("Resultado:");

        // Lista de moedas para exibição. O ideal seria carregar dinamicamente da API.
        String[] moedas = {"USD - Dólar", "EUR - Euro", "GBP - Libra", "JPY - Iene", "BRL - Real", "ARS - Peso Argentino"};
        ComboBox<String> deMoeda = new ComboBox<>();
        deMoeda.getItems().addAll(moedas);
        deMoeda.setValue("USD - Dólar");

        ComboBox<String> paraMoeda = new ComboBox<>();
        paraMoeda.getItems().addAll(moedas);
        paraMoeda.setValue("BRL - Real");

        TextField valorField = new TextField();
        valorField.setPromptText("Digite o valor");

        Button converterBtn = new Button("Converter");

        // --- Lógica do Botão de Conversão ---
        converterBtn.setOnAction(e -> {
            try {
                String valorTexto = valorField.getText().replace(",", ".");
                double valor = Double.parseDouble(valorTexto);

                double valorConvertido = ConversorMoedas.converter(deMoeda.getValue(), paraMoeda.getValue(), valor);
                String resultadoFormatado = String.format("%.2f", valorConvertido);
                labelResultado.setText("Resultado: " + resultadoFormatado);

            } catch (NumberFormatException ex) {
                exibirAlerta("Erro de Entrada", "Por favor, digite um número válido!");
            } catch (IllegalStateException ex) {
                exibirAlerta("Erro de Conversão", ex.getMessage());
            }
        });

        // --- Montagem do Layout em Grade ---
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(12);
        grid.setHgap(12);

        grid.add(labelDe, 0, 0);
        grid.add(deMoeda, 1, 0);
        grid.add(labelPara, 0, 1);
        grid.add(paraMoeda, 1, 1);
        grid.add(labelValor, 0, 2);
        grid.add(valorField, 1, 2);
        grid.add(converterBtn, 1, 3);
        grid.add(labelResultado, 1, 4);

        // --- Configuração e Exibição da Cena ---
        Scene scene = new Scene(grid, 420, 280);
        // Aplica o arquivo de estilo CSS à cena.
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Método auxiliar para exibir alertas de erro de forma padronizada.
     */
    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}