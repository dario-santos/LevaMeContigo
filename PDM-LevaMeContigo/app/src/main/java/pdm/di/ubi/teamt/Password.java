package team.t.levamecontigo;

public class Password
{
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                            + "0123456789"
                                            + "abcdefghijklmnopqrstuvxyz"
                                            + "!\"#$%&/()=?";

   
    private Password(){}
    
    /**
     * Generates a random password
     * 
     * @param passwordSize Size of the password
     * @return The random password
     */
    public static String Generate(int passwordSize)
    {
        if(passwordSize <= 0)
            throw new IllegalArgumentException("The parameter passwordSize needs to be greater than 0"); 
        
        StringBuilder sb = new StringBuilder();

        for(int i = 0 ; i < passwordSize ; i++)
        {
            int index = (int)(alphabet.length() * Math.random());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    } 
}