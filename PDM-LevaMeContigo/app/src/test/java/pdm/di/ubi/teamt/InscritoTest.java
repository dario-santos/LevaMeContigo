package pdm.di.ubi.teamt;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Comentario;
import pdm.di.ubi.teamt.tables.Inscrito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class InscritoTest
{
    @Test
    public void createEmptyInscrito()
    {
        Inscrito i = new Inscrito();

        assertNull(i.getIdUser());
        assertNull(i.getIdPub());
        assert !i.getAccepted();
    }

    @Test
    public void createInscrito()
    {
        Inscrito i = new Inscrito("1", "3", false);

        assertEquals("1", i.getIdUser());
        assertEquals("3", i.getIdPub());
        assert !i.getAccepted();
    }

    @Test
    public void inscritoMapIsCorrect()
    {
        Inscrito i = new Inscrito("1", "3", false);
        Map<String, Object> result = i.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("idUser", "1");
        expected.put("idPub", "3");
        expected.put("accepted", false);

        assert expected.equals(result);
    }
}