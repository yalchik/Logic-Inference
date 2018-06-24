@echo off
set APP_JAR=..\target\LogicInference-1.0-SNAPSHOT.jar
set TEST_KB1=test_knowledge_base1.txt
set TEST_KB2=test_knowledge_base2.txt
set TEST_KB3=test_knowledge_base3.txt

echo Expect: [M(d,b)]
java -jar %APP_JAR% %TEST_KB1% M(?,?)

echo Expect: [L(b,d), L(d,b)]
java -jar %APP_JAR% %TEST_KB1% L(?,?)

echo Expect: [Q(b,a,c), Q(d,b,a), Q(c,a,b)]
java -jar %APP_JAR% %TEST_KB1% Q(?,?,?)

echo Expect: [P(a), P(d)]
java -jar %APP_JAR% %TEST_KB1% P(?,?)

echo Expect: [M(u,w), M(p,w), M(d,b), M(a,l)]
java -jar %APP_JAR% %TEST_KB2% M(?,?)

echo Expect: [M(u,w), M(p,w)]
java -jar %APP_JAR% %TEST_KB2% M(?,w)

echo Expect: []
java -jar %APP_JAR% %TEST_KB2% M(w,?)

echo Expect: [M(a,l)]
java -jar %APP_JAR% %TEST_KB2% M(a,l)

echo Expect: [X(a,b,c), X(b,b,k), X(d,b,k), X(f,c,k), X(b,b,l), X(d,b,l)]
java -jar %APP_JAR% %TEST_KB3% X(?,?,?)

echo Expect: [X(a,b,c)]
java -jar %APP_JAR% %TEST_KB3% X(a,b,c)

echo Expect: [X(f,c,k)]
java -jar %APP_JAR% %TEST_KB3% X(f,c,?)

echo Expect: []
java -jar %APP_JAR% %TEST_KB3% X(x,y,z)
