package druyaned.aston.intensive.userservice.serve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import druyaned.aston.intensive.userservice.model.UserConversion;
import druyaned.aston.intensive.userservice.model.UserDto;
import druyaned.aston.intensive.userservice.model.UserEntity;
import druyaned.aston.intensive.userservice.repo.UserRepository;
import druyaned.aston.intensive.userservice.serve.UserService.Result;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepo;

    private UserService userService;

    @BeforeAll
    public static void setUpTestClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setUpTestMethod() {
        userService = new UserService(userRepo);
    }

    @Test
    public void getAllShouldReturnOkAndAllUsers() throws Exception {
        List<UserEntity> users = makeUsers();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(users, pageable, users.size());

        List<UserDto> expectedUserDtoList = page.stream().collect(ArrayList::new, (list, user)
                -> list.add(UserConversion.dtoFromEntity(user)), ArrayList::addAll);

        when(userRepo.findAll(any(Pageable.class))).thenReturn(page);

        List<UserDto> actualUserDtoList = userService.getAll(pageable);

        verify(userRepo).findAll(any(Pageable.class));

        assertIterableEquals(expectedUserDtoList, actualUserDtoList);
    }

    private List<UserEntity> makeUsers() {
        List<UserEntity> users = new ArrayList<>();

        users.add(makeUser());
        users.add(makeUser(2L, "Leo", "leo@alamail.com", "2001-08-17",
                "2025-10-26T03:30:26.76015+03"));
        users.add(makeUser(3L, "Kai", "kai@boombox.org", "2002-07-16",
                "2025-10-26T03:30:43.559921+03"));
        users.add(makeUser(4L, "Mia", "mia@beauty.net", null,
                "2025-10-26T03:31:03.376876+03"));
        users.add(makeUser(5L, "Ana", "ana@javadev.int", null,
                "2025-10-26T03:31:12.042545+03"));
        users.add(makeUser(6L, "Ira", "ira@eli.pali", null,
                "2025-11-01T21:45:21.921686+03"));

        return users;
    }

    @Test
    public void getShouldReturnNotFound() throws Exception {
        Long id = 1L;

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        Result getResult = userService.get(id);

        verify(userRepo).findById(1L);

        assertEquals(Result.notFound(id), getResult);
    }

    @Test
    public void getShouldReturnOkAndUserEntity() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserConversion.dtoFromEntity(user);
        Result expectedResult = Result.found(userDto);

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        Result actualResult = userService.get(user.getId());

        verify(userRepo).findById(user.getId());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void createByExistingEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = UserConversion.dtoFromEntity(makeUser());
        Result expectedResult = Result.emailDuplication(userDto.getEmail());

        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(true);

        Result actualResult = userService.create(userDto);

        verify(userRepo).existsByEmail(userDto.getEmail());
        verify(userRepo, never()).save(any(UserEntity.class));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void createShouldReturnCreated() throws Exception {
        UserEntity savedUser = makeUser();
        UserDto userDto = UserConversion.dtoFromEntity(savedUser);
        Result expectedResult = Result.created(userDto);

        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepo.save(any(UserEntity.class))).thenReturn(savedUser);

        Result actualResult = userService.create(userDto);

        verify(userRepo).existsByEmail(userDto.getEmail());
        verify(userRepo).save(any(UserEntity.class));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void updateByNonExistingIdShouldReturnNotFound() throws Exception {
        UserDto userDto = UserConversion.dtoFromEntity(makeUser());
        Result expectedResult = Result.notFound(userDto.getId());

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.empty());

        Result result = userService.update(userDto.getId(), userDto);

        verify(userRepo).findById(userDto.getId());
        verify(userRepo, never()).existsByEmail(anyString());
        verify(userRepo, never()).save(any(UserEntity.class));

        assertEquals(expectedResult, result);
    }

    @Test
    public void updateByExistingEmailShouldReturnBadRequest() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserConversion.dtoFromEntity(user);

        String existingEmail = "existing@email.com";
        userDto.setEmail(existingEmail);

        Result expectedResult = Result.emailDuplication(existingEmail);

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(existingEmail)).thenReturn(true);

        Result actualResult = userService.update(userDto.getId(), userDto);

        verify(userRepo).findById(user.getId());
        verify(userRepo).existsByEmail(existingEmail);
        verify(userRepo, never()).save(any(UserEntity.class));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void updateShouldReturnOk() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserConversion.dtoFromEntity(user);

        String otherEmail = "other@email.com";
        userDto.setEmail(otherEmail);

        Result expectedResult = Result.updated(userDto);

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(otherEmail)).thenReturn(false);
        when(userRepo.save(user)).thenReturn(user);

        Result actualResult = userService.update(userDto.getId(), userDto);

        verify(userRepo).findById(user.getId());
        verify(userRepo).existsByEmail(otherEmail);
        verify(userRepo).save(user);

        assertEquals(otherEmail, user.getEmail());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deleteByNonExistingIdShouldReturnNotFound() throws Exception {
        Long id = 2L;
        Result expectedResult = Result.notFound(id);

        when(userRepo.findById(id)).thenReturn(Optional.empty());

        Result actualResult = userService.delete(id);

        verify(userRepo).findById(id);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deleteShouldReturnOk() throws Exception {
        UserEntity user = makeUser();
        UserDto userDto = UserConversion.dtoFromEntity(user);
        Result expectedResult = Result.deleted(userDto);

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepo).deleteById(user.getId());

        Result actualResult = userService.delete(user.getId());

        verify(userRepo).findById(user.getId());
        verify(userRepo).deleteById(user.getId());

        assertEquals(expectedResult, actualResult);
    }

    private UserEntity makeUser() {
        return makeUser(1L, "Max", "max@example.com", null, "2025-10-26T03:30:14.172079+03");
    }

    private UserEntity makeUser(Long id, String name, String email, String birthdate,
            String createdAt) {

        UserEntity user = new UserEntity();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthdate == null
                ? null
                : LocalDate.parse(birthdate));
        user.setCreatedAt(OffsetDateTime.parse(createdAt).withOffsetSameInstant(ZoneOffset.UTC));

        return user;
    }
}
