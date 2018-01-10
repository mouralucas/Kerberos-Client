package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas Penha de Moura - 1208977
 */
public class Client {

    private String ID_C;
    private String ID_S;
    private String request;
    private String M1, M2, M3, M4, M5, M6;
    private String M2_Pt1_encrypt;
    private String M2_Pt1;
    private String M2_Pt2;
    private String M3_Pt1_open;
    private String M3_Pt1_encrypt;
    private String M4_Pt1_encrypt;
    private String M4_Pt1;
    private String M4_Pt2;
    
    private String M5_Pt1_open;
    private String M5_Pt1_encrypt;
    
    private final String tgsId = "TGS";
    private final String serverId = "Server";

    static String chaveEncript;
    static String mensagemDoServidorAS;
    static String T_C_TGS, K_C_TGS, K_C_S, T_C_S;
    static String K_C, T_R, ultimo;
    static String N1, N2;

    String splitMsg[];

    /*
    ---------------SETS E GETS NECESSÁRIOS--------------------
     */
    public String getID_C() {
        return ID_C;
    }

    public void setID_C(String ID_C) {
        this.ID_C = ID_C;
    }

    public String getID_S() {
        return ID_S;
    }

    public void setID_S(String ID_S) {
        this.ID_S = ID_S;
    }

    /*
    ---------------OUTROS MÉTODOS--------------------
     */
    public boolean clientVerification(String client) {

        if (client.toLowerCase().equals("lucas")) {
            setID_C(client);
            return true;
        } else {
            return false;
        }

    }

    /**
     *
     * @param messageToAuthenticationService
     * @return
     * @throws IOException
     */
    public String ASConnecion(String messageToAuthenticationService) throws IOException {

        Socket clientSocket = new Socket("localhost", 12345);
        DataOutputStream messageToAS = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader messageFromAS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        messageToAS.writeBytes(messageToAuthenticationService + '\n');

        M2 = messageFromAS.readLine();
        clientSocket.close();

        String M2_open = openMgs("M2", M2);
        System.out.println("M2 = " + M2_open);
        return M2_open;

    }

    public String TGSConnection(String messageToTicketGrantingService) throws IOException {

        Socket clientSocket = new Socket("localhost", 54321);
        DataOutputStream messageToTGS = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader messageFromTGS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        messageToTGS.writeBytes(messageToTicketGrantingService + '\n');

        M4 = messageFromTGS.readLine();

        clientSocket.close();

        String M4_open = openMgs("M4", M4);
        System.out.println("M4 = " + M4_open);
        return M4_open;

    }

    public String ServerConnection(String messageToServer) throws IOException {

        Socket clientSocket = new Socket("localhost", 34567);

        DataOutputStream messageToS = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader messageFromS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        messageToS.writeBytes(messageToServer + '\n');

        M6 = messageFromS.readLine();

        clientSocket.close();
        System.out.println("M6 crypt: " + M6);
        String M6_open = openMgs("M6", M6);
        System.out.println("M6 = " + M6_open);
        return M6_open;

    }

    public String messageGen(String msg) throws IOException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        T_R = new SimpleDateFormat("dd/MM/yyyy-HH:mm").format(timestamp.getTime());

        switch (msg) {
            case "M1":
                K_C = newKeyGen("KC" + getID_C());
                System.out.println("K_C em M1 " + K_C);

                N1 = Integer.toString((int) (Math.random() * 100));
                System.out.println("N1 = " + N1);

                M1 = ID_C + ";" + tgsId + ";" + T_R + ";" + N1;
                System.out.println("M1 = " + M1);
                return M1; //retorna para a janela, a janela chama o ASconnection com essa msg
            case "M3":
                N2 = Integer.toString((int) (Math.random() * 100));
                System.out.println("N2 = " + N2);
                
                M3_Pt1_open = ID_C + ";" + T_R;
                System.out.println("K_C_TGS em M3: " + K_C_TGS);
                M3_Pt1_encrypt = Encode.encode(M3_Pt1_open, K_C_TGS);

                M3 = M3_Pt1_encrypt + ";" + T_C_TGS + ";" + serverId + ";" + N2;
                System.out.println("M3 = " + M3);
                return M3;
            case "M5":
                request = ID_S;
                
                M5_Pt1_open = (ID_C + ";" + T_R);
                M5_Pt1_encrypt = Encode.encode(M5_Pt1_open, K_C_S);
                
                M5 = M5_Pt1_encrypt + ";" + T_C_S + ";" + request;
                System.out.println("M5 = " + M5);
                return M5;
            default:
                System.out.println("Esta não é uma mensagem válida");
                return "Esta não é uma mensagem válida (cria mensagem)";
        }
    }

    public String openMgs(String openMsg, String msg) {
        switch (openMsg) {
            case "M2":
                splitMsg = msg.split(";");
                M2_Pt1_encrypt = splitMsg[0];
                M2_Pt1 = Encode.decode(M2_Pt1_encrypt, K_C);
                M2_Pt2 = splitMsg[1];

                //set a chave de seção no CLIENTE
                String aux[] = M2_Pt1.split(";");
                K_C_TGS = aux[0];
                T_C_TGS = M2_Pt2;

                return M2_Pt1 + ";" + M2_Pt2;
            case "M4":
                splitMsg = msg.split(";");
                M4_Pt1_encrypt = splitMsg[0];
                M4_Pt1 = Encode.decode(M4_Pt1_encrypt, K_C_TGS);
                
                M4_Pt2 = splitMsg[1]; //T_C_S
                
                
                String aux2[] = M4_Pt1.split(";");
                K_C_S = aux2[0];          
                T_C_S = M4_Pt2;         //Cliente não tem acesso a esse ticket
                
                return M4_Pt1 + ";" + M4_Pt2;

            case "M6":
                M6 = Encode.decode(msg, K_C_S);
                return M6;
            default:
                System.out.println("Esta não é uma mensagem válida");
                return "Esta não é uma mensagem válida (abre mensagem)";
        }
    }

    public String newKeyGen(String passwd) {
        String retorno = null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(passwd.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder hash = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                hash.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //System.out.println("Hex format : " + sb.toString());
            retorno = hash.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retorno.substring(0, 16);
    }
}
