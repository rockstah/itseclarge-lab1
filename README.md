IT Security in Large Infrastructures (Lab 1)
============================================

Der komplette Code dieses Projektes ist auf github gehostet:
https://github.com/codecurry/itseclarge-lab1

Architektur
-----------
Das Projekt ist als JAX-RS RESTful Webservice realisiert. Die Kommunikation erfolgt über HTTP und ist über 2-Way-SSL Authentifizierung geschützt. Folgende Technologien wurden verwendet:

* Jetty 7.1
* Servlet 2.5
* RESTlet 2.0
* SQLite 0.56

Howto
-----
* ant run_app -- Server starten
* ant run_test -- Unit-Tests starten
* ant clean -- Build und Datenbank entfernen

Hinweise
--------
* die Client-Zertifikate haben _kein_ Passwort, auch wenn beim Import danach gefragt wird
* Passwort für den Keystore ist _itsec1_
* um den SSL/TLS-Verkehr zu beobachten im build.xml die jvmarg-Tags aktivieren
