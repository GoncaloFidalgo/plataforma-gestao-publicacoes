package app.ws;

import app.dtos.PublicacaoDTO;
import app.ejbs.PublicacaoBean;
import app.ejbs.UserBean;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.security.Authenticated;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("publications")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class PublicacaoService {

    @EJB
    private PublicacaoBean publicacaoBean;

    @EJB
    private UserBean userBean;

    @Context
    private SecurityContext securityContext;

    private static final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/publications/";

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPublication(MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
            
            String metadataJson = getFormData(formDataMap, "metadata");
            if (metadataJson == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"Metadata is required\"}")
                        .build();
            }

            InputPart filePart = getFilePart(formDataMap, "file");
            if (filePart == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"File is required\"}")
                        .build();
            }

            String username = securityContext.getUserPrincipal().getName();
            User user = userBean.find(username);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            String originalFilename = filePart.getHeaders().getFirst("Content-Disposition");
            originalFilename = extractFilename(originalFilename);
            
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            if (!fileExtension.equalsIgnoreCase(".pdf") && !fileExtension.equalsIgnoreCase(".zip")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"File must be PDF or ZIP\"}")
                        .build();
            }

            String storedFilename = UUID.randomUUID().toString() + fileExtension;
            java.nio.file.Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            
            InputStream fileInputStream = filePart.getBody(InputStream.class, null);
            Files.copy(fileInputStream, uploadPath.resolve(storedFilename), StandardCopyOption.REPLACE_EXISTING);

            Map<String, Object> metadata = parseMetadata(metadataJson);
            String titulo = (String) metadata.get("titulo");
            String tipo = (String) metadata.get("tipo");
            List<String> autores = (List<String>) metadata.get("autores");
            String areaCientifica = (String) metadata.get("area_cientifica");
            String descricao = (String) metadata.get("descricao");
            Boolean hidden = metadata.get("hidden") != null ? (Boolean) metadata.get("hidden") : false;

            String autor = autores != null && !autores.isEmpty() ? String.join(", ", autores) : "";
            String resumo = descricao != null ? descricao : "";

            var publicacao = publicacaoBean.create(
                    titulo,
                    autor,
                    areaCientifica,
                    descricao,
                    storedFilename,
                    resumo,
                    username,
                    user.getName()
            );

            if (hidden != null) {
                publicacao.setHidden(hidden);
            }

            var responseDTO = PublicacaoDTO.from(publicacao);
            return Response.status(Response.Status.CREATED).entity(responseDTO).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (MyConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensagem\": \"Dados inválidos\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao criar publicação\"}")
                    .build();
        }
    }


    //TODO FALTA SO DEIXAR FAZER ISTO OS RESPONSAVEIS.
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicacao(@PathParam("id") Long id) {
        var publicacao = publicacaoBean.find(id);

        if (publicacao == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensagem\": \"Publicação não encontrada\"}")
                    .build();
        }

        var dto = PublicacaoDTO.from(publicacao);

        return Response.ok(dto).build();
    }


    @GET
    @Path("/{id}/ficheiro")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") Long id) {
        var publicacao = publicacaoBean.find(id);
        if (publicacao == null || publicacao.getFile() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Publicação ou ficheiro não encontrado")
                    .build();
        }

        java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR, publicacao.getFile());
        java.io.File file = filePath.toFile();

        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Ficheiro físico não encontrado no servidor")
                    .build();
        }

        String niceFilename = "documento_" + id + getExtension(publicacao.getFile());

        return Response.ok(file)
                .header("Content-Disposition", "attachment; filename=\"" + niceFilename + "\"")
                .build();
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".pdf";
        return filename.substring(filename.lastIndexOf("."));
    }



    private String getFormData(Map<String, List<InputPart>> formDataMap, String key) {
        List<InputPart> parts = formDataMap.get(key);
        if (parts == null || parts.isEmpty()) {
            return null;
        }
        try {
            return parts.get(0).getBodyAsString();
        } catch (Exception e) {
            return null;
        }
    }

    private InputPart getFilePart(Map<String, List<InputPart>> formDataMap, String key) {
        List<InputPart> parts = formDataMap.get(key);
        if (parts == null || parts.isEmpty()) {
            return null;
        }
        return parts.get(0);
    }

    private String extractFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return "file";
        }
        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("filename")) {
                String filename = part.split("=")[1].trim();
                return filename.replace("\"", "");
            }
        }
        return "file";
    }

    private Map<String, Object> parseMetadata(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid metadata JSON");
        }
    }
}

