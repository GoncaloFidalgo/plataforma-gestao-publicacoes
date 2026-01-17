package app.ejbs;

import app.dtos.OllamaRequest;
import app.dtos.OllamaResponse;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Stateless
public class OllamaBean {

    private static final Logger logger = Logger.getLogger(OllamaBean.class.getName());
    private String ollamaUrl;
    private String ollamaModel;

    @PostConstruct
    public void init() {
        // Lê as variáveis de ambiente definidas no docker-compose.yml
        // Se não encontrar, usa valores por defeito
        ollamaUrl = System.getenv("OLLAMA_API_URL");
        if (ollamaUrl == null) ollamaUrl = "http://ollama:11434/api/generate";

        ollamaModel = System.getenv("OLLAMA_MODEL");
        if (ollamaModel == null) ollamaModel = "tinyllama";
    }

    public String generateSummary(String titulo, String descricao) {
        // Criar um prompt simples
        String promptText = "Resuma o seguinte texto académico em português de Portugal (max 3 linhas). Título: "
                + titulo + ". Conteúdo: " + descricao;

        OllamaRequest requestBody = new OllamaRequest(ollamaModel, promptText);

        try (Client client = ClientBuilder.newClient()) {
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
            return "Erro de comunicação com o serviço de IA.";
        }
    }
}