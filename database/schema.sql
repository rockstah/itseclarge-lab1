CREATE TABLE IF NOT EXISTS cards (
	cardno CHAR(16),
	validity CHAR(5),
	signature TEXT,
	climit DECIMAL(13,4),
	customer LONG,
	PRIMARY KEY (cardno, validity)
);
