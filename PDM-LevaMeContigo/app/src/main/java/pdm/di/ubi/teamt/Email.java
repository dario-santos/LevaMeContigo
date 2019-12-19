package pdm.di.ubi.teamt;

import java.util.regex.Pattern;

public class Email 
{
    private static final String domain = "@ubi.pt";
    private static final String validAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                                + "0123456789"
                                                + "abcdefghijklmnopqrstuvxyz"
                                                + "._-";
    
    private Email(){}
    
    /**
     * Checks if a email is valid
     * 
     * @param email the email to validate
     * @return If the email is valid returns true, false otherwise. 
     */
    public static boolean IsValid(String email)
    {
        if(email == null)
            return false;
        if(!email.contains(domain))
            return false;

        String e = email.replaceFirst(Pattern.quote(domain), "");

        if(e.isEmpty())
            return false;

        for(int i = 0 ; i < e.length() ; i++)
            if(validAlphabet.indexOf(e.charAt(i)) == -1)
                return false;

        return true;
    }
}