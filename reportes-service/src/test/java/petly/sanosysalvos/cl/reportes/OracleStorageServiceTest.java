package petly.sanosysalvos.cl.reportes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

import petly.sanosysalvos.cl.reportes.Services.OracleStorageService;

@ExtendWith(MockitoExtension.class)
class OracleStorageServiceTest {

    @Mock
    private ObjectStorageClient objectStorageClient;

    private OracleStorageService oracleStorageService;

    @BeforeEach
    void setUp() throws Exception {
        Constructor<OracleStorageService> constructor =
                OracleStorageService.class.getDeclaredConstructor(
                        ObjectStorageClient.class,
                        String.class,
                        String.class,
                        String.class
                );

        constructor.setAccessible(true);

        oracleStorageService = constructor.newInstance(
                objectStorageClient,
                "test-namespace",
                "test-bucket",
                "sa-santiago-1"
        );
    }

    @Test
    void subirImagenSubeArchivoYRetornaUrl() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "reporte.jpg",
                "image/jpeg",
                "contenido".getBytes()
        );

        String url = oracleStorageService.subirImagen(file);

        ArgumentCaptor<PutObjectRequest> captor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(objectStorageClient).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();

        assertThat(request.getNamespaceName()).isEqualTo("test-namespace");
        assertThat(request.getBucketName()).isEqualTo("test-bucket");
        assertThat(request.getObjectName()).endsWith("_reporte.jpg");
        assertThat(request.getContentType()).isEqualTo("image/jpeg");

        assertThat(url).contains("test-namespace");
        assertThat(url).contains("test-bucket");
        assertThat(url).contains("sa-santiago-1");
        assertThat(url).endsWith("_reporte.jpg");
    }

    @Test
    void eliminarImagenEliminaArchivoCorrectamente() {
    String imageUrl = "https://objectstorage.sa-santiago-1.oraclecloud.com/n/test-namespace/b/test-bucket/o/imagen123.jpg";

    oracleStorageService.eliminarImagen(imageUrl);

    ArgumentCaptor<DeleteObjectRequest> captor =
            ArgumentCaptor.forClass(DeleteObjectRequest.class);

    verify(objectStorageClient).deleteObject(captor.capture());

    DeleteObjectRequest request = captor.getValue();

    assertThat(request.getNamespaceName()).isEqualTo("test-namespace");
    assertThat(request.getBucketName()).isEqualTo("test-bucket");
    assertThat(request.getObjectName()).isEqualTo("imagen123.jpg");
}
}