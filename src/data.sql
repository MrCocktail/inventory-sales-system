-- Drop Database DB_Stock2025;
Create database DB_Stock2025;

Use DB_Stock2025;


Create Table Employe(
  idEmploye  int  NOT NULL AUTO_INCREMENT,
  Prenom varchar(20) NOT NULL Default '',
  Nom varchar(20) NOT NULL Default '',
  Poste varchar(20) NOT NULL Default '',
  Adresse varchar(20) NOT NULL Default '',
  Phone varchar(20) NOT NULL Default '',
  Salaire  decimal(8,2)  NOT NULL,
Constraint PK_Employe1 Primary Key(idEmploye)
);


Create Table Client(
  idClient  int  NOT NULL AUTO_INCREMENT,
  Prenom varchar(20) NOT NULL Default '',
  Nom varchar(20) NOT NULL Default '',
  Adresse varchar(20) NOT NULL Default '',
  Phone varchar(20) NOT NULL ,
Constraint PK_Client1 Primary Key(idClient)
);

Create Table Pieces(
  idPiece  int  NOT NULL AUTO_INCREMENT,
  Nom   varchar(30) NOT NULL Default '',
  Provenance varchar(25) NOT NULL Default '',
  Prix_achat  decimal(8,2)  NOT NULL,
  Prix_vente  decimal(8,2)  NOT NULL,
  date_arrivage  date ,
  Quantite  int,
Constraint PK_Piece1 Primary Key(idPiece)
);

Create Table Vente(
  numero  int  NOT NULL AUTO_INCREMENT,
  idEmploye int NOT NULL ,
  idClient int  NOT NULL ,
  montant decimal(8,2)  NOT NULL,
  DateVente  date ,
Constraint PK_Vente1 Primary Key(numero)
);

Create Table ligne_de_vente(
  id int  NOT NULL AUTO_INCREMENT,
  numero int  NOT NULL ,
  idEmploye int NOT NULL ,
  idPiece  int NOT NULL ,
  Quantite int,
  montant decimal(8,2)  NOT NULL,
  Constraint PK_ligneVente1 Primary Key(id)
);


ALTER TABLE Vente  ADD  CONSTRAINT FK_Vente1 FOREIGN KEY(idEmploye) REFERENCES Employe(idEmploye);
ALTER TABLE Vente  ADD  CONSTRAINT FK_Vente2 FOREIGN KEY(idClient) REFERENCES Client(idClient);
ALTER TABLE ligne_de_vente  ADD  CONSTRAINT FK_ligneVente1 FOREIGN KEY(numero) REFERENCES Vente(numero);
ALTER TABLE ligne_de_vente  ADD  CONSTRAINT FK_ligneVente2 FOREIGN KEY(idEmploye) REFERENCES Employe(idEmploye);
ALTER TABLE ligne_de_vente  ADD  CONSTRAINT FK_ligneVente3 FOREIGN KEY(idPiece) REFERENCES Pieces(idPiece);




INSERT INTO Employe (Prenom, Nom, Poste, Adresse, Phone, Salaire) 
VALUES 
('Roger', 'Paul', 'Ingenieur', '12 Rue O', '2312-3453', 18965),
('Betty', 'Andre', 'Gestionnaire', '5 Rue Pavee', '3782-5653', 21965),
('Sabine', 'Jean', 'Secretaire', '121 Gd Rue', '3876-3232', 12834);

INSERT INTO Client (Prenom, Nom, Adresse, Phone) 
VALUES 
('Gethro', 'Pierre', '28 Gd Rue', '3876-1234'),
('Ismaelle', 'Larochelle', '29 Rue Lambert', '2232-6234'),
('Nathanael', 'Paul', '217 Rue du Centre', '3176-1234');

INSERT INTO Pieces (Nom, Provenance, Prix_achat, Prix_vente, date_arrivage, Quantite) 
VALUES 
('Alternateur', 'Japon', 13876.56, 18834.50, '2020-10-2', 20),
('Radiateur', 'Japon', 18376.56, 21834.50, '2020-11-2', 30),
('Boite de Bougie', 'Japon', 1000, 1250, '2020-2-21', 40);

INSERT INTO Vente (idEmploye, idClient, montant, DateVente) 
VALUES 
(1, 3, 31000, '2021-2-2'),
(2, 1, 21000, '2021-2-2'),
(1, 2, 10000, '2021-2-2');


insert into ligne_de_vente (numero,idEmploye, idPiece, Quantite,montant) values(1,3,1,1,20500 );
insert into ligne_de_vente (numero,idEmploye, idPiece, Quantite,montant) values(1,3,2,1,10500 );
insert into ligne_de_vente (numero,idEmploye, idPiece, Quantite,montant) values(2,1,2,1,21500 );
insert into ligne_de_vente (numero,idEmploye, idPiece, Quantite,montant) values(3,2,1,1,10000 );



