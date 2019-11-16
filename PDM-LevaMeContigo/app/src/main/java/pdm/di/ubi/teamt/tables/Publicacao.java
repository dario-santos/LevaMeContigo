package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Publicacao
{
    private String dia;
    private String hora;
    private String origem;
    private String destino;
    private int numPassageiros;
    private String contrapartidas;

    public Publicacao(String dia, String hora, String origem, String destino, int numPassageiros, String contrapartidas)
    {
        this.dia = dia;
        this.hora = hora;
        this.origem = origem;
        this.destino = destino;
        this.numPassageiros = numPassageiros;
        this.contrapartidas = contrapartidas;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("dia", this.dia);
        result.put("hora", this.hora);
        result.put("origem", this.origem);
        result.put("destino", this.destino);
        result.put("numPassageiros", this.numPassageiros);
        result.put("contrapartidas", this.contrapartidas);

        return result;
    }
}
