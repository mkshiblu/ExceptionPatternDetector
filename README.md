# JeapHunter
A tool to detect various Java Exception Anit Patterns such as Nested Try, Destructive Wrapping, Over Catch


# How to Run

1. Eclipse IDE for Enterprise Java Developers
2. File -> Open project from directory
3. Project option -> Debug as Eclipse Application

> If you want to use thirparty library evaluation please use the code from thirdpartylibs branch. It makes it slower.

Without thirdpartylibs our tool runs on HibernateORM with more than 7000 java files and more than half a million code in 5 minutes with a cache size of 20 (Change the capacity of cache in ASTUtil.Cache class. In our findings it works better with cache size with ~50

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

#### OverCatch Result

Result from checkstyle (without thirdpartly libs evaluation (which makes it slower)

````

---------------OVER CATCH--------------
JavadocParser.java at Line:3497 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:18413 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:6183 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:10857 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:14257 col: 2
RecognitionException is catching NoViableAltException, 
InputEmptyCatchBlockDefaultLF.java at Line:25 col: 8
Exception is catching RuntimeException, 
JavadocParser.java at Line:1392 col: 2
RecognitionException is catching FailedPredicateException, 
GeneratedJavaRecognizer.java at Line:7429 col: 6
RecognitionException is catching MismatchedTokenException, 
JavadocParser.java at Line:11284 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:14566 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:17282 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:19364 col: 2
RecognitionException is catching NoViableAltException, 
InputEmptyCatchBlockDefault.java at Line:33 col: 8
Exception is catching RuntimeException, 
JavadocParser.java at Line:10078 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:281 col: 2
RecognitionException is catching FailedPredicateException, 
TranslationCheck.java at Line:567 col: 8
Exception is catching IOException, 
InputEmptyCatchBlockViolationsByComment.java at Line:17 col: 8
Exception is catching RuntimeException, 
JavadocParser.java at Line:9299 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:6637 col: 4
RecognitionException is catching MismatchedTokenException, 
JavadocParser.java at Line:13845 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:14772 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:1079 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaLexer.java at Line:1985 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
CommitValidationTest.java at Line:212 col: 8
IOException is catching MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException, 
JavadocParser.java at Line:6962 col: 2
RecognitionException is catching NoViableAltException, 
InputIndentationValidBlockIndent.java at Line:192 col: 26
Exception is catching IOException, 
PackageObjectFactory.java at Line:345 col: 8
ReflectiveOperationException is catching ClassNotFoundException, 
GeneratedJavaRecognizer.java at Line:1374 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
GeneratedJavaRecognizer.java at Line:4143 col: 3
RecognitionException is catching SemanticException, NoViableAltException, MismatchedTokenException, 
GeneratedJavaRecognizer.java at Line:5942 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:8520 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:17387 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:255 col: 2
RecognitionException is catching MismatchedTokenException, 
GeneratedJavaRecognizer.java at Line:3915 col: 5
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:1939 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:3070 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:12842 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:20390 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:4273 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:13742 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:16787 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:15086 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:8168 col: 2
RecognitionException is catching FailedPredicateException, 
GeneratedJavaRecognizer.java at Line:1972 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:7741 col: 2
RecognitionException is catching NoViableAltException, 
JavaParser.java at Line:98 col: 8
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
GeneratedJavaLexer.java at Line:1759 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
JavadocParser.java at Line:19072 col: 2
RecognitionException is catching FailedPredicateException, 
SuppressionsLoaderTest.java at Line:212 col: 8
IOException is catching MalformedURLException, 
JavadocParser.java at Line:17754 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:16870 col: 2
RecognitionException is catching NoViableAltException, 
InputIndentationTryWithResourcesStrict.java at Line:72 col: 8
Exception is catching NumberFormatException, 
JavadocParser.java at Line:14463 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:368 col: 2
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:1246 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:8947 col: 2
RecognitionException is catching FailedPredicateException, 
GeneratedJavaRecognizer.java at Line:4893 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:12415 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:292 col: 2
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:9726 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:18705 col: 2
RecognitionException is catching NoViableAltException, 
CheckUtil.java at Line:96 col: 8
Exception is catching ParserConfigurationException, SAXException, IOException, 
JavadocParser.java at Line:14360 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:17179 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:19731 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:14051 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaLexer.java at Line:2315 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
JavadocParser.java at Line:3849 col: 2
RecognitionException is catching NoViableAltException, 
InputEmptyCatchBlockViolationsByVariableName.java at Line:17 col: 8
Exception is catching RuntimeException, 
JavadocParser.java at Line:11636 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaLexer.java at Line:3032 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
GeneratedJavaLexer.java at Line:1828 col: 6
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
JavadocParser.java at Line:14986 col: 2
RecognitionException is catching NoViableAltException, FailedPredicateException, 
GeneratedJavaLexer.java at Line:1805 col: 5
RecognitionException is catching MismatchedCharException, 
JavadocParser.java at Line:18046 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaLexer.java at Line:138 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
JavadocParser.java at Line:12063 col: 2
RecognitionException is catching FailedPredicateException, 
InputEmptyCatchBlockViolationsByComment.java at Line:8 col: 8
Exception is catching RuntimeException, 
JavadocParser.java at Line:20023 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:14669 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:16973 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:13639 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:4493 col: 4
RecognitionException is catching MismatchedTokenException, 
GeneratedJavaRecognizer.java at Line:162 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:7389 col: 2
RecognitionException is catching FailedPredicateException, 
GeneratedJavaRecognizer.java at Line:5545 col: 5
RecognitionException is catching SemanticException, NoViableAltException, MismatchedTokenException, 
Checker.java at Line:280 col: 12
Exception is catching CheckstyleException, 
GeneratedJavaLexer.java at Line:1782 col: 4
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
InputEmptyCatchBlockViolationsByVariableName.java at Line:8 col: 8
Exception is catching RuntimeException, 
GeneratedJavaRecognizer.java at Line:5519 col: 4
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:15280 col: 2
RecognitionException is catching NoViableAltException, FailedPredicateException, 
GeneratedJavaRecognizer.java at Line:7355 col: 4
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
InputIndentationTryResourcesNotStrict.java at Line:72 col: 8
Exception is catching NumberFormatException, 
JavadocParser.java at Line:6610 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:4625 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:3893 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:5404 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:14154 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:2718 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:17076 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:5052 col: 2
RecognitionException is catching FailedPredicateException, 
JavadocParser.java at Line:10505 col: 2
RecognitionException is catching FailedPredicateException, 
GeneratedJavaLexer.java at Line:1920 col: 10
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
GeneratedJavaRecognizer.java at Line:2203 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:13433 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:2287 col: 4
RecognitionException is catching MismatchedTokenException, 
InputFinalLocalVariableAssignedMultipleTimes.java at Line:199 col: 8
Exception is catching IllegalStateException, IllegalArgumentException, 
GeneratedJavaRecognizer.java at Line:4539 col: 4
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
JavadocParser.java at Line:13536 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:1512 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:16267 col: 2
RecognitionException is catching NoViableAltException, 
JavadocParser.java at Line:2291 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaRecognizer.java at Line:7392 col: 5
RecognitionException is catching MismatchedTokenException, 
JavadocParser.java at Line:5831 col: 2
RecognitionException is catching FailedPredicateException, 
SuppressionFilterTest.java at Line:251 col: 8
IOException is catching MalformedURLException, 
JavadocParser.java at Line:13948 col: 2
RecognitionException is catching NoViableAltException, 
PackageObjectFactory.java at Line:355 col: 12
ReflectiveOperationException is catching IllegalAccessException, InvocationTargetException, InstantiationException, 
GeneratedJavaRecognizer.java at Line:5714 col: 3
RecognitionException is catching NoViableAltException, MismatchedTokenException, 
NewlineAtEndOfFileCheck.java at Line:137 col: 8
IOException is catching FileNotFoundException, 
InputEmptyCatchBlockDefaultLF.java at Line:33 col: 8
Exception is catching RuntimeException, 
GeneratedJavaLexer.java at Line:1897 col: 9
RecognitionException is catching MismatchedCharException, 
GeneratedJavaLexer.java at Line:2675 col: 3
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
JavadocParser.java at Line:16713 col: 2
RecognitionException is catching NoViableAltException, 
GeneratedJavaLexer.java at Line:1851 col: 7
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
GeneratedJavaLexer.java at Line:1874 col: 8
RecognitionException is catching MismatchedCharException, NoViableAltForCharException, 
InputEmptyCatchBlockDefault.java at Line:25 col: 8
Exception is catching RuntimeException, 
GeneratedJavaRecognizer.java at Line:2253 col: 4
RecognitionException is catching MismatchedTokenException, 
`````



