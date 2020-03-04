# JeapHunter
A tool to detect various Java Exception Anit Patterns such as Nested Try, Destructive Wrapping, Over Catch


# How to Run

1. Eclipse IDE for Enterprise Java Developers
2. File -> Open project from directory
3. Project option -> Debug as Eclipse Application



### Exception Anti Patterns

##### 1. Nested Try

````java

try{
    
   foo();
   
  try{
     bar();
   }catch(BarException ex)

}catch(Exception ex){

}
````

- No need to go into method level( i.e. do not need to check try catch existence inside foo or bar


##### 2. Destructive Wrapping


````java
try{
  foo();
  
}catch(FileNotFoundException ex){
  throw new FileErrorException("Problem reading file");
 
}
````


3. Over Catch

````java

try{

  foo();

}catch(IOException ex){

}


foo () {
}

````

- **OVerCatch:** The handler catches multiple different lower-level exceptions
- foo() does not throw any IOException or their subclasses, So there is no need to catch the IOException. Therefore it's an overcatch.
- 
- Ideally we need to check the source code or signature inside foo() has thrown any exception that are subclasses of the same class of the handled exception
- The methods invoked inside foo() can also throw this exceptions. so we need to check that also.
- This could lead to many deep level checking. For this tool, we can put a thehsold of 4-5 levels,
- For now we should only checked

- UnChecked exception like NullPointerException might not have that throws declaration in the method signature

##### Test data
- Need to apply our tool on a Large (But not gigantice) project to show the results
- Just one project is fine
