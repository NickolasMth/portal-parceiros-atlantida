package br.com.rbs.gestaoempresas.config;

import br.com.rbs.gestaoempresas.entity.Usuario;
import br.com.rbs.gestaoempresas.repository.PerfilRepository;
import br.com.rbs.gestaoempresas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnExpression("'${app.admin.email:}' != ''")
public class AdminInicial implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final PerfilRepository perfis;
    private final PasswordEncoder encoder;
    private final String email;
    private final String senha;

    public AdminInicial(UsuarioRepository usuarios, PerfilRepository perfis, PasswordEncoder encoder,
                        @Value("${app.admin.email}") String email, @Value("${app.admin.senha}") String senha) {
        this.usuarios = usuarios; this.perfis = perfis; this.encoder = encoder;
        this.email = email; this.senha = senha;
    }

    @Override
    public void run(String... args) {
        if (usuarios.findByEmailIgnoreCase(email).isPresent()) return;
        Usuario u = new Usuario();
        u.setNome("Admin RBS");
        u.setEmail(email);
        u.setSenhaHash(encoder.encode(senha));
        u.setStatus("ATIVO");
        u.setPerfil(perfis.findByCodigo("ADMIN_RBS").orElseThrow());
        usuarios.save(u);
    }
}
