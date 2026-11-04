package br.com.rbs.gestaoempresas.service;

import java.io.InputStream;

public interface ArmazenamentoArquivos {

    void salvar(String caminho, InputStream arquivo, long tamanho);

    InputStream baixar(String caminho);

    void excluir(String caminho);
}