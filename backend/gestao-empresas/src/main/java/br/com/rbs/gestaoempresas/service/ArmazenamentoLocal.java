package br.com.rbs.gestaoempresas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@ConditionalOnProperty(
        name = "app.storage.tipo",
        havingValue = "local",
        matchIfMissing = true
)
public class ArmazenamentoLocal implements ArmazenamentoArquivos {

    private final Path diretorio;

    public ArmazenamentoLocal(
            @Value("${app.storage.dir}") String diretorio
    ) {
        this.diretorio = Paths.get(diretorio)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public void salvar(
            String caminho,
            InputStream arquivo,
            long tamanho
    ) {
        try {
            Path destino = diretorio.resolve(caminho).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new IllegalArgumentException("Caminho inválido");
            }

            Files.createDirectories(destino.getParent());

            Files.copy(
                    arquivo,
                    destino,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    @Override
    public InputStream baixar(String caminho) {
        try {
            Path destino = diretorio.resolve(caminho).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new IllegalArgumentException("Caminho inválido");
            }

            return Files.newInputStream(destino);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar arquivo", e);
        }
    }

    @Override
    public void excluir(String caminho) {
        try {
            Path destino = diretorio.resolve(caminho).normalize();

            if (!destino.startsWith(diretorio)) {
                throw new IllegalArgumentException("Caminho inválido");
            }

            Files.deleteIfExists(destino);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao excluir arquivo", e);
        }
    }
}