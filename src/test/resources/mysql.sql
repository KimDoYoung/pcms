use pcms;
CREATE TABLE person (
       ID       		CHAR(2)       NOT NULL,
       NAME        		VARCHAR(20)  NOT NULL,
       HEIGHT           int,
       SCORE			double,
	   UPD_DT           DATE,
      CONSTRAINT PLAYER_PK PRIMARY KEY (ID)
);
INSERT INTO person VALUES('01','Hong',160, 80.5,now());
INSERT INTO person VALUES('02','Kim',170, 50.5,now());
INSERT INTO person VALUES('03','Jeong',180, 60.4,now());
INSERT INTO person VALUES('04','Park',156, 73.2,now());
INSERT INTO person VALUES('05','Won',125, 93.5,now());

select * from person;