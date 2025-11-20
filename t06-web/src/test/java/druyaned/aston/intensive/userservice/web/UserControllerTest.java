package druyaned.aston.intensive.userservice.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.CREATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.DELETED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.NOT_UPDATED;
import static druyaned.aston.intensive.userservice.serve.UserService.Result.Type.UPDATED;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeAll
    public static void setUpTestClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void getAllShouldReturnOkAndAllUsers() throws Exception {
        List<UserDto> userDtoList = makeUserDtoList();
        Result<List<UserDto>> result = new Result<>(Result.Type.FOUND, "", userDtoList);

        when(userService.getAll(any(Pageable.class))).thenReturn(result);

        MockHttpServletResponse response = mockMvc
                .perform(get("/user-service/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        verify(userService).getAll(any(Pageable.class));

        TypeReference<List<UserDto>> typeReference = new TypeReference<List<UserDto>>() {
        };
        List<UserDto> responseUsers = objectMapper.readValue(response.getContentAsString(),
                typeReference);

        assertEquals(userDtoList.size(), responseUsers.size());

        for (UserDto userDto : userDtoList) {
            OffsetDateTime createdAt = userDto.getCreatedAt();
            userDto.setCreatedAt(createdAt.withOffsetSameInstant(ZoneOffset.UTC));
        }
        assertTrue(userDtoList.containsAll(responseUsers));
    }

    private List<UserDto> makeUserDtoList() {
        List<UserDto> userDtoList = new ArrayList<>();

        userDtoList.add(makeUserDto());
        userDtoList.add(makeUserDto(2L, "Leo", "leo@alamail.com", "2001-08-17",
                "2025-10-26T03:30:26.76015+03"));
        userDtoList.add(makeUserDto(3L, "Kai", "kai@boombox.org", "2002-07-16",
                "2025-10-26T03:30:43.559921+03"));
        userDtoList.add(makeUserDto(4L, "Mia", "mia@beauty.net", null,
                "2025-10-26T03:31:03.376876+03"));
        userDtoList.add(makeUserDto(5L, "Ana", "ana@javadev.int", null,
                "2025-10-26T03:31:12.042545+03"));
        userDtoList.add(makeUserDto(6L, "Ira", "ira@eli.pali", null,
                "2025-11-01T21:45:21.921686+03"));

        return userDtoList;
    }

    @Test
    public void getShouldReturnNotFound() throws Exception {
        Long id = 1L;
        Result<UserDto> result = Result.notFound(id);

        when(userService.get(anyLong())).thenReturn(result);

        mockMvc.perform(get("/user-service/user/1")).andExpect(status().isNotFound());

        verify(userService).get(anyLong());
    }

    @Test
    public void getShouldReturnOkAndUserEntity() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(Result.Type.FOUND, "", userDto);

        when(userService.get(anyLong())).thenReturn(result);

        mockMvc.perform(get("/user-service/user/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService).get(anyLong());
    }

    @Test
    public void createByExistingEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(Result.Type.NOT_CREATED, "", null);

        when(userService.create(any(UserDto.class))).thenReturn(result);

        mockMvc.perform(
                post("/user-service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService).create(any(UserDto.class));
    }

    @Test
    public void createShouldReturnCreated() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(CREATED, "", userDto);
        String expectedLocation = "http://localhost/user-service/create/user/" + userDto.getId();

        when(userService.create(any(UserDto.class))).thenReturn(result);

        mockMvc.perform(
                post("/user-service/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", expectedLocation));

        verify(userService).create(any(UserDto.class));
    }

    @Test
    public void updateByNonExistingIdShouldReturnNotFound() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = Result.notFound(userDto.getId());

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(result);

        mockMvc.perform(
                put("/user-service/update/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userService).update(anyLong(), any(UserDto.class));
    }

    @Test
    public void updateByExistingEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(NOT_UPDATED, "", null);

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(result);

        mockMvc.perform(
                put("/user-service/update/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService).update(anyLong(), any(UserDto.class));
    }

    @Test
    public void updateShouldReturnOk() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(UPDATED, "", userDto);

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(result);

        mockMvc.perform(
                put("/user-service/update/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userService).update(anyLong(), any(UserDto.class));
    }

    @Test
    public void deleteByNonExistingIdShouldReturnNotFound() throws Exception {
        Long id = 2L;
        Result<UserDto> result = Result.notFound(id);

        when(userService.delete(anyLong())).thenReturn(result);

        mockMvc.perform(delete("/user-service/delete/" + id))
                .andExpect(status().isNotFound());

        verify(userService).delete(anyLong());
    }

    @Test
    public void deleteShouldReturnOk() throws Exception {
        UserDto userDto = makeUserDto();
        Result<UserDto> result = new Result<>(DELETED, "", userDto);

        when(userService.delete(anyLong())).thenReturn(result);

        mockMvc.perform(delete("/user-service/delete/" + userDto.getId()))
                .andExpect(status().isOk());

        verify(userService).delete(anyLong());
    }

    private UserDto makeUserDto() {
        return makeUserDto(1L, "Max", "max@example.com", null, "2025-10-26T03:30:14.172079+03");
    }

    private UserDto makeUserDto(Long id, String name, String email, String birthdate,
            String createdAt) {

        UserDto user = new UserDto();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthdate == null ? null : LocalDate.parse(birthdate));
        user.setCreatedAt(OffsetDateTime.parse(createdAt));

        return user;
    }
}
