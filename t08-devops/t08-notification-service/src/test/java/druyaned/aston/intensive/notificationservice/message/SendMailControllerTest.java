package druyaned.aston.intensive.notificationservice.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import druyaned.aston.intensive.notificationservice.web.MailMessageDto;
import druyaned.aston.intensive.notificationservice.web.SendMailController;
import jakarta.mail.internet.AddressException;
import static org.hamcrest.Matchers.startsWith;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests of {@link SendMailController}.
 *
 * @author druyaned
 */
@WebMvcTest(SendMailController.class)
@ActiveProfiles("test")
public class SendMailControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailMessageHandler mailMessageHandler;

    @BeforeAll
    public static void setUpTestClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void sendInvalidMailShouldReturnInternalServerError() throws Exception {
        String email = "very_bad_email";
        String message = "Some very important message";

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setEmail(email);
        mailMessageDto.setMessage(message);

        doThrow(new AddressException("Invalid Addresses"))
                .when(mailMessageHandler).handle(email, message);

        mockMvc
                .perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailMessageDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(startsWith("Failed to send email: ")));

        verify(mailMessageHandler).handle(email, message);
    }

    @Test
    public void sendValidMailShouldReturnOk() throws Exception {
        String email = "email@example.com";
        String message = "Some very important message";

        MailMessageDto mailMessageDto = new MailMessageDto();
        mailMessageDto.setEmail(email);
        mailMessageDto.setMessage(message);

        doNothing().when(mailMessageHandler).handle(email, message);

        mockMvc
                .perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailMessageDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email is sent to " + email));

        verify(mailMessageHandler).handle(email, message);
    }
}
