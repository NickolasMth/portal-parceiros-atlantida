
package br.com.rbs.gestaoempresas.service;

import br.com.rbs.gestaoempresas.dto.ConviteDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ConviteEmailListener {

    private static final Logger log =
            LoggerFactory.getLogger(ConviteEmailListener.class);

    private final EmailService emailService;

    public ConviteEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoCriarConvite(ConviteDtos.ConviteCriadoEvent evento) {
        try {
            emailService.enviarConvite(
                    evento.email(),
                    evento.token()
            );
        } catch (RuntimeException ex) {
            // Não registrar o token nem as credenciais SMTP.
            log.error(
                    "Falha ao enviar convite para o destinatário {}",
                    evento.email()
            );

        }
    }
}
