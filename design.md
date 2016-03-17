#전반적인 설계
MBoard 클래스에서 모두 처리하는 것으로 한다.
MBoard가 여러개의 클래스별로 분배해 주는 역활을 한다.
**Board**는 컨텐츠 묶음을 의미하며 ,  **Item**이란 컨텐츠를 의미한다 

##Board 관련 메소드들
1. createBoard
2. modifyBoard
3. deleteBoard

## 각  Board를 다루는 메소드들

각 클래스들은 Board ID를 받아서 주어진 기능들을 소화해 낸다
   
1. listItem
2. createItem
3. modifyItem
4. deleteItem

#metadata
- datasourceId
- datasourceType
- db : 저장공간 데이터베이스의 종류
- boardtype : 게시판의 유형
	- 옵션
- id : 게시판의 id -> 데이터베이스의 테이블명을 이것으로 한다
- title : 게시판의 종류
- desc : 짧은 설명
- fields : 관리할 데이터 항목들 
	- id , title, datatype, size, 필수여부

#MetaData를 저장

#JSP의 생성 및 저장
