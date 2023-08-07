CREATE SCHEMA IF NOT EXISTS COMPUTER;
SET SCHEMA COMPUTER;
-- from http://www.sqlbooks.ru

CREATE TABLE Laptop (
	code int NOT NULL,
	model varchar (50) NOT NULL ,
	speed int NOT NULL ,
	ram int NOT NULL ,
	hd float NOT NULL ,
	price float NULL ,
	screen int NOT NULL,
	CONSTRAINT PK_Laptop PRIMARY KEY (code ASC)
) ;

CREATE TABLE PC (
	code int NOT NULL PRIMARY KEY,
	model varchar (50) NOT NULL ,
	speed int NOT NULL ,
	ram int NOT NULL ,
	hd float NOT NULL ,
	cd varchar (10) NOT NULL ,
	price float NULL
) ;

CREATE TABLE Product (
	maker varchar (10) NOT NULL,
	model varchar (50) NOT NULL PRIMARY KEY ,
	type varchar (50) NOT NULL 
) ;

CREATE TABLE Printer (
	code int NOT NULL PRIMARY KEY,
	model varchar (50) NOT NULL ,
	color char (1) NOT NULL ,
	type varchar (10) NOT NULL ,
	price float NULL 
) ;

ALTER TABLE Laptop ADD 
	CONSTRAINT FK_Laptop_product FOREIGN KEY 
	(
		model
	) REFERENCES Product (
		model
	);

ALTER TABLE PC ADD 
	CONSTRAINT FK_pc_product FOREIGN KEY 
	(
		model
	) REFERENCES Product (
		model
	);

ALTER TABLE Printer ADD
	CONSTRAINT FK_printer_product FOREIGN KEY 
	(
		model
	) REFERENCES Product (
		model
	);
----Product------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
insert into Product values('B','1121','PC');
insert into Product values('A','1232','PC');
insert into Product values('A','1233','PC');
insert into Product values('E','1260','PC');
insert into Product values('A','1276','Printer');
insert into Product values('D','1288','Printer');
insert into Product values('A','1298','Laptop');
insert into Product values('C','1321','Laptop');
insert into Product values('A','1401','Printer');
insert into Product values('A','1408','Printer');
insert into Product values('D','1433','Printer');
insert into Product values('E','1434','Printer');
insert into Product values('B','1750','Laptop');
insert into Product values('A','1752','Laptop');
insert into Product values('E','2113','PC');
insert into Product values('E','2112','PC');


                                                                                                                                                                                                                                                                 
----PC------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
insert into PC values(1,'1232',500,64,5,'12x',600);
insert into PC values(2,'1121',750,128,14,'40x',850);
insert into PC values(3,'1233',500,64,5,'12x',600);
insert into PC values(4,'1121',600,128,14,'40x',850);
insert into PC values(5,'1121',600,128,8,'40x',850);
insert into PC values(6,'1233',750,128,20,'50x',950);
insert into PC values(7,'1232',500,32,10,'12x',400);
insert into PC values(8,'1232',450,64,8,'24x',350);
insert into PC values(9,'1232',450,32,10,'24x',350);
insert into PC values(10,'1260',500,32,10,'12x',350);
insert into PC values(11,'1233',900,128,40,'40x',980);
insert into PC values(12,'1233',800,128,20,'50x',970);




                                                                                                                                                                                                                                                                 
----Laptop------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
insert into Laptop values(1,'1298',350,32,4,700,11);
insert into Laptop values(2,'1321',500,64,8,970,12);
insert into Laptop values(3,'1750',750,128,12,1200,14);
insert into Laptop values(4,'1298',600,64,10,1050,15);
insert into Laptop values(5,'1752',750,128,10,1150,14);
insert into Laptop values(6,'1298',450,64,10,950,12);



                                                                                                                                                                                                                                                                 
----Printer------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 
insert into Printer values(1,'1276','n','Laser',400);
insert into Printer values(2,'1433','y','Jet',270);
insert into Printer values(3,'1434','y','Jet',290);
insert into Printer values(4,'1401','n','Matrix',150);
insert into Printer values(5,'1408','n','Matrix',270);
insert into Printer values(6,'1288','n','Laser',400);

