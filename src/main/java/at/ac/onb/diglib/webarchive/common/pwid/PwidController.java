package at.ac.onb.diglib.webarchive.common.pwid;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.ac.onb.diglib.webarchive.common.pwid.data.Archive;
import at.ac.onb.diglib.webarchive.common.pwid.data.Resolver;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class PwidController {
    private static final Logger log = LoggerFactory.getLogger(PwidController.class);

    protected Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * The /heartbeat endpoint allows to verify, that the PwidController is up and
     * running.
     *
     * Example call:
     * curl -v http://localhost:8080/heartbeat
     */
    @GetMapping("/heartbeat")
    public void heartbeat() {
    }

    /**
     * The /heartbeat endpoint allows to verify, that the PwidController is up and
     * running.
     *
     * Example call:
     * curl -v http://localhost:8080/heartbeat
     */
    @GetMapping("/")
    @Operation(summary = "", description = "The index page.", hidden = false)
    public ResponseEntity<String> index() {
        HashMap<String, Object> scopes = new HashMap<String, Object>();
        scopes.put("apiBasePath", getDefaultPwidEndpoint().toString());

        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("index.mustache");
        mustache.execute(writer, scopes);
        return new ResponseEntity<String>(writer.toString(), HttpStatus.OK);
    }

    /**
     * The /pwid endpoint converts a PWID or an ArchiveUrl to a pwid Object, which
     * includes all extracted Infos and the resolving url.
     *
     * Example calls:
     * curl -v
     * "http://localhost:8080/pwid?archiveString=urn:pwid:webarchiv.onb.ac.at:2013-12-03T17:03:03Z:page:http://m.onb.ac.at/prunksaal.htm"
     * curl -v
     * "http://localhost:8080/pwid?archiveString=https://webarchiv.onb.ac.at/web/20131203170303/http://m.onb.ac.at/prunksaal.htm"
     *
     * curl -v
     * "http://localhost:8080/pwid?archiveString=urn:pwid:archive.org:2022-11-27T18:33:21Z:page:https://www.iana.org/assignments/urn-formal/pwid"
     * curl -v
     * "http://localhost:8080/pwid?archiveString=https://web.archive.org/web/20221127183321/https://www.iana.org/assignments/urn-formal/pwid"
     */
    @GetMapping(path = "/pwid")
    @Operation(summary = "", description = "This method converts a pwid or a ArchiveUrl to a pwid Object, which includes all extracted Infos and the resolving url.", hidden = false)
    @Parameters({
            @Parameter(name = "Authorization", description = "authorization header", required = true, schema = @Schema(implementation = String.class, defaultValue = "Bearer "), in = ParameterIn.HEADER),
            @Parameter(name = "X-API-VERSION", description = "API Version", required = false, schema = @Schema(implementation = String.class, defaultValue = "0.2.0"), in = ParameterIn.HEADER)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request. Maybe pwid or Url is invalid"),
            @ApiResponse(responseCode = "422", description = "URL can not to be resolved. Archive is not supported"),
            @ApiResponse(responseCode = "200", description = "returns the pwid object", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PWID.class)) })
    })
    public ResponseEntity<String> pwid(@RequestParam("archiveString") String aArchiveString) {
        try {
            PWID pwid = PwidResolver.resolveAny(aArchiveString, new PwidRegistry(), getDefaultResolver());
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(pwid));
        } catch (PwidUnsupportedException e) {
            log.error("PwidUnsupportedException");
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception e) {
            log.error("Exception");
            log.debug(e.getClass().getCanonicalName());
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * The /resolve Endpoint takes a PWID or an archive url and resolves it to the
     * replay page.
     *
     * Example calls:
     * curl -v
     * "http://localhost:8080/resolve?pwid=urn:pwid:webarchiv.onb.ac.at:2013-12-03T17:03:03Z:page:http://m.onb.ac.at/prunksaal.htm"
     * curl -v
     * "http://localhost:8080/resolve?pwid=https://webarchiv.onb.ac.at/web/20131203170303/http://m.onb.ac.at/prunksaal.htm"
     *
     * curl -v
     * "http://localhost:8080/resolve?pwid=urn:pwid:archive.org:2022-11-27T18:33:21Z:page:https://www.iana.org/assignments/urn-formal/pwid"
     * curl -v
     * "http://localhost:8080/resolve?pwid=https://web.archive.org/web/20221127183321/https://www.iana.org/assignments/urn-formal/pwid"
     */
    @GetMapping(path = "/resolve")
    @Operation(summary = "", description = "This method convert a pwid or a ArchiveUrl via a pwid Objectto a replay URL", hidden = false)
    @Parameters({
            @Parameter(name = "Authorization", description = "authorization header", required = true, schema = @Schema(implementation = String.class, defaultValue = "Bearer "), in = ParameterIn.HEADER),
            @Parameter(name = "X-API-VERSION", description = "API Version", required = false, schema = @Schema(implementation = String.class, defaultValue = "0.2.0"), in = ParameterIn.HEADER)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request. Maybe pwid or Url is invalid"),
            @ApiResponse(responseCode = "422", description = "URL can not to be resolved. Archive is not supported"),
            @ApiResponse(responseCode = "200", description = "returns the pwid object", content = {
                    @Content(mediaType = "text/html") })
    })
    public ResponseEntity<String> resolve(@RequestParam("pwid") String aArchiveString) {
        try {
            PwidRegistry registry = new PwidRegistry();
            Resolver defaultResolver = getDefaultResolver();
            PWID pwid = PwidResolver.resolveAny(aArchiveString, registry, defaultResolver);
            PwidResolver pwidResolver = new PwidResolver(registry, defaultResolver);

            HashMap<String, Object> scopes = new HashMap<String, Object>();
            scopes.put("resolvedUrl", pwid.resolvedUrl);
            scopes.put("pwid", pwid.urn);
            scopes.put("resolverUrl", pwid.resolvingUri);
            Iterator<String> archiveIds = registry.getArchiveIds().iterator();
            List<HashMap<String, Object>> archives = new ArrayList<HashMap<String, Object>>();
            for (; archiveIds.hasNext();) {
                String archiveId = archiveIds.next();
                Archive archive = registry.getArchive(archiveId);
                PWID otherPwid = pwid.clone();
                otherPwid.setArchiveId(archiveId);
                otherPwid = otherPwid.clone();
                otherPwid = pwidResolver.resolve(otherPwid);

                HashMap<String, Object> archivePwid = new HashMap<String, Object>();
                archivePwid.put("archive", archive);
                archivePwid.put("archive_pwid_urn", otherPwid.getUrn());
                archivePwid.put("archive_pwid_replay", otherPwid.resolvedUrl);
                archives.add(archivePwid);
            }
            scopes.put("archives", archives);

            Writer writer = new StringWriter();
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile("resolve.mustache");
            mustache.execute(writer, scopes);
            return new ResponseEntity<String>(writer.toString(), HttpStatus.OK);
        } catch (PwidUnsupportedException e) {
            log.error("PwidUnsupportedException");
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception e) {
            log.error("Exception");
            log.debug(e.getClass().getCanonicalName());
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private Resolver getDefaultResolver() {
        return new Resolver("", ServletUriComponentsBuilder.fromCurrentContextPath().path("").build().toUriString(),
                "/resolve?pwid={{pwid}}");
    }

    private URI getDefaultPwidEndpoint() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("pwid").build().toUri();
    }
}