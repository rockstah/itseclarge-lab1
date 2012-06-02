Zertifikat-Struktur
===================
Alle Verbindungen zwischen der Kartenverwaltung und anderen Modulen sind
über wechselseitige SSL Authentifizierung geschützt. Zu diesem Zweck existieren
in diesem Verzeichnis die entsprechenden Zertifikate, die im Realsystem
natürlich auf sicheren Kanälen auf die einzelnen Netzwerkkomponenten
aufgespielt werden müssten.

Für jedes Modul existiert ein eigener Keystore in dem der private Schlüssel
und das von der CA signierte Zertifikat gespeichert ist. Dieser Keystore
wird ausschließlich von jeweils einem Modul verwedet.

Der Truststore ist für alle Module identisch und enthält nur das Root-Zertifikat
der CA.

Die Kundenverwaltung besitzt außerdem noch die privaten Schlüssel der
einzelnen Mitarbeiter. In einem Realsystem könnten diese Schlüssel etwa
auf Smartcards abgelegt sein.

Die Kartenverwaltung besitzt einen weiteren Store in dem die Zertifikate
der Komponenten aufbewahrt werden, die erweiterte Rechte haben. Außerdem
sind hier die öffentlichen Schlüssel der Mitarbeiter hinterlegt, um die
digitalen Signaturen prüfen zu können.
