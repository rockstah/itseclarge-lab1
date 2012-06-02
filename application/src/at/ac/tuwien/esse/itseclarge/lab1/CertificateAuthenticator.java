package at.ac.tuwien.esse.itseclarge.lab1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.http.HttpRequest;
import org.restlet.ext.jetty.internal.JettyCall;
import org.restlet.security.Authenticator;

public class CertificateAuthenticator extends Authenticator {
	
	private static final String PRIVILEDGED_CERTSTORE_PATH = "keystore/karten/certstore-privileged.jks";
	private static final char[] PRIVILEDGED_CERTSTORE_PASSWORD = "itsec1".toCharArray();

	/**
	 * Verwaltet eine Liste von Zertifikaten, die erweiterte Operationen
	 * durchführen dürfen. Eine erweiterte Operation ist alles, was kein
	 * GET-Request ist.
	 */
	private KeyStore priviledgedCerticiateStore;

	/**
	 * Constructor.
	 * Initialisiert auch die Liste der priviligierten Zertifikate.
	 * 
	 * @param context Server-Kontext
	 */
	public CertificateAuthenticator(Context context) {
		super(context);

		try {
			priviledgedCerticiateStore = KeyStore.getInstance("JKS");
			priviledgedCerticiateStore.load(new FileInputStream(PRIVILEDGED_CERTSTORE_PATH), PRIVILEDGED_CERTSTORE_PASSWORD);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean authenticate(Request request, Response response) {
		boolean allowed = false;

		// Zertifikate direkt aus dem HTTP-Request auslesen
		List<Certificate> certificates = ((JettyCall) ((HttpRequest) request).getHttpCall())
				.getSslClientCertificates();

		// Alles was kein GET-Request ist muss ein priviligiertes Zertifikat haben.
		if (!request.getMethod().equals(Method.GET)) {
			try {
				allowed = (priviledgedCerticiateStore.getCertificateAlias(certificates.get(0)) != null);
			} catch (KeyStoreException e) {
				allowed = false;
			}
		} else {
			allowed = true;
		}

		// 401 Unauthorized setzen wenn nicht erlaubt
		if (!allowed) {
			response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			response.setEntity("{\"error\":\"This method is not for you.\"}", MediaType.APPLICATION_JSON);
		}

		return allowed;
	}

}
