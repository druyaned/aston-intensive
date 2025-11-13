package druyaned.aston.intensive.userservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "Name can not be null")
    @Size(min = 2, max = 127, message = "Name length should be in [2, 127]")
    private String name;

    @NotNull(message = "Email can not be null")
    @Email(regexp = "^(?=.{1,64}@.{3,255}$)"
            + "[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @Past
    private LocalDate birthdate;

    @PastOrPresent
    private OffsetDateTime createdAt;

    public UserDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserDto{"
                + "id=" + id
                + ", name=" + name
                + ", email=" + email
                + ", birthdate=" + birthdate
                + ", createdAt=" + createdAt
                + '}';
    }
}
