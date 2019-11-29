package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Inscrito
{
    private String idUser;
    private String idPub;
    private boolean accepted;

    public Inscrito() {}

    public Inscrito(String idUser, String idPub, boolean accepted)
    {
        this.idUser = idUser;
        this.idPub = idPub;
        this.accepted = accepted;
    }

    public String getIdUser()
    {
        return idUser;
    }

    public String getIdPub()
    {
        return idPub;
    }

    public boolean getAccepted()
    {
        return accepted;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("idUser", this.idUser);
        result.put("idPub", this.idPub);
        result.put("accepted", this.accepted);

        return result;
    }

}
