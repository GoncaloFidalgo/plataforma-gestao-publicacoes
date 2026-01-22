package app.ws;

import app.dtos.comments.CommentCreateDTO;
import app.dtos.comments.CommentDTO;
import app.dtos.comments.CommentVisibilityDTO;
import app.dtos.publication.PublicacaoCreateDTO;
import app.dtos.publication.PublicacaoDTO;
import app.dtos.rating.RatingRequestDTO;
import app.dtos.rating.RatingResponseDTO;
import app.ejbs.CommentBean;
import app.ejbs.PublicacaoBean;
import app.ejbs.RatingBean;
import app.ejbs.UserBean;
import app.entities.Comment;
import app.entities.Publicacao;
import app.entities.Rating;
import app.exceptions.MyConstraintViolationException;
import app.exceptions.MyEntityExistsException;
import app.exceptions.MyEntityNotFoundException;
import app.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import app.ejbs.OllamaBean;
@Path("publications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class PublicacaoService {

    @EJB private PublicacaoBean publicacaoBean;
    @EJB private UserBean userBean;
    @EJB private RatingBean ratingBean;
    @EJB private CommentBean commentBean;

    @EJB
    private OllamaBean ollamaBean;

    @Context
    private SecurityContext securityContext;

    private static final Logger logger = Logger.getLogger(PublicacaoService.class.getName());
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
            String extension = getExtension(originalName);


            if (!extension.equalsIgnoreCase(".pdf") && !extension.equalsIgnoreCase(".zip")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("File must be PDF or ZIP").build();
            }

            // Criar a publicação na BD
            InputStream inputStream = filePart.getBody(InputStream.class, null);
            String creatorUsername = securityContext.getUserPrincipal().getName();

            Publicacao publicacao = publicacaoBean.create(
                    createDTO.getTitulo(),
                    createDTO.getTipoId(),
                    createDTO.getAutores(),
                    createDTO.getAreaId(),
                    createDTO.getDescricao(),
                    inputStream,
                    extension,
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

        if (publicacao == null || publicacao.getFilename() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Publication or file not found")
                    .build();
        }

        File file = publicacaoBean.getFile(id);

        if (file == null || !file.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found on server").build();
        }

        String extension = getExtension(publicacao.getFilename());
        String sanitizedTitle = publicacao.getTitulo().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String downloadName = sanitizedTitle + extension;

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
    public Response getAllComments(@PathParam("id") Long id, @QueryParam("hidden") Boolean hidden) {
        List<Comment> comments = commentBean.findAll(id, hidden);
        return Response.ok(CommentDTO.from(comments)).build();
    }
    @POST
    @Path("{id}/comments")
    public Response createComment(@PathParam("id") Long id, CommentCreateDTO dto) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            Comment comment = commentBean.create(id, username, dto.getComment());

            return Response.status(Response.Status.CREATED)
                    .entity(CommentDTO.from(comment))
                    .build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("{id}/comments/{commentId}")
    public Response updateComment(@PathParam("id") Long id, @PathParam("commentId") Long commentId, CommentCreateDTO dto) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            Comment comment = commentBean.update(id, commentId, dto.getComment(), username);

            return Response.ok(CommentDTO.from(comment)).build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("{id}/comments/{commentId}/visibility")
    @RolesAllowed({"Administrator", "Responsavel"})
    public Response updateCommentVisibility(@PathParam("id") Long id, @PathParam("commentId") Long commentId, CommentVisibilityDTO dto) {
        try {
            commentBean.updateVisibility(id, commentId, dto.getHidden(), dto.getMotive());
            return Response.ok("{\"mensagem\": \"Visibilidade do comentário atualizada com sucesso\"}").build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}/comments/{commentId}")
    public Response deleteComment(@PathParam("id") Long id, @PathParam("commentId") Long commentId) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            commentBean.delete(id, commentId, username);
            return Response.ok("{\"mensagem\": \"Comentário removido com sucesso\"}").build();
        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
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

    @POST
    @Path("{id}/resumir")
    @RolesAllowed({"Administrator", "Responsavel", "Colaborador"})
    public Response gerarResumoAutomatico(@PathParam("id") Long id) {
        try {
            Publicacao publicacao = publicacaoBean.find(id);
            if (publicacao == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            StringBuilder conteudoCompleto = new StringBuilder();
            if (publicacao.getDescricao() != null) {
                conteudoCompleto.append(publicacao.getDescricao()).append("\n");
            }

            if (publicacao.getFilename() != null && publicacao.getFilename().toLowerCase().endsWith(".pdf")) {
                String pdfText = extractContentFromPdf(publicacao.getFilename());
                if (pdfText != null && !pdfText.isBlank()) {
                    conteudoCompleto.append("\n--- Excerto do PDF ---\n").append(pdfText);
                }
            }

            String textoFinal = conteudoCompleto.toString();

            if (textoFinal.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensagem\": \"Não há conteúdo suficiente (descrição ou PDF) para gerar um resumo.\"}")
                        .build();
            }

            String resumoGerado = ollamaBean.generateSummary(publicacao.getTitulo(), textoFinal);

            publicacaoBean.updateResumo(id, resumoGerado);

            return Response.ok()
                    .entity("{\"resumo\": \"" + resumoGerado.replace("\"", "\\\"").replace("\n", " ") + "\"}")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensagem\": \"Erro ao processar resumo: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Método auxiliar para extrair texto do PDF
     */
    private String extractContentFromPdf(String filename) {
        try {
            java.nio.file.Path filePath = Paths.get(UPLOAD_DIR, filename);
            File file = filePath.toFile();

            if (!file.exists()) {
                logger.warning("Ficheiro PDF não encontrado para resumo: " + filename);
                return null;
            }

            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();


                stripper.setStartPage(1);
                stripper.setEndPage(5);

                String text = stripper.getText(document);

                if (text.length() > 4000) {
                    text = text.substring(0, 4000) + "... [texto truncado]";
                }
                return text;
            }
        } catch (IOException e) {
            logger.severe("Erro ao ler PDF (" + filename + "): " + e.getMessage());
            return null;
        }
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

   @GET
   @Path("{id}/ratings")
   public Response getRatings(@PathParam("id") Long id) {
       Publicacao p = publicacaoBean.find(id);
       if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

       List<PublicacaoDTO.RatingDTO> ratings = p.getRatings().stream()
               .map(PublicacaoDTO.RatingDTO::from)
               .collect(Collectors.toList());
       return Response.ok(ratings).build();
   }

   @POST
   @Path("{id}/ratings")
   @RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
   public Response addRating(@PathParam("id") Long id, RatingRequestDTO dto) {
       try {
           String username = securityContext.getUserPrincipal().getName();

           // Validar input (1-5)
           if (dto.getRating() < 1 || dto.getRating() > 5) {
               return Response.status(Response.Status.BAD_REQUEST).entity("Rating deve ser entre 1 e 5").build();
           }

           double newAverage = ratingBean.create(id, username, dto.getRating());
           int count = ratingBean.getCount(id);

           return Response.status(Response.Status.CREATED)
                   .entity(new RatingResponseDTO(newAverage, count))
                   .build();

       } catch (MyEntityExistsException e) {
           return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
       } catch (MyEntityNotFoundException e) {
           return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
       }
   }

    // EP48 - Atualizar Rating (PATCH)
    @PATCH
    @Path("{id}/ratings/{ratingId}")
    @RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
    public Response updateRating(@PathParam("id") Long id, @PathParam("ratingId") Long ratingId, RatingRequestDTO dto) {
        try {
            String username = securityContext.getUserPrincipal().getName();

            if (dto.getRating() < 1 || dto.getRating() > 5) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Rating deve ser entre 1 e 5").build();
            }

            double newAverage = ratingBean.update(id, ratingId, dto.getRating(), username);
            int count = ratingBean.getCount(id);

            return Response.ok()
                    .entity(new RatingResponseDTO(newAverage, count))
                    .build();

        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // EP49 - Remover Rating (DELETE)
    @DELETE
    @Path("{id}/ratings/{ratingId}")
    @RolesAllowed({"Colaborador", "Responsavel", "Administrator"})
    public Response removeRating(@PathParam("id") Long id, @PathParam("ratingId") Long ratingId) {
        try {
            double newAverage = ratingBean.delete(id, ratingId);
            int count = ratingBean.getCount(id);

            return Response.ok()
                    .entity(new RatingResponseDTO(newAverage, count))
                    .build();

        } catch (MyEntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }
    @GET
    @Path("{id}/ratings/me")
    public Response getMyRating(@PathParam("id") Long id) {
        String username = securityContext.getUserPrincipal().getName();
        Rating rating = ratingBean.findRatingByUser(id, username);

        if (rating == null) {
            return Response.noContent().build();
        }

        return Response.ok(PublicacaoDTO.RatingDTO.from(rating)).build();
    }
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

