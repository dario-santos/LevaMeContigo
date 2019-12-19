package pdm.di.ubi.teamt;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import pdm.di.ubi.teamt.tables.Publicacao;
import pdm.di.ubi.teamt.tables.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PublicacaoTest
{
    @Test
    public void createEmptyPublicacao()
    {
        Publicacao p = new Publicacao();

        assertNull(p.getData());
        assertNull(p.getHora());
        assertNull(p.getOrigem());
        assertNull(p.getDestino());
        assert p.getNumPassageiros() == 0;
        assertNull(p.getContrapartidas());
        assertNull(p.getIdUser());
    }

    @Test
    public void createPublicacao()
    {
        Publicacao p = new Publicacao("2019-12-19", "17:38", "Covilha", "Horta", 0, "3 euros", "1");

        assertEquals("2019-12-19", p.getData());
        assertEquals("17:38", p.getHora());
        assertEquals("Covilha", p.getOrigem());
        assertEquals("Horta", p.getDestino());
        assert p.getNumPassageiros() == 0;
        assertEquals("3 euros", p.getContrapartidas());
        assertEquals("1", p.getIdUser());
    }

    @Test
    public void publicacaoMapIsCorrect()
    {
        Publicacao p = new Publicacao("2019-12-19", "17:38", "Covilha", "Horta", 0, "3 euros", "1");
        Map<String, Object> result = p.toMap();

        Map<String, Object> expected = new HashMap<>();
        expected.put("data", "2019-12-19");
        expected.put("hora", "17:38");
        expected.put("origem", "Covilha");
        expected.put("destino", "Horta");
        expected.put("numPassageiros", 0);
        expected.put("contrapartidas", "3 euros");
        expected.put("idUser", "1");

        assert expected.equals(result);
    }
}