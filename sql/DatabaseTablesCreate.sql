CREATE DATABASE Java4Project

use Java4Project
go

CREATE TABLE "Games" (
	GameID int NOT NULL IDENTITY PRIMARY KEY,
	Title varChar(80) NOT NULL UNIQUE,
	PlayersMin tinyint NOT NULL,
	PlayersMax tinyint NOT NULL,
	SetupTime smallint NOT NULL,
	PlayTime smallint NOT NULL,
	Themes varChar(100),
	Mechanics varchar(100),
	Comments varChar(300)
)


/*USE master;
CREATE LOGIN [ProjectUser] WITH PASSWORD = 'P@55word';
USE Java4Project;
CREATE USER [ProjectUser] FOR LOGIN [ProjectUser];*/