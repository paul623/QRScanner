package com.paul.qrscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.net.ConnectException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Decoder.BASE64Decoder;


/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/25 12：30
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class RSAutill {

    /*** 私钥 */
    private static RSAPrivateKey privateKey;
    /*** 公钥 */
    private static RSAPublicKey publicKey;
    private Context context;
    public RSAutill(Context context)
    {
        genKeyPair(context);

    }

    /*** 随机生成密钥对 */

    public static void genKeyPair(Context context)
    {
        KeyPairGenerator keyPairGen = null;
        try
        {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        SharedPreferences sharedPreferences=context.getSharedPreferences(MySupport.LOCALKEYS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(MySupport.LOCALPRIVATEKEY,privateKey.toString());
        editor.putString(MySupport.LOCALPUBLICKEY,publicKey.toString());
        editor.apply();
    }
    /*** RSA加密过程
     * ** @param publicKey 公钥
     * * @param plainTextData 明文数据
     * * @return
     * * @throws Exception 加密过程中的异常信息 */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception
    {
        if (publicKey == null)
        {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher;
        try
        {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此加密算法");
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e)
        {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e)
        {
            throw new Exception("明文长度非法");
        } catch
        (BadPaddingException e)
        {
            throw new Exception("明文数据已损坏");
        }
    }


    /*** RSA解密 **
     * @param privateKey 私钥
     * * @param cipherData 密文数据 *
     * @return 明文 *
     * @throws Exception 解密过程中的异常信息 */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception
    {
        if (privateKey == null)
        {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此解密算法");
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (InvalidKeyException e)
        {
            throw new Exception("解密私钥非法,请检查");
        }
        catch (IllegalBlockSizeException e)
        {
            throw new Exception("密文长度非法");
        }
        catch (BadPaddingException e)
        {
            throw new Exception("密文数据已损坏");}
    }

   public static final String RSAEncrypt(String data) throws Exception {
       byte [] bytes=encrypt(publicKey,data.getBytes());
       return Base64.encodeToString(bytes,Base64.NO_WRAP);
   }
    public static final String RSADecode(String data) throws Exception {
        byte []bytes=Base64.decode(data,Base64.NO_WRAP);
        return decrypt(privateKey,bytes).toString();
    }
    public static final String RSADecode(String data,RSAPrivateKey rsaPrivateKey) throws Exception {
        byte []bytes=Base64.decode(data,Base64.NO_WRAP);
        return decrypt(rsaPrivateKey,bytes).toString();
    }

}
