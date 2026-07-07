package ar.utn.ba.ddsi.mailing.services.impl;

import ar.utn.ba.ddsi.mailing.models.entities.Email;
import ar.utn.ba.ddsi.mailing.models.repositories.IEmailRepository;
import ar.utn.ba.ddsi.mailing.services.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final IEmailRepository emailRepository;
    private final JavaMailSender mailSender;

    public EmailService(IEmailRepository emailRepository, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.mailSender = mailSender;
    }

    @Override
    public Email enviarEmail(Email email) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(email.getRemitente());
            mensaje.setTo(email.getDestinatario());
            mensaje.setSubject(email.getAsunto());
            mensaje.setText(email.getContenido());

            mailSender.send(mensaje);
            email.setEnviado(true);
            logger.info("Email enviado a {}", email.getDestinatario());
        } catch (MailException e) {
            email.setEnviado(false);
            logger.error("Error al enviar email a {}: {}", email.getDestinatario(), e.getMessage());
        }
        return emailRepository.save(email);
    }

    @Override
    public List<Email> obtenerEmails(Boolean pendiente) {
        if (pendiente != null) {
            return emailRepository.findByEnviado(!pendiente);
        }
        return emailRepository.findAll();
    }
}
