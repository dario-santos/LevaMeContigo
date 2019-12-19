package pdm.di.ubi.teamt;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.User;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserTest
{
    @Test
    public void createEmptyUser()
    {
        User u = new User();

        assertNull(u.getNome());
        assertNull(u.getDataInscricao());
    }

    @Test
    public void createUser()
    {
        User u = new User("Dario Santos", "2012-12-01");

        assertEquals("Dario Santos", u.getNome());
        assertEquals("2012-12-01", u.getDataInscricao());
    }

    @Test
    public void userMapIsCorrect()
    {
        User u = new User("Dario Santos", "2012-12-01");
        Map<String, Object> result = u.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("nome", "Dario Santos");
        expected.put("dataInscricao", "2012-12-01");

        assert expected.equals(result);
    }
}