package pdm.di.ubi.teamt;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.Comentario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ComantarioTest
{
    @Test
    public void createEmptyComentario()
    {
        Comentario c = new Comentario();

        assertNull(c.getIdUser());
        assertNull(c.getUserName());
        assertNull(c.getIdPub());
        assertNull(c.getComentario());
    }

    @Test
    public void createComentario()
    {
        Comentario c = new Comentario("1","Dario Santos","3","ISTO PASSA PELA MINHA CASA?");

        assertEquals("1", c.getIdUser());
        assertEquals("Dario Santos", c.getUserName());
        assertEquals("3", c.getIdPub());
        assertEquals("ISTO PASSA PELA MINHA CASA?", c.getComentario());
    }

    @Test
    public void comentarioMapIsCorrect()
    {
        Comentario c = new Comentario("1","Dario Santos","3","ISTO PASSA PELA MINHA CASA?");
        Map<String, Object> result = c.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("idUser", "1");
        expected.put("userName", "Dario Santos");
        expected.put("idPub", "3");
        expected.put("comentario", "ISTO PASSA PELA MINHA CASA?");

        assert expected.equals(result);
    }
}