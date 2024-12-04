package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.dto.DocumentResDto;
import com.iambstha.tl_rest_api.dto.ThumbnailDetails;
import com.iambstha.tl_rest_api.entity.Document;
import com.iambstha.tl_rest_api.exception.FileSizeExceededException;
import com.iambstha.tl_rest_api.exception.FileUploadException;
import com.iambstha.tl_rest_api.exception.NotAllowedException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.repository.DocumentRepository;
import com.iambstha.tl_rest_api.util.FileUtility;
import com.iambstha.tl_rest_api.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    @Autowired
    private final DocumentRepository documentRepository;

    @Autowired
    private final UserService userService;

    @Value("${tl.upload-file-path}")
    private String uploadPath;

    @Value("${tl.file-encryption-password}")
    private String fileEncryptionPassword;

    @Value("${tl.file-encryption-salt}")
    private String fileEncryptionSalt;

    Document handleDocument(ThumbnailDetails thumbnailDetails, Document document) {

        try {
            if(thumbnailDetails.getDocumentTypeId() == null){
                return null;
            }
            if(thumbnailDetails.getFile() == null){
                throw new FileUploadException("File not found");
            }
            if (thumbnailDetails.getFile().getSize() > 200000000) {
                throw new FileSizeExceededException("File size has exceeded the limit.");
            }

            byte[] content = thumbnailDetails.getFile().getBytes();
            document.setContent(content);

            String fileName = uploadDocument(thumbnailDetails.getFile(), Objects.requireNonNull(UserUtil.getUserId()).toString());
            document.setFileName(fileName);
            document.setStatus(1);
            document.setOriginalFileName(thumbnailDetails.getFile().getOriginalFilename());
            document.setFileContentType(thumbnailDetails.getFile().getContentType());


            document.setUser(userService.getUserActualById(UserUtil.getUserId()));
            return documentRepository.save(document);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            throw new RuntimeException(e);
        }


    }

    public DocumentResDto getDocumentById(Long documentId) {

        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new RecordNotFoundException("Document not found");
        }
        return this.buildDocumentTypeResponse(document.get());
    }

    private String uploadDocument(MultipartFile file, String userId) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKey key = FileUtility.getKeyFromPassword(fileEncryptionPassword, fileEncryptionSalt);
        Path dirPath = Paths.get(uploadPath + File.separator + userId);
        if (!dirPath.toFile().exists()) {
            boolean res = dirPath.toFile().mkdir();
            if (!res) {
                throw new NotAllowedException("Could not create directory");
            }
        }

        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        int index = originalFileName.lastIndexOf('.');
        String extension = (index > 0) ? originalFileName.substring(index + 1) : null;
        String fileName = FileUtility.getDateTimeFilename(extension);

        while (new File(dirPath + File.separator + fileName).isFile()) {
            fileName = FileUtility.getDateTimeFilename(extension);
        }

        File inputFile = FileUtility.multipartToFile(file, fileName);
        String algorithm = "AES/CBC/PKCS5Padding";
        IvParameterSpec ivParameterSpec = FileUtility.generateIv();

        File encryptedFile = new File(dirPath + File.separator + fileName);
        FileUtility.encryptFile(algorithm, key, ivParameterSpec, inputFile, encryptedFile);
        boolean deleted = inputFile.delete();

        return fileName;
    }

    public File downloadDocument(String fileName, String userId) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, IOException,
            BadPaddingException, InvalidKeyException {

        SecretKey key = FileUtility.getKeyFromPassword(fileEncryptionPassword, fileEncryptionSalt);
        String algorithm = "AES/CBC/PKCS5Padding";
        IvParameterSpec ivParameterSpec = FileUtility.generateIv();
        File encryptedFile = new File(uploadPath + File.separator + userId + File.separator + fileName);
        File decryptedFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        FileUtility.decryptFile(algorithm, key, ivParameterSpec, encryptedFile, decryptedFile);

        return decryptedFile;
    }

    private DocumentResDto buildDocumentTypeResponse(Document document) {

        return DocumentResDto.builder()
                .documentId(document.getDocumentId())
                .fileName(document.getFileName())
                .fileContentType(document.getFileContentType())
                .originalFileName(document.getOriginalFileName())
                .user(document.getUser())
                .status(document.getStatus())
                .deleted(document.getDeleted())
                .build();

    }

}
