CREATE TABLE student (id:integer, gpa:decimal, name:string, family:string,isMale:Boolean)
INSERT INTO student(id,gpa,name,isMale,family) VALUES (50,19.0,"Ali", true,"Hasani")
INSERT INTO student(id,gpa,name,family,isMale) VALUES (60,19.5,"Hossein", "Golestani", true)
INSERT INTO student(gpa,name,isMale) VALUES (20.0,"Sahar", false)
SELECT * FROM student Where family="GOLESTANI"
CREATE TABLE team (name: string, point:integer)
CREATE TABLE account (username: string, password: string)
SELECT * FROM student WHERE family = null
INSERT INTO team (name) VALUES ("Barcelona")
