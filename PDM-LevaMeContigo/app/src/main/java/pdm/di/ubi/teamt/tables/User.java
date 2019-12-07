package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User
{
    private String nome;
    private String dataInscricao;
    private String userAvatar;

    public User() {}

    public User(String nome, String dataInscricao, String userAvatar)
    {
        this.nome = nome;
        this.dataInscricao = dataInscricao;
        this.userAvatar = userAvatar;
    }

    public String getNome()
    {
        return nome;
    }

    public String getDataInscricao()
    {
        return dataInscricao;
    }

    public String getUserAvatar()
    {
        return userAvatar;
    }


    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("dataInscricao", dataInscricao);
        result.put("userAvatar", userAvatar);

        return result;
    }
}
