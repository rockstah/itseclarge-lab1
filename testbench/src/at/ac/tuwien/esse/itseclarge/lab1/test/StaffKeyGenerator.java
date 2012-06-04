package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class StaffKeyGenerator {

	/**
	 * Generiert ein Schlüsselpaar.
	 * 
	 * @param args
	 * @throws Exception wenn was schief geht :) 
	 */
	public static void main(String[] args) throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		
		BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream("keystore/karten/staffkey.pub"));
		f.write(pub.getEncoded());
		f.close();
		
		System.out.println(pub.getFormat() + " used for public key");
		
		f = new BufferedOutputStream(new FileOutputStream("keystore/kunden/staffkey.priv"));
		f.write(priv.getEncoded());
		f.close();
		
		System.out.println(priv.getFormat() + " used for private key");
	}

}
