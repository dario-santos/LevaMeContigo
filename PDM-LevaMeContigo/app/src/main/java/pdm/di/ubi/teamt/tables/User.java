package pdm.di.ubi.teamt.tables;

import android.content.Intent;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User
{
    private String nome;
    private int numViagens;
    private float nota;
    private String dataInscricao;

    public User() {}

    public User(String nome, int numViagens, float nota, String dataInscricao)
    {
        this.nome = nome;
        this.numViagens = Integer.valueOf(numViagens);
        this.nota = Float.valueOf(nota);
        this.dataInscricao = dataInscricao;
    }

    public String getNome()
    {
        return nome;
    }

    public int getNumViagens()
    {
        return numViagens;
    }

    public float getNota()
    {
        return nota;
    }

    public String getDataInscricao()
    {
        return dataInscricao;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("numViagens", numViagens);
        result.put("nota", nota);
        result.put("dataInscricao", dataInscricao);

        return result;
    }
}
