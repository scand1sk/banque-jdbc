drop table if exists Titulaire, MoyenPaiement, Operation, Compte, Client;

CREATE TABLE Client
(
  idclient serial primary key,
  nom text NOT NULL
);

CREATE TABLE Compte (
   noCompte serial primary key,
   solde numeric(20,2) NOT NULL,
   decouvertAutorise numeric(20,2) NOT NULL,
   intitule text NOT NULL,
   CHECK (solde >= decouvertautorise)
);

CREATE TABLE Operation (
  idOperation serial primary key,
  dateHeure timestamp NOT NULL DEFAULT now(),
  montant numeric(20,2) NOT NULL,
  libelle text NOT NULL,
  idMP int not null references MoyenPaiement
);

CREATE TABLE MoyenPaiement (
  idMP serial primary key,
  idClient int not null references Client,
  noCompte int not null references Compte,
  type text not null not null,
  expiration date
);

CREATE TABLE Titulaire (
  idClient int references Client,
  noCompte int references Compte,
  primary key (idClient, noCompte));

INSERT INTO Client VALUES (-1, 'Jean-Mi'), (-2, 'Gérard');