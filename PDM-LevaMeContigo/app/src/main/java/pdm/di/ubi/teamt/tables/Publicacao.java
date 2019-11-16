package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Publicacao
{
    private String data;
    private String hora;
    private String origem;
    private String destino;
    private int numPassageiros;
    private String contrapartidas;
    private String idUser;

    public Publicacao() {}

    public Publicacao(String data, String hora, String origem, String destino, int numPassageiros, String contrapartidas, String idUser)
    {
        this.data = data;
        this.hora = hora;
        this.origem = origem;
        this.destino = destino;
        this.numPassageiros = numPassageiros;
        this.contrapartidas = contrapartidas;
        this.idUser = idUser;
    }

    public String getData()
    {
        return data;
    }

    public String getHora()
    {
        return hora;
    }

    public String getOrigem()
    {
        return origem;
    }

    public String getDestino()
    {
        return destino;
    }

    public int getNumPassageiros()
    {
        return numPassageiros;
    }

    public String getContrapartidas()
    {
        return contrapartidas;
    }

    public String getIdUser()
    {
        return idUser;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", this.data);
        result.put("hora", this.hora);
        result.put("origem", this.origem);
        result.put("destino", this.destino);
        result.put("numPassageiros", this.numPassageiros);
        result.put("contrapartidas", this.contrapartidas);
        result.put("idUser", this.idUser);

        return result;
    }
}
