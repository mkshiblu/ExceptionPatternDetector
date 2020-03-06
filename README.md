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


foo(){
  try{
    bar();

  }catch(IOException ex){

  }catch(Exception ex){
}

  void bar() throws FileNotFoundException {
  }
}

````

- **OVerCatch:** The handler catches multiple different lower-level exceptions
- bar() throws FileNotFoundException  So there is no need to catch the IOException in foo()
- So foo's IOException ex clause is an overcatch but not the Exception ex clause
- UnChecked exception like NullPointerException might not have that throws declaration in the method signature

##### Limitations of JeapHunter

**Nested Try**
- Does not consider library methods

**Destructive Wrapping**
- Does not consider library methods
- Only handles statements like throw new(....)


**OverCatch**
- Does not consider the declaration/ methods/ javadocs from third partly library, however there is a seprate code branch in our repo where when the source code of tha third party library is attached, it it can treat them as normal code (I.e. it works with source attachment)
- Does not go inside code inside of a ClassInstanceCreation (i.e. does not consider code which is inside constructor)



##### Edge cases handled by our OverCatch

**1. Handling recursive calls**

````java
	public void recursive() throws FileNotFoundException{
		try {
			if (21 > 3) {
				recursive();
			}
			throw new NullPointerException();
		} catch (IOException ex) {

		}
	}
````

This is an overcatch since the method recursive calls it self which throws FileNotFoundException and the catch block is IOException


**2. OverCatches for multiple catch blocks in the same hierarchy**

`````java
public void foo() {
	try {

	if (332 / 32 == 3)
		throw new FileNotFoundException();

	if (332 / 32 == 3)
		throw new EOFException();

	if (332 / 32 == 3)
		throw new IOException();
	} catch (FileNotFoundException iox) {

	} catch (IOException iox) {

	} catch (Exception ex) {

	}
	throw new NullPointerException();
}
`````


- FNEX is not an overcatch since there is no subclass thrown
- IOException is an overcatch since it handles EOFException 
- Exception is not an overcatch since IOX and FNEX already handles all the throw statements


**3. Similar Exception thrown from the invoked method**

````java

public void foo() {
	
	try {
	bar();
	
	}
	catch(RunTimeException ex) {
		
	}
}

public void bar() {
	....
		throw new NullPointerException();		
	try {
		
		throw new NullPointerException();
	}catch(NullPointerException ex){
		
	}
}
`````

- bar is not an overcatch. but foo is an overcatch since it handles the NPE not declared in the signature by RTE

**4. Catch with multiple Exception Types**

PackageObjectFactory.java from the CheckStyle project

````Java
private Object createObject(String className) throws CheckstyleException {
	Class<?> clazz = null;

	try {
		clazz = Class.forName(className, true, moduleClassLoader);
	}
	catch (final ReflectiveOperationException | NoClassDefFoundError ignored) {
		// ignore the exception
	}

	Object instance = null;

	if (clazz != null) {
		try {
			instance = clazz.getDeclaredConstructor().newInstance();
		}
		catch (final ReflectiveOperationException ex) {
			throw new CheckstyleException("Unable to instantiate " + className, ex);
		}
	}

	return instance;
}
`````


