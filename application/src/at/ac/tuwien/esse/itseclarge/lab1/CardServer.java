package at.ac.tuwien.esse.itseclarge.lab1;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.util.Series;

/**
 * Mit -Djavax.net.debug=all ausführen um den SSL-Traffic live zu sehen.
 * 
 * @author stephan
 */
public class CardServer {

	private static final String KEYSTORE_PATH = "keystore/kartenverwaltung-store.jks";
	private static final String KEYSTORE_PASSWORD = "itsec1";
	private static final String KEY_PASSWORD = "itsec2";

	public static void main(String[] args) throws Exception {
		Component comp = new Component();
		Server server = comp.getServers().add(Protocol.HTTPS, 8182);

		Series<Parameter> p = server.getContext().getParameters();

		p.add("sslContextFactory", "org.restlet.ext.ssl.PkixSslContextFactory");
		p.add("keystorePath", KEYSTORE_PATH);
		p.add("keystorePassword", KEYSTORE_PASSWORD);
		p.add("keyPassword", KEY_PASSWORD);

		p.add("truststorePath", KEYSTORE_PATH);
		p.add("truststorePassword", KEYSTORE_PASSWORD);

		p.add("needClientAuthentication", "true");

		server.getContext().setParameters(p);

		JaxRsApplication application = new JaxRsApplication(server.getContext());
		application.add(new CardApplication());
		
		CertificateAuthenticator guard = new CertificateAuthenticator(server.getContext());
		guard.setNext(application);
		
		comp.getDefaultHost().attach(guard);
		comp.start();
		System.out.println("Server started on port " + server.getPort());
		System.out.println("Press key to stop server");
		System.in.read();
		System.out.println("Stopping server");
		comp.stop();
		System.out.println("Server stopped");
	}
}
