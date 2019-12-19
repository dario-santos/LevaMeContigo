package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Avaliar
{
    String idUserAvaliador;
    String nomeAvaliador;
    String idUserAvaliado;
    String idPub;
    String comentario;
    float nota;

    public Avaliar() {}

    public Avaliar(String idUserAvaliador, String nomeAvaliador, String idUserAvaliado, String idPub, String comentario, float nota)
    {
        this.idUserAvaliador = idUserAvaliador;
        this.nomeAvaliador = nomeAvaliador;
        this.idUserAvaliado = idUserAvaliado;
        this.idPub= idPub;
        this.comentario = comentario;
        this.nota = nota;
    }


    public String getIdUserAvaliador()
    {
        return idUserAvaliador;
    }

    public String getNomeAvaliador()
    {
        return nomeAvaliador;
    }

    public String getIdUserAvaliado()
    {
        return idUserAvaliado;
    }

    public String getIdPub()
    {
        return idPub;
    }

    public String getComentario()
    {
        return comentario;
    }

    public float getNota()
    {
        return nota;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idUserAvaliador", this.idUserAvaliador);
        result.put("nomeAvaliador", this.nomeAvaliador);
        result.put("idUserAvaliado", this.idUserAvaliado);
        result.put("idPub", this.idPub);
        result.put("comentario", this.comentario);
        result.put("nota", this.nota);

        return result;
    }
}