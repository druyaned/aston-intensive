package druyaned.aston.intensive.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Entity class according to the task requirement: "entity User with fields:
 * id, name, email, age, created_at". I asked to change the age by the
 * birthdate. The suggestion was accepted. So this entity satisfies the
 * conditions of the task and also implements Serializable interface for
 * well known purposes. I also decided to add validation here using
 * {@code hibernate-validator} dependency to avoid some wacky input.
 * 
 * @author druyaned
 * @see druyaned.aston.intensive.userservice.App
 * @see druyaned.aston.intensive.userservice.dao.UserDao
 */
@Entity
@Table(name = "Users")
public class User implements Serializable {
    
    private static final long serialVersionUID = -6613282189904272753L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 127)
    @NotNull(message = "Name can not be a null")
    @Size(min = 2, max = 127, message = "Name length should be in [2, 127]")
    private String name;
    
    @Column(nullable = false, unique = true, length = 320)
    @NotNull
    @Email(regexp = "^(?=.{1,64}@.{3,255}$)"
            + "[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    private String email;
    
    @Past
    private LocalDate birthdate;
    
    @Column(name = "created_at", nullable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;
    
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
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.id, ((User)obj).id);
    }
    
    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", name=" + name
                + ", email=" + email
                + ", birthdate=" + birthdate
                + ", createdAt=" + createdAt
                + '}';
    }
}
