package petly.sanosysalvos.cl.mascotas.Services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

@Service
public class OracleStorageService {

    private final ObjectStorageClient client;

    private final String namespace = System.getenv("OCI_NAMESPACE");
    private final String bucketName = System.getenv("OCI_BUCKET");
    private final String region = System.getenv("OCI_REGION");

    public OracleStorageService() {

        try {
            AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                    .userId(System.getenv("OCI_USER_ID"))
                    .tenantId(System.getenv("OCI_TENANCY_ID"))
                    .fingerprint(System.getenv("OCI_FINGERPRINT"))
                    .privateKeySupplier(() -> {
                        try {
                            return new FileInputStream(System.getenv("OCI_PRIVATE_KEY_PATH"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .region(com.oracle.bmc.Region.fromRegionId(region))
                    .build();

            this.client = ObjectStorageClient.builder().build(provider);

        } catch (Exception e) {
            throw new RuntimeException("Error configurando OCI", e);
        }
    }

    public String subirImagen(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("Archivo vacío");
        }

        String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(nombreArchivo)
                .putObjectBody(file.getInputStream())
                .contentType(file.getContentType())
                .build();

        client.putObject(request);

        return "https://objectstorage." + region + ".oraclecloud.com/n/"
                + namespace + "/b/" + bucketName + "/o/" + nombreArchivo;
    }
}
