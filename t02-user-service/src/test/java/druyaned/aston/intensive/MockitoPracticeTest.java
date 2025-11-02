package druyaned.aston.intensive;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MockitoPracticeTest {
    
    @Mock
    private List<String> mockedList;
    
    @Spy
    private Person spiedPerson = new Person();
    
    @Test
    public void testMockedList() {
        mockedList.add("abc");
        Mockito.verify(mockedList).add("abc");
        assertEquals(0, mockedList.size());
        Mockito.when(mockedList.size()).thenReturn(5);
        assertEquals(5, mockedList.size());
        assertEquals(5, mockedList.size());
    }
    
    @Test
    public void testSpiedPerson() {
        String edName = "Ed";
        String miaName = "Mia";
        LocalDate birthdate = LocalDate.of(2000, 9, 18);
        spiedPerson
                .setName(edName)
                .setBirthdate(birthdate);
        Mockito.verify(spiedPerson).setName(edName);
        Mockito.verify(spiedPerson).setBirthdate(birthdate);
        assertEquals(edName, spiedPerson.getName());
        assertEquals(birthdate, spiedPerson.getBirthdate());
        Mockito.doReturn("Mia").when(spiedPerson).getName();
        assertEquals(miaName, spiedPerson.getName());
        Mockito.doCallRealMethod().when(spiedPerson).getName();
        assertEquals(edName, spiedPerson.getName());
    }
}
