package com.cqq._3dobject.service;

import com.cqq._3dobject.entity.ModelFile;
import com.cqq._3dobject.repository.ModelFileRepository;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelFileServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void bindFileUrlShouldCreateModelFileRecordForUploadedGlbPath() {
        ModelFileRepository repository = mock(ModelFileRepository.class);
        when(repository.save(any(ModelFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModelFileService service = new ModelFileService();
        ReflectionTestUtils.setField(service, "modelFileRepository", repository);

        ModelFile saved = service.bindFileUrl(
                76L,
                "/uploads/models/76/model.glb",
                null,
                null,
                null,
                null
        );

        assertEquals(76L, saved.getMid());
        assertEquals("uploads/models/76/model.glb", saved.getFilePath());
        assertEquals("GLB", saved.getFileType());
        assertEquals("model.glb", saved.getOriginalName());
        assertEquals("model/gltf-binary", saved.getContentType());
    }

    @Test
    void bindFileUrlShouldDownloadZipAndStoreExtractedGlb() throws Exception {
        ModelFileRepository repository = mock(ModelFileRepository.class);
        when(repository.save(any(ModelFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ModelFileService service = new ModelFileService();
        ReflectionTestUtils.setField(service, "modelFileRepository", repository);
        ReflectionTestUtils.setField(service, "baseUploadDir", tempDir);

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        byte[] zipBytes = zipWithModelGlb();
        server.createContext("/model.glb", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, zipBytes.length);
            exchange.getResponseBody().write(zipBytes);
            exchange.close();
        });
        server.start();

        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/model.glb?token=abc";
            ModelFile saved = service.bindFileUrl(76L, url, "GLB", "model.glb", null, null);

            assertEquals(76L, saved.getMid());
            assertEquals("GLB", saved.getFileType());
            assertEquals("model.glb", saved.getOriginalName());
            assertEquals("model/gltf-binary", saved.getContentType());
            assertTrue(saved.getFilePath().startsWith("uploads/models/76/"));
            assertTrue(saved.getFilePath().endsWith(".glb"));

            String storedName = saved.getFilePath().substring(saved.getFilePath().lastIndexOf('/') + 1);
            Path storedFile = tempDir.resolve("76").resolve(storedName);
            assertTrue(Files.exists(storedFile));
            assertEquals(4L, Files.size(storedFile));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void filePathColumnShouldSupportLongRemoteUrls() throws Exception {
        Column filePathColumn = ModelFile.class.getDeclaredField("filePath").getAnnotation(Column.class);

        assertEquals("TEXT", filePathColumn.columnDefinition());
    }

    private byte[] zipWithModelGlb() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            zipOutputStream.putNextEntry(new ZipEntry("result/model.glb"));
            zipOutputStream.write(new byte[]{'g', 'l', 'T', 'F'});
            zipOutputStream.closeEntry();
        }
        return outputStream.toByteArray();
    }
}
