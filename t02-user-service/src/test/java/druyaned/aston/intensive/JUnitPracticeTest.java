package druyaned.aston.intensive;

import java.time.LocalDate;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JUnitPracticeTest {
    
    public static Stream<Arguments> makeFullFilledPersons() {
        return Stream.of(
                Arguments.of(true, Person.create("Max",
                        LocalDate.of(2000, 9, 18))),
                Arguments.of(true, Person.create("Leo",
                        LocalDate.of(2001, 8, 17))),
                Arguments.of(true, Person.create("Mia",
                        LocalDate.of(2002, 7, 16))),
                Arguments.of(true, Person.create("Ana",
                        LocalDate.of(2003, 6, 15)))
        );
    }
    
    @ParameterizedTest
    @MethodSource("makeFullFilledPersons")
    void personCreate_shouldReturnFullFilledPersons(boolean expected,
            Person person) {
        assertEquals(expected, person.isFullFilled());
    }
}

class Person {
    
    private String name;
    private LocalDate birthdate;
    
    public static Person create(String name, LocalDate birthdate) {
        return new Person().setName(name).setBirthdate(birthdate);
    }
    
    public String getName() {
        return name;
    }
    
    public Person setName(String name) {
        this.name = name;
        return this;
    }
    
    public LocalDate getBirthdate() {
        return birthdate;
    }
    
    public Person setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }
    
    public boolean isFullFilled() {
        return name != null && !name.isEmpty() && birthdate != null;
    }
}
