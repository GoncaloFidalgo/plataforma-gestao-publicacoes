package app.ejbs;

import app.dtos.OllamaRequest;
import app.dtos.OllamaResponse;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

@Stateless
public class OllamaBean {

    private static final Logger logger = Logger.getLogger(OllamaBean.class.getName());
    private String ollamaUrl;
    private String ollamaModel;

    @PostConstruct
    public void init() {
        ollamaUrl = System.getenv("OLLAMA_API_URL");
        if (ollamaUrl == null) ollamaUrl = "http://ollama:11434/api/generate";

        ollamaModel = System.getenv("OLLAMA_MODEL");
        if (ollamaModel == null) ollamaModel = "tinyllama";
    }

    // ADICIONE ESTA ANOTAÇÃO AQUI
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String generateSummary(String titulo, String descricao) {
        String promptText = "Resuma o seguinte texto académico em português de Portugal (max 3 linhas). Título: "
                + titulo + ". Conteúdo: " + descricao;

        OllamaRequest requestBody = new OllamaRequest(ollamaModel, promptText);

        try (Client client = ClientBuilder.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()) {

            Response response = client.target(ollamaUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {
                OllamaResponse result = response.readEntity(OllamaResponse.class);
                return result.getResponse();
            } else {
                logger.severe("Erro Ollama: " + response.getStatus());
                return "Erro ao gerar resumo (Status " + response.getStatus() + ")";
            }
        } catch (Exception e) {
            logger.severe("Erro de conexão com IA: " + e.getMessage());
            e.printStackTrace();
            return "Erro de comunicação com o serviço de IA.";
        }
    }
}