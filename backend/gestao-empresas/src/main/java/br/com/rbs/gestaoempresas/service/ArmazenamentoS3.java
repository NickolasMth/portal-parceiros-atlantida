package br.com.rbs.gestaoempresas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;

@Component
@ConditionalOnProperty(
        name = "app.storage.tipo",
        havingValue = "s3"
)
public class ArmazenamentoS3 implements ArmazenamentoArquivos {

    private final S3Client s3Client;
    private final String bucket;

    public ArmazenamentoS3(
            @Value("${app.storage.s3.endpoint}") String endpoint,
            @Value("${app.storage.s3.region}") String region,
            @Value("${app.storage.s3.access-key}") String accessKey,
            @Value("${app.storage.s3.secret-key}") String secretKey,
            @Value("${app.storage.s3.bucket}") String bucket
    ) {
        this.bucket = bucket;

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

    @Override
    public void salvar(String caminho, InputStream arquivo, long tamanho) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(caminho)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(arquivo, tamanho)
        );
    }

    @Override
    public InputStream baixar(String caminho) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(caminho)
                .build();

        return s3Client.getObject(request);
    }

    @Override
    public void excluir(String caminho) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(caminho)
                .build();

        s3Client.deleteObject(request);
    }
}