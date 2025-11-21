package br.com.gabxdev.domain.model;

public class Contrato {
    private String cpf;

    private String id;

    private String nome;

    private String cidade;

    private String rua;

    private String status;

    private String cep;


    public Contrato() {
    }

    public Contrato(String cpf, String id, String nome, String cidade, String rua, String status, String cep) {
        this.cpf = cpf;
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.rua = rua;
        this.status = status;
        this.cep = cep;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
