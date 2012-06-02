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

	private static final String KEYSTORE_PATH = "keystore/karten/keystore.jks";
	private static final String TRUSTSTORE_PATH = "keystore/truststore.jks";
	private static final String STORE_PASSWORD = "itsec1";

	public static void main(String[] args) throws Exception {
		Component comp = new Component();
		Server server = comp.getServers().add(Protocol.HTTPS, 8182);

		Series<Parameter> p = server.getContext().getParameters();

		// im Keystore wird das Server-Zertifikat aufbewart
		p.add("keystorePath", KEYSTORE_PATH);
		p.add("keystorePassword", STORE_PASSWORD);
		p.add("keyPassword", "");

		// der Truststore enthält die Client-Zertifikate, denen vertraut wird
		p.add("truststorePath", TRUSTSTORE_PATH);
		p.add("truststorePassword", STORE_PASSWORD);

		// aktiviert Mutual Authentication / 2-Way-SSL / Client Authentication
		p.add("needClientAuthentication", "true");

		JaxRsApplication application = new JaxRsApplication(server.getContext());
		application.add(new CardApplication());
		
		// Der CertificateAuthenticator überprüft bei jedem Request ob für die
		// gewählte Methode (GET/POST/DELETE/...) ein spezielles Zertifikat notwendig ist
		// und authorisiert nur korrekte Anfragen.
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
