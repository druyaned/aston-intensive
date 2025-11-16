package druyaned.aston.intensive.userservice.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import druyaned.aston.intensive.userservice.model.UserMapper;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.notify.UserEvent;
import druyaned.aston.intensive.userservice.repo.UserRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepo;

    @MockitoBean
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @BeforeAll
    public static void setUpTestClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void getAllShouldReturnOkAndAllUsers() throws Exception {
        List<UserEntity> users = makeUsers();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(users, pageable, users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = mockMvc
                .perform(get("/user-service/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        verify(userRepo).findAll(any(Pageable.class));

        List<UserEntity> responseUsers = objectMapper.readValue(
                response.getContentAsString(),
                new TypeReference<List<UserEntity>>() {});

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(users.size(), responseUsers.size());
        assertTrue(users.containsAll(responseUsers));
    }

    private List<UserEntity> makeUsers() {
        List<UserEntity> list = new ArrayList<>();

        list.add(makeUser());
        list.add(makeUser(2L, "Leo", "leo@alamail.com", "2001-08-17",
                "2025-10-26T03:30:26.76015+03"));
        list.add(makeUser(3L, "Kai", "kai@boombox.org", "2002-07-16",
                "2025-10-26T03:30:43.559921+03"));
        list.add(makeUser(4L, "Mia", "mia@beauty.net", null,
                "2025-10-26T03:31:03.376876+03"));
        list.add(makeUser(5L, "Ana", "ana@javadev.int", null,
                "2025-10-26T03:31:12.042545+03"));
        list.add(makeUser(6L, "Ira", "ira@eli.pali", null,
                "2025-11-01T21:45:21.921686+03"));

        return list;
    }

    @Test
    public void getShouldReturnNotFound() throws Exception {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/user-service/user/1"))
                .andExpect(status().isNotFound());

        verify(userRepo).findById(1L);
    }

    @Test
    public void getShouldReturnOkAndUserEntity() throws Exception {
        UserEntity user = makeUser();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user-service/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(userRepo).findById(user.getId());
    }

    @Test
    public void createByExistingEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = UserMapper.dtoFromEntity(makeUser());
        String expectedContent = "Email \"" + userDto.getEmail() + "\" exists";

        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(true);

        mockMvc.perform(
                post("/user-service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedContent));

        verify(userRepo).existsByEmail(userDto.getEmail());
        verify(userRepo, never()).save(any(UserEntity.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(UserEvent.class));
    }

    @Test
    public void createShouldReturnCreated() throws Exception {
        UserEntity savedUser = makeUser();
        UserDto userDto = UserMapper.dtoFromEntity(savedUser);
        String expectedLocation = "http://localhost/user-service/create/user/"
                + savedUser.getId();

        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepo.save(any(UserEntity.class))).thenReturn(savedUser);
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(any(CompletableFuture.class));

        mockMvc.perform(
                post("/user-service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", expectedLocation));

        verify(userRepo).existsByEmail(userDto.getEmail());
        verify(userRepo).save(any(UserEntity.class));
        verify(kafkaTemplate).send(anyString(), anyString(), any(UserEvent.class));
    }

    @Test
    public void updateByNonExistingIdShouldReturnNotFound() throws Exception {
        UserDto userDto = UserMapper.dtoFromEntity(makeUser());

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                put("/user-service/update/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userRepo).findById(userDto.getId());
        verify(userRepo, never()).existsByEmail(anyString());
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateByExistingEmailShouldReturnBadRequest() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserMapper.dtoFromEntity(user);

        String existingEmail = "existing@email.com";
        userDto.setEmail(existingEmail);

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(existingEmail)).thenReturn(true);

        mockMvc.perform(
                put("/user-service/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email \""
                        + existingEmail + "\" exists"));

        verify(userRepo).findById(user.getId());
        verify(userRepo).existsByEmail(existingEmail);
        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    public void updateShouldReturnOk() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserMapper.dtoFromEntity(user);

        String otherEmail = "other@email.com";
        userDto.setEmail(otherEmail);

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(otherEmail)).thenReturn(false);
        when(userRepo.save(user)).thenReturn(user);

        mockMvc.perform(
                put("/user-service/update/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));

        verify(userRepo).findById(user.getId());
        verify(userRepo).existsByEmail(otherEmail);
        verify(userRepo).save(user);

        assertEquals(otherEmail, user.getEmail());
    }

    @Test
    public void deleteByNonExistingIdShouldReturnNotFound() throws Exception {
        Long id = 2L;

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/user-service/delete/" + id))
                .andExpect(status().isNotFound());

        verify(userRepo).findById(id);
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(UserEvent.class));
    }

    @Test
    public void deleteShouldReturnOk() throws Exception {
        UserEntity user = makeUser();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepo).deleteById(user.getId());
        when(kafkaTemplate.send(anyString(), anyString(), any(UserEvent.class)))
                .thenReturn(any(CompletableFuture.class));

        mockMvc.perform(delete("/user-service/delete/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));

        verify(userRepo).findById(user.getId());
        verify(userRepo).deleteById(user.getId());
        verify(kafkaTemplate).send(anyString(), anyString(), any(UserEvent.class));
    }

    private UserEntity makeUser() {
        return makeUser(1L, "Max", "max@example.com", null,
                "2025-10-26T03:30:14.172079+03");
    }

    private UserEntity makeUser(
            Long id,
            String name,
            String email,
            String birthdate,
            String createdAt) {

        UserEntity user = new UserEntity();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthdate == null
                ? null
                : LocalDate.parse(birthdate));
        user.setCreatedAt(OffsetDateTime.parse(createdAt));

        return user;
    }
}
