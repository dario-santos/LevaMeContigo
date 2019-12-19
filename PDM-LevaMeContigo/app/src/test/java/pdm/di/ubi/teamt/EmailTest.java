package pdm.di.ubi.teamt;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EmailTest
{
    @Test
    public void emailIsCorrect()
    {
        boolean t = Email.IsValid("dario.santos@ubi.pt");
        assert t;
    }

    @Test
    public void emailInvalidDomain()
    {
        boolean t = Email.IsValid("dario.santos@di.ubi.pt");
        assert !t;
    }

    @Test
    public void emailInvalidChar()
    {
        boolean t = Email.IsValid("dário.santos@ubi.pt");
        assert !t;
    }


    @Test
    public void emailEmpty()
    {
        boolean t = Email.IsValid("");
        assert !t;
    }

    @Test
    public void emailNoDomain()
    {
        boolean t = Email.IsValid("dario.santos");
        assert !t;
    }

    @Test
    public void emailNoUser()
    {
        boolean t = Email.IsValid("@ubi.pt");
        assert !t;
    }
}