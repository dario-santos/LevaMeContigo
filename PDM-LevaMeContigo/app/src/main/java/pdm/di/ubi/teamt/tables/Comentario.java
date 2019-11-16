package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Comentario
{
    String idUser;
    String idPub;
    String comentario;

    public void Comentario() {}

    public void Comentario(String idUser, String idPub, String comentario)
    {
        this.idUser = idUser;
        this.idPub = idPub;
        this.comentario = comentario;
    }

    public String getIdPub()
    {
        return idPub;
    }

    public String getIdUser()
    {
        return idUser;
    }

    public String getComentario()
    {
        return comentario;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idUser", this.idUser);
        result.put("idPub", this.idPub);
        result.put("comentario", this.comentario);

        return result;
    }
}
