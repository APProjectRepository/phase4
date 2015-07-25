CREATE TABLE student(id:integer, gpa:decimal, name:string, family:string,isMale:logical)
CREATE TABLE student(id:integer, gpa:decimal, name:string, family:string, family:Boolean)
CREATE TABLE student(id:integer, gpa:decimal, name:string, family:string,isMale:Boolean)
CREATE TABLE student(id:integer, gpa:decimal, name:string, family:string,isMale:Boolean)
DROP TABLE st
CREATE TABLE st(id:integer)
DROP TABLE st
INSERT INTO st (id) VALUES (50)
INSERT INTO student(studentid) VALUES (50)
INSERT INTO student(id,gpa,name,family,isMale) VALUES (50,null,"Ali", Hasani, true)
INSERT INTO student(id,gpa,name,family,isMale) VALUES (50,"Ali", "Hasani", true)
INSERT INTO student(id,gpa,name,family,isMale) VALUES (50,null,"Ali", "Hasani", true)
INSERT INTO student(id,name,family,isMale) VALUES (10,null,"Maryam", "Hasani", false)
DELETE FROM st WHERE id=10
DELETE FROM student WHERE studentid=50
DELETE FROM student WHERE isMale="true"
DELETE FROM student WHERE id=90
DELETE FROM student WHERE isMale=true
UPDATE st SET id=90 WHERE id=10
UPDATE student SET isMale=true WHERE studentid=50
UPDATE student SET name="Reza" WHERE isMale="true"
UPDATE student SET name="Taghi" WHERE id=90
UPDATE student SET name="Reza", isMale=true WHERE isMale=false
