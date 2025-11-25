package druyaned.aston.intensive.userservice.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.serve.UserService;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
    private static final MediaType HAL_JSON = MediaType.parseMediaType("application/hal+json");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserModelAssembler userModelAssembler;

    @BeforeAll
    public static void setUpTestClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void getAllShouldReturnOkAndAllUsers() throws Exception {
        List<UserDto> userDtoList = makeUserDtoList();
        CollectionModel<EntityModel<UserDto>> collectionModel = new UserModelAssembler()
                .toCollectionModel(userDtoList);

        when(userService.getAll(any(Pageable.class))).thenReturn(userDtoList);
        when(userModelAssembler.toCollectionModel(anyList())).thenReturn(collectionModel);

        MockHttpServletResponse response = mockMvc.perform(
                get("/user-service/users").accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andReturn()
                .getResponse();

        verify(userService).getAll(any(Pageable.class));
        verify(userModelAssembler).toCollectionModel(anyList());

        JsonNode root = objectMapper.readTree(response.getContentAsString());
        JsonNode embedded = root.path("_embedded");

        JsonNode usersArray = null;
        Iterator<String> fieldNames = embedded.fieldNames();
        if (embedded.isObject() && fieldNames.hasNext()) {
            usersArray = embedded.path(fieldNames.next());
        }

        List<UserDto> responseUserDtoList = new ArrayList<>();
        if (usersArray != null && usersArray.isArray()) {
            for (JsonNode userNode : usersArray) {
                UserDto userDto = objectMapper.readValue(userNode.toString(), UserDto.class);
                responseUserDtoList.add(userDto);
            }
        }

        for (UserDto userDto : userDtoList) {
            OffsetDateTime createdAt = userDto.getCreatedAt();
            userDto.setCreatedAt(createdAt.withOffsetSameInstant(ZoneOffset.UTC));
        }

        assertEquals(userDtoList.size(), responseUserDtoList.size());
        assertTrue(responseUserDtoList.containsAll(userDtoList));
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
        Result notFoundResult = Result.notFound(id);

        when(userService.get(anyLong())).thenReturn(notFoundResult);

        mockMvc.perform(
                get("/user-service/user/1").accept(HAL_JSON))
                .andExpect(status().isNotFound());

        verify(userService).get(anyLong());
        verify(userModelAssembler, never()).toModel(any(UserDto.class));
    }

    @Test
    public void getShouldReturnOkAndUserEntity() throws Exception {
        UserDto userDto = makeUserDto();
        Result foundResult = Result.found(userDto);
        EntityModel<UserDto> entityModel = new UserModelAssembler().toModel(userDto);

        when(userService.get(anyLong())).thenReturn(foundResult);
        when(userModelAssembler.toModel(any(UserDto.class))).thenReturn(entityModel);

        mockMvc.perform(
                get("/user-service/user/" + userDto.getId()).accept(HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(HAL_JSON))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService).get(anyLong());
        verify(userModelAssembler).toModel(any(UserDto.class));
    }

    @Test
    public void createByExistingEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = makeUserDto();
        Result emailDuplicationResult = Result.emailDuplication(userDto.getEmail());

        when(userService.create(any(UserDto.class))).thenReturn(emailDuplicationResult);

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
        Result createdResult = Result.created(userDto);
        String expectedLocation = "http://localhost/user-service/create/user/" + userDto.getId();

        when(userService.create(any(UserDto.class))).thenReturn(createdResult);

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
        Result notFoundResult = Result.notFound(userDto.getId());

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(notFoundResult);

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
        Result emailDuplicationResult = Result.emailDuplication(userDto.getEmail());

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(emailDuplicationResult);

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
        Result updatedResult = Result.updated(userDto);

        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(updatedResult);

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
        Result notFoundResult = Result.notFound(id);

        when(userService.delete(anyLong())).thenReturn(notFoundResult);

        mockMvc.perform(
                delete("/user-service/delete/" + id))
                .andExpect(status().isNotFound());

        verify(userService).delete(anyLong());
    }

    @Test
    public void deleteShouldReturnOk() throws Exception {
        UserDto userDto = makeUserDto();
        Result deletedResult = Result.deleted(userDto);

        when(userService.delete(anyLong())).thenReturn(deletedResult);

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
