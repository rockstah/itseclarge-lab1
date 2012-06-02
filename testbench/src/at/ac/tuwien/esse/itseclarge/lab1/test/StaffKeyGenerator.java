package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class StaffKeyGenerator {

	/**
	 * Generiert ein Schlüsselpaar.
	 * 
	 * @param args
	 * @throws Exception wenn was schief geht :) 
	 */
	public static void main(String[] args) throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		PublicKey pub = pair.getPublic();
		
		FileOutputStream f = new FileOutputStream("keystore/karten/staffkey.pub");
		f.write(pub.getEncoded());
		f.close();
		
		f = new FileOutputStream("keystore/kunden/staffkey.priv");
		f.write(priv.getEncoded());
		f.close();
	}

}
