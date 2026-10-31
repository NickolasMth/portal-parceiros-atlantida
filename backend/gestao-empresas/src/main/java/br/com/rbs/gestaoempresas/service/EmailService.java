
package br.com.rbs.gestaoempresas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remetente;
    private final String urlFrontend;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String remetente,
            @Value("${app.frontend.url}") String urlFrontend) {

        this.mailSender = mailSender;
        this.remetente = remetente;
        this.urlFrontend = urlFrontend;
    }

    public void enviarConvite(String destinatario, String token) {
        String link = UriComponentsBuilder
                .fromUriString(urlFrontend)
                .path("/aceitar-convite")
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Convite para cadastro");

        mensagem.setText(
                "Olá!\n\n"
                        + "Você recebeu um convite para se cadastrar "
                        + "no Portal de Parceiros.\n\n"
                        + "Para criar sua conta, acesse:\n"
                        + link + "\n\n"
                        + "Este convite é válido por 7 dias "
                        + "e só pode ser utilizado uma vez.\n\n"
                        + "Se você não esperava este convite, "
                        + "ignore esta mensagem."
        );

        mailSender.send(mensagem);
    }
}
