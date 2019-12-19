package pdm.di.ubi.teamt;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PasswordTest
{
    @Test
    public void passwordsize_isCorrect()
    {
        try
        {
            String pass = Password.Generate(5);
            assertEquals(5, pass.length());
        }
        catch (Exception e)
        {
            assert false;
        }
    }

    @Test
    public void passwordSizeZero()
    {
        try
        {
            Password.Generate(0);
        }
        catch (Exception e)
        {
            assert true;
        }
    }
}