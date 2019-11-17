package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Avaliar
{
    String idUser;
    String userName;
    String idPub;
    String comentario;
    float nota;

    public Avaliar() {}

    public Avaliar(String idUser, String userName, String idPub, String comentario, float nota)
    {
        this.idUser = idUser;
        this.userName = userName;
        this.idPub = idPub;
        this.comentario = comentario;
        this.nota = nota;
    }


    public String getIdUser()
    {
        return idUser;
    }


    public String getUserName()
    {
        return userName;
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
        result.put("idUser", this.idUser);
        result.put("userName", this.userName);
        result.put("idPub", this.idPub);
        result.put("comentario", this.comentario);
        result.put("nota", this.nota);

        return result;
    }
}