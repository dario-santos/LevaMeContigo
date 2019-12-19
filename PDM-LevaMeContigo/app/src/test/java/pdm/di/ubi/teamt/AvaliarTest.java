package pdm.di.ubi.teamt;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Avaliar;
import pdm.di.ubi.teamt.tables.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AvaliarTest
{
    @Test
    public void createEmptyAvaliar()
    {
        Avaliar a = new Avaliar();

        assertNull(a.getIdUserAvaliador());
        assertNull(a.getNomeAvaliador());
        assertNull(a.getIdUserAvaliado());
        assertNull(a.getIdPub());
        assertNull(a.getComentario());
        assert a.getNota() == 0F;
    }

    @Test
    public void createAvaliar()
    {
        Avaliar a = new Avaliar("1", "Dario Santos", "2", "3", "MUITO BOM.", 1);

        assertEquals("1", a.getIdUserAvaliador());
        assertEquals("Dario Santos",a.getNomeAvaliador());
        assertEquals("2", a.getIdUserAvaliado());
        assertEquals("3", a.getIdPub());
        assertEquals("MUITO BOM.", a.getComentario());
        assert a.getNota() == 1F;
    }

    @Test
    public void avaliarMapIsCorrect()
    {
        Avaliar a = new Avaliar("1", "Dario Santos", "2", "3", "MUITO BOM.", 1);
        Map<String, Object> result = a.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("idUserAvaliador", "1");
        expected.put("nomeAvaliador", "Dario Santos");
        expected.put("idUserAvaliado", "2");
        expected.put("idPub", "3");
        expected.put("comentario", "MUITO BOM.");
        expected.put("nota", 1F);

        assert expected.equals(result);
    }
}