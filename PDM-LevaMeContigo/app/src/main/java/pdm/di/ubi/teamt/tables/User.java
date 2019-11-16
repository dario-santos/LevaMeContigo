package pdm.di.ubi.teamt.tables;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User
{
    private String nome;
    private int numViagens;
    private float nota;
    private String dataInscricao;

    public User(String nome, int numViagens, float nota, String dataInscricao)
    {
        this.nome = nome;
        this.numViagens = numViagens;
        this.nota = nota;
        this.dataInscricao = dataInscricao;
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
