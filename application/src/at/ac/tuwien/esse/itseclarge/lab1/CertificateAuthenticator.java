package at.ac.tuwien.esse.itseclarge.lab1;

import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

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

	/**
	 * Verwaltet eine Liste von Zertifikaten, die erweiterte Operationen
	 * durchführen dürfen. Eine erweiterte Operation ist alles, was kein
	 * GET-Request ist.
	 */
	private List<Certificate> privilegedCertificates;

	/**
	 * Constructor.
	 * Initialisiert auch die Liste der priviligierten Zertifikate.
	 * 
	 * @param context Server-Kontext
	 */
	public CertificateAuthenticator(Context context) {
		super(context);

		privilegedCertificates = new ArrayList<Certificate>();
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			privilegedCertificates.add(cf.generateCertificate(new FileInputStream(
					"keystore/kundenverwaltung-public.cer")));
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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
			allowed = privilegedCertificates.containsAll(certificates);
		}

		// 401 Unauthorized setzen wenn nicht erlaubt
		if (!allowed) {
			response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			response.setEntity("{\"error\":\"This method is not for you.\"}", MediaType.APPLICATION_JSON);
		}

		return allowed;
	}

}
