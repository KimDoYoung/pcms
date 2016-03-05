-- junit 단위 테스트용
-- person은 5개의 row를 가진다
DROP TABLE person;
CREATE TABLE person (
       ID       		CHAR(2)       NOT NULL,
       NAME        		VARCHAR2(20)  NOT NULL,
       HEIGHT           NUMBER(3),
       SCORE			NUMBER(5,2),
	   UPD_DT           DATE,
      CONSTRAINT PLAYER_PK PRIMARY KEY (ID)
);
INSERT INTO person VALUES('01','Hong',160, 80.5,sysdate);
INSERT INTO person VALUES('02','Kim',170, 50.5,sysdate);
INSERT INTO person VALUES('03','Jeong',180, 60.4,sysdate);
INSERT INTO person VALUES('04','Park',156, 73.2,sysdate);
INSERT INTO person VALUES('05','Won',125, 93.5,sysdate);
commit;