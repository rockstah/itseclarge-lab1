IT Security in Large Infrastructures (Lab 1)
============================================

Architektur
-----------
Das Projekt ist als JAX-RS RESTful Webservice realisiert. Die Kommunikation erfolgt über HTTP und ist über 2-Way-SSL Authentifizierung geschützt. Folgende Technologien wurden verwendet:

* Jetty 7.1
* Servlet 2.5
* RESTlet 2.0
* SQLite 0.56


Howto
-----
* Repository clonen
* ant run_app ausführen


Hinweise
--------
* Zum Testen im Browser muss zumindest eines der Client-Zertifikate installiert werden
* Die Client-Zertifikate haben _kein_ Passwort, auch wenn beim Import danach gefragt wird
* Zum Testen eignet sich [RESTClient](https://addons.mozilla.org/de/firefox/addon/restclient/) als Extension für Firefox
* Passwort für den Keystore ist _itsec1_
