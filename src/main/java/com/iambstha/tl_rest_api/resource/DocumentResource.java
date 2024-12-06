package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.dto.DocumentResDto;
import com.iambstha.tl_rest_api.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Tag(name = "Blog Management")
@RestController
@RequestMapping("v1/api/document")
@RequiredArgsConstructor
public class DocumentResource {

    @Autowired
    private final DocumentService service;

    @Operation(summary = "Download blog post thumbnail by id")
    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Resource> downloadDocumentById(@PathVariable("documentId") Long documentId) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {

        DocumentResDto documentResponse = service.getDocumentById(documentId);

        File file = service.downloadDocument(documentResponse.getFileName(), documentResponse.getUser().getUserId().toString());
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        InputStream in = getClass().getResourceAsStream(file.getAbsolutePath());
        String headerValue = "inline; filename=\"" + documentResponse.getOriginalFileName() + "\"";
        file.delete();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documentResponse.getFileContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

}
