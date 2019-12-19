package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User
{
    private String nome;
    private String dataInscricao;

    public User() {}

    public User(String nome, String dataInscricao)
    {
        this.nome = nome;
        this.dataInscricao = dataInscricao;
    }

    public String getNome()
    {
        return nome;
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
        result.put("dataInscricao", dataInscricao);

        return result;
    }
}
