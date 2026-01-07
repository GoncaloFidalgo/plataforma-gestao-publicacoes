package app.ws;

import app.dtos.PublicacaoCreateDTO;
import app.dtos.PublicacaoDTO;
import app.ejbs.PublicacaoBean;
import app.ejbs.UserBean;
import app.entities.Publicacao;
import app.entities.User;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityNotFoundException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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
    @RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
    public Response upload(MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

            // Obter metadata
            List<InputPart> metadataParts = uploadForm.get("metadata");
            if (metadataParts == null || metadataParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("metadata is missing").build();
            }
            String metadataJson = metadataParts.get(0).getBodyAsString();

            // Map JSON to DTO
            ObjectMapper mapper = new ObjectMapper();
            PublicacaoCreateDTO createDTO = mapper.readValue(metadataJson, PublicacaoCreateDTO.class);

            // Obter ficheiro
            List<InputPart> fileParts = uploadForm.get("file");
            if (fileParts == null || fileParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("file is missing").build();
            }
            InputPart filePart = fileParts.get(0);

            String originalName = getFileName(filePart.getHeaders());
            String extension = "";
            int i = originalName.lastIndexOf('.');
            if (i > 0) {
                extension = originalName.substring(i);
            }

            if (!extension.equalsIgnoreCase(".pdf") && !extension.equalsIgnoreCase(".zip")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("File must be PDF or ZIP").build();
            }

            // Guardar ficheiro
            String storedFileName = UUID.randomUUID().toString() + extension;
            java.nio.file.Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            InputStream inputStream = filePart.getBody(InputStream.class, null);
            Files.copy(inputStream, path.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);

            // Criar a publicação na BD
            String creatorUsername = securityContext.getUserPrincipal().getName();

            Publicacao publicacao = publicacaoBean.create(
                    createDTO.getTitulo(),
                    createDTO.getTipo(),
                    createDTO.getAutores(),
                    createDTO.getArea_cientifica(),
                    createDTO.getDescricao(),
                    storedFileName,
                    createDTO.getTags(),
                    creatorUsername
            );

            if (Boolean.TRUE.equals(createDTO.getHidden())) {
                publicacao.setHidden(true);
            }

            return Response.status(Response.Status.CREATED)
                    .entity(PublicacaoDTO.from(publicacao))
                    .build();

        } catch (MyConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("File upload failed: " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unknown error").build();
        }
    }

    @GET
    @Path("/{id}/ficheiro")
    public Response download(@PathParam("id") Long id) {
        Publicacao publicacao = publicacaoBean.find(id);

        if (publicacao == null || publicacao.getFile() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Publication or file not found")
                    .build();
        }

        java.nio.file.Path filePath = Paths.get(UPLOAD_DIR, publicacao.getFile());
        File file = filePath.toFile();

        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found on server")
                    .build();
        }

        String extension = getExtension(publicacao.getFile());
        String sanitizedTitle = publicacao.getTitulo().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String downloadName = sanitizedTitle + extension;

        // Ver se é pdf ou zip
        String contentType = MediaType.APPLICATION_OCTET_STREAM;
        if (extension.equalsIgnoreCase(".pdf")) {
            contentType = "application/pdf";
        } else if (extension.equalsIgnoreCase(".zip")) {
            contentType = "application/zip";
        }

        return Response.ok(file, contentType)
                .header("Content-Disposition", "attachment; filename=\"" + downloadName + "\"")
                .build();
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return (i > 0) ? filename.substring(i) : "";
    }


    @GET
    @Path("/{id}")
    public Response getDetails(@PathParam("id") Long id) {
        Publicacao p = publicacaoBean.find(id);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(PublicacaoDTO.from(p)).build();
    }
    @GET
    public Response getAll(
            @QueryParam("tag") String tag,
            @QueryParam("titulo") String titulo,
            @QueryParam("autor") String autor,
            @QueryParam("area") String area,
            @QueryParam("uploader") String uploader,
            @QueryParam("hidden") Boolean hidden
    ) {
        List<Publicacao> publicacoes = publicacaoBean.findAll(tag, titulo, autor, area, uploader, hidden);

        List<PublicacaoDTO> dtos = publicacoes.stream()
                .map(PublicacaoDTO::from)
                .collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    @GET
    @Path("{id}/comments")
    public Response getComentarios(@PathParam("id") Long id) {
        Publicacao p = publicacaoBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        List<PublicacaoDTO.ComentarioDTO> comments = p.getComentarios().stream()
                .map(PublicacaoDTO.ComentarioDTO::from)
                .collect(Collectors.toList());
        return Response.ok(comments).build();
    }

    @GET
    @Path("{id}/history")
    public Response getHistorico(@PathParam("id") Long id) {
        Publicacao p = publicacaoBean.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        List<PublicacaoDTO.HistoricoEdicaoDTO> history = p.getHistoricoEdicoes().stream()
                .map(PublicacaoDTO.HistoricoEdicaoDTO::from)
                .collect(Collectors.toList());
        return Response.ok(history).build();
    }

   /* @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePublication(@PathParam("id") Long id, Map<String, Object> payload) {
        try {
            String titulo = (String) payload.get("titulo");
            String areaCientifica = (String) payload.get("area_cientifica");
            String descricao = (String) payload.get("descricao");
            String resumo = (String) payload.get("resumo");
            String tags = (String) payload.get("tags"); // Expecting "Tag1, Tag2"
            Boolean hidden = payload.get("hidden") != null ? (Boolean) payload.get("hidden") : null;

            String editorUsername = securityContext.getUserPrincipal().getName();

            // Handle 'autor' legacy logic if needed (omitted for brevity, keep your original if needed)
            String autor = (String) payload.get("autor");

            var publicacao = publicacaoBean.update(
                    id,
                    titulo,
                    autor,
                    areaCientifica,
                    descricao,
                    resumo,
                    tags,
                    hidden,
                    editorUsername // Pass current user for History
            );

            if (publicacao == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensagem\": \"Publicação não encontrada\"}")
                        .build();
            }

            return Response.ok(PublicacaoDTO.from(publicacao)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao processar o pedido\"}")
                    .build();
        }
    }*/

   /* @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        var publicacao = publicacaoBean.find(id);
        if (publicacao == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            publicacaoBean.delete(id);

            if (publicacao.getFile() != null && !publicacao.getFile().isBlank()) {
                java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR, publicacao.getFile());
                java.nio.file.Files.deleteIfExists(filePath);
            }

            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao apagar publicação\"}")
                    .build();
        }
    }*/


    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
    }
}

