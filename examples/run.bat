@echo off
echo M(?,?) from input.txt
java -jar ..\dist\LogicInference.jar input.txt M(?,?)

echo L(?,?) from input.txt
java -jar ..\dist\LogicInference.jar input.txt L(?,?)

echo Q(?,?,?) from input.txt
java -jar ..\dist\LogicInference.jar input.txt Q(?,?,?)

echo P(?,?) from input.txt
java -jar ..\dist\LogicInference.jar input.txt P(?,?)

echo M(?,?) from in1.txt
java -jar ..\dist\LogicInference.jar in1.txt M(?,?)

echo M(?,w) from in1.txt
java -jar ..\dist\LogicInference.jar in1.txt M(?,w)

echo M(w,?) from in1.txt
java -jar ..\dist\LogicInference.jar in1.txt M(w,?)

echo M(a,l) from in1.txt
java -jar ..\dist\LogicInference.jar in1.txt M(a,l)

echo X(?,?,?) from in2.txt
java -jar ..\dist\LogicInference.jar in2.txt X(?,?,?)

echo X(a,b,c) from in2.txt
java -jar ..\dist\LogicInference.jar in2.txt X(a,b,c)

echo X(f,c,?) from in2.txt
java -jar ..\dist\LogicInference.jar in2.txt X(f,c,?)

echo X(x,y,z) from in2.txt
java -jar ..\dist\LogicInference.jar in2.txt X(x,y,z)

pause