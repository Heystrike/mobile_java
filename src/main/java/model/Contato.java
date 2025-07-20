package model;

/**
 * Modelo de dados para Contato
 * Utiliza o padrão Builder para construção flexível
 */
public class Contato {
    private final String nome;
    private final String telefone;
    private final String email;
    private final String empresa;
    
    private Contato(Builder builder) {
        this.nome = builder.nome;
        this.telefone = builder.telefone;
        this.email = builder.email;
        this.empresa = builder.empresa;
    }
    
    // Getters
    public String getNome() {
        return nome;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getEmpresa() {
        return empresa;
    }
    
    /**
     * Builder para construção de objetos Contato
     */
    public static class Builder {
        private String nome = "";
        private String telefone = "";
        private String email = "";
        private String empresa = "";
        
        public Builder nome(String nome) {
            this.nome = nome != null ? nome : "";
            return this;
        }
        
        public Builder telefone(String telefone) {
            this.telefone = telefone != null ? telefone : "";
            return this;
        }
        
        public Builder email(String email) {
            this.email = email != null ? email : "";
            return this;
        }
        
        public Builder empresa(String empresa) {
            this.empresa = empresa != null ? empresa : "";
            return this;
        }
        
        public Contato build() {
            return new Contato(this);
        }
    }
    
    @Override
    public String toString() {
        return "Contato{" +
                "nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", empresa='" + empresa + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Contato contato = (Contato) obj;
        return nome.equals(contato.nome) &&
                telefone.equals(contato.telefone) &&
                email.equals(contato.email) &&
                empresa.equals(contato.empresa);
    }
    
    @Override
    public int hashCode() {
        int result = nome.hashCode();
        result = 31 * result + telefone.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + empresa.hashCode();
        return result;
    }
}