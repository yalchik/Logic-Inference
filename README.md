Logical Inference Model
===============
* The program takes a knowledge base with defined facts and rules as an input, accepts a question and gives the answer based on the given knowledge base.
* Facts are represented by predicate formulas with specified arguments. Example of a fact: P(a,b).
* Rules are represented by [horn clauses](https://en.wikipedia.org/wiki/Horn_clause). Example of a rule: P(x,y) <- P(x,z); P(z,y).
* Question is represented by a predicate with missing, or partially missing, or existing values (in the latter case it checks the truth of the given fact). Examples: P(?,?), P(a,?), P(a,b).
* The program gives the predicate list that are true in the given knowledge base.


# Модель обратного логического вывода.

* На вход подаётся база знаний, в которой есть факты и правила.
* Факты представлены предикатными формулами с указанными значениями аргументов; факты утверждают истинность заданной интерпретации предиката. Значением аргументов являются константы. Например: P(a,b).
* Правила представляются в виде [хорновских дизъюнктов](https://ru.wikipedia.org/wiki/%D0%A5%D0%BE%D1%80%D0%BD%D0%BE%D0%B2%D1%81%D0%BA%D0%B8%D0%B9_%D0%B4%D0%B8%D0%B7%D1%8A%D1%8E%D0%BD%D0%BA%D1%82). Например: P(x,y) <- P(x,z); P(z,y).
* В качестве параметра в программe указывается цель в виде предикатной формулы с отсутствующими, частично отсутствующими, либо указанными значениями (в последнем случае проверяет истинность данного набора). Например: P(?,?), P(a,?), P(a,b).
* На выходе получаем список предикатов, истинных в данной базе знайний.

### Example knowledge base:
```
Q(b,a,c)
Q(d,b,a)
Q(c,a,b)
P(a)
P(d)
L(b,d)
K(b,d)
L(y,x) <- K(x,y)
M(x,y) <- L(y,x); P(x); Q(x,y,z)
```
Question: `M(?,?)`  
Answer: `[M(d,b)]`

Question: `L(?,?)`  
Answer: `[L(b,d), L(d,b)]`

Question: `Q(?,?,?)`  
Answer: `[Q(b,a,c), Q(d,b,a), Q(c,a,b)]`

Question: `P(?,?)`  
Answer: `[P(a), P(d)]`

### Usage:
Format:`java -jar [jar_path] [kb_path] [question]`  
Example: `java -jar target/LogicInference-1.0-SNAPSHOT.jar examples/test_knowledge_base1.txt M(?,?)`

### Demo with more examples:
```bat
cd %PROJECT_DIR%/examples
run.bat
```
